package parser.ast.module

import lexer._
import parser._
import parser.ast._
import parser.ast.expressions._
import parser.ast.expressions.functions.{ForwardPipeFuncCallExpression, FunctionCallExpression, FunctionCallWithDollarExpression, LambdaExpression}
import parser.ast.expressions.literals.Atom
import parser.ast.expressions.logical.LogicalExpression
import parser.ast.decls._
import parser.ast.types._

import scala.annotation.tailrec
import scala.collection.mutable

case class Module(name: String, imports: Option[List[ImportClause]],decls: List[LangNode]) extends LangNode

object Module  {

  def parse(tokens: TokenStream, nameFromFile: String): Module = {
    val moduleName = if (!tokens.peek(MODULE)) {
      nameFromFile
    } else {
      tokens.consume(MODULE)
      if (tokens.peek(classOf[TID])) tokens.consume(classOf[TID]).value else tokens.consume(classOf[ID]).value
    }
    parseModule(moduleName, tokens)
  }

  private[this] def parseModule(moduleName: String, tokens: TokenStream) : Module = {
    tokens.consumeOptionals(NL)
    val defs = mutable.HashMap.empty[String, MultiDefDecl]
    Module(moduleName, ImportClause.parse(tokens), parseModuleNodes(tokens))
  }

  private[this] def parseModuleNodes(tokens:TokenStream): List[LangNode] = {
    filter(parseModuleNodes(tokens, Nil, Map.empty[String, MultiDefDecl]))
  }

  @tailrec
  private[this]
  def parseModuleNodes(tokens: TokenStream, nodes: List[LangNode], defs: Map[String, MultiDefDecl]): (Map[String, MultiDefDecl], List[LangNode]) = {
    if (tokens.isEmpty) {
      (defs, nodes.reverse)
    }
    else {
      val inner = if (!tokens.peek(PRIVATE)) false else { tokens.consume(PRIVATE); true }
      val (newDefs, newNodes) = tokens.nextToken() match {
        case None =>  (defs, nodes)
        case Some(token) =>
          token match {
            case CLASS => (defs, ClassDecl.parse(inner, tokens) :: nodes)
            case DATA => (defs, AdtDecl.parse(inner, tokens) :: nodes)
            case DEF =>
              val (ndefs, node) = MultiDefDecl.parse(inner, tokens, defs)
              (ndefs, node :: nodes)
            case DISPATCH => (defs, DispatchDecl.parse(inner, tokens) :: nodes)
            case EXTENDS => (defs, ExtendsDecl.parse(inner, tokens) :: nodes)
            case RECORD => (defs, RecordDecl.parse(inner, tokens) :: nodes)
            case TRAIT => (defs, TraitDecl.parse(inner, tokens) :: nodes)
            case _ => (defs, TopLevelExpression.parse(tokens) :: nodes)
          }
      }
      tokens.consumeOptionals(NL)
      parseModuleNodes(tokens, newNodes, newDefs)
    }
  }


  private[this] def filter(data: (Map[String, MultiDefDecl], List[LangNode])): List[LangNode] = {
    var result = List.empty[LangNode]
    val (defs, nodes) = data
    filter(nodes, Nil, defs)
  }

  private[this]
  def filter(nodes: List[LangNode], result: List[LangNode], defs: Map[String, MultiDefDecl]) : List[LangNode] = {
    if (nodes.isEmpty) {
      result.reverse
    }
    else {
      val node = nodes.head
      node match {
        case md: MultiDefDecl =>
          defs.get(md.id) match {
            case None => filter(nodes.tail, result, defs)
            case Some(md) =>
              val multiDef = MultiDefDecl(md.id, md.decls.reverse)
              if (multiDef.decls.length == 1)
                filter(nodes.tail, multiDef.decls.head :: result, defs - md.id)
              else
                filter(nodes.tail, multiDef :: result, defs - md.id)
          }
        case _ =>
          filter(nodes.tail, node :: result, defs)
      }
    }
  }





}
