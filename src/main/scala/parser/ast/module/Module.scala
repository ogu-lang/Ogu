package parser.ast.module

import lexer._
import parser.ast._
import parser.ast.decls._
import parser.ast.expressions._
import parser.ast.types._

import scala.annotation.tailrec

case class Module(name: String, imports: Option[List[ImportClause]],decls: List[LangNode]) extends LangNode

object Module {

  def parse(tokens: TokenStream, nameFromFile: String): Module = {
    val moduleName = if (!tokens.peek(MODULE)) {
      nameFromFile
    } else {
      tokens.consume(MODULE)
      if (tokens.peek(classOf[TID])) tokens.consume(classOf[TID]).value else tokens.consume(classOf[ID]).value
    }
    parse(moduleName, tokens)
  }

  private[this] def parse(moduleName: String, tokens: TokenStream): Module = {
    tokens.consumeOptionals(NL)
    Module(moduleName, ImportClause.parse(tokens), parseModuleNodes(tokens))
  }

  private[this] def parseModuleNodes(tokens: TokenStream): List[LangNode] = {
    filter(parseModuleNodes(tokens, Nil, Map.empty[String, MultiDefDecl]))
  }

  @tailrec
  private[this]
  def parseModuleNodes(tokens: TokenStream, nodes: List[LangNode], defs: Map[String, MultiDefDecl]): (Map[String, MultiDefDecl], List[LangNode]) = {
    if (tokens.isEmpty) {
      (defs, nodes.reverse)
    }
    else {
      val inner = if (!tokens.peek(PRIVATE)) false else {
        tokens.consume(PRIVATE); true
      }
      val (newDefs, newNodes) = tokens.nextSymbol() match {
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
      tokens.consumeOptionals(NL)
      parseModuleNodes(tokens, newNodes, newDefs)
    }
  }


  private[this] def filter(data: (Map[String, MultiDefDecl], List[LangNode])): List[LangNode] = {
    val (defs, nodes) = data
    filter(nodes, Nil, defs)
  }

  private[this]
  def filter(nodes: List[LangNode], result: List[LangNode], defs: Map[String, MultiDefDecl]): List[LangNode] = {
    nodes.headOption match {
      case None => result.reverse
      case Some(node) =>
        node match {
          case MultiDefDecl(mdId, _) =>
            defs.get(mdId) match {
              case None => filter(nodes.tail, result, defs)
              case Some(MultiDefDecl(id, decls)) =>
                val multiDef = MultiDefDecl(id, decls.reverse)
                multiDef.decls match {
                  case List(decl) => filter(nodes.tail, decl :: result, defs - id)
                  case _ => filter(nodes.tail, multiDef :: result, defs - id)
                }
            }
          case _ =>
            filter(nodes.tail, node :: result, defs)
        }
    }
  }
}
