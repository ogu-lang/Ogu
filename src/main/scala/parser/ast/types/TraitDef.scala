package parser.ast.types

import lexer._
import parser.ast.module.Module
import parser.{ClassMethodDecl, LangNode}

import scala.annotation.tailrec

case class TraitDef(traitName: String, methods: List[ClassMethodDecl]) extends LangNode

object TraitDef {

  def parse(tokens: TokenStream): List[TraitDef] = {
    if (tokens.peek(INDENT)) {
      tokens.consume(INDENT)
      val traits = consumeTraitDefs(tokens, List.empty)
      tokens.consume(DEDENT)
      traits
    } else {
      List.empty
    }
  }

  @tailrec
  private def consumeTraitDefs(tokens: TokenStream, traits: List[TraitDef]) : List[TraitDef]  = {
    if (tokens.peek(DEDENT)) {
      traits.reverse
    } else {
      tokens.consume(EXTENDS)
      val traitName = tokens.consume(classOf[TID]).value
      tokens.consumeOptionals(NL)
      tokens.consume(INDENT)
      val traitMethods = consumeClassMethodDecls(tokens, List.empty)
      tokens.consume(DEDENT)
      consumeTraitDefs(tokens, TraitDef(traitName, traitMethods) :: traits)
    }
  }

  @tailrec
  private def consumeClassMethodDecls(tokens: TokenStream, methods: List[ClassMethodDecl]) : List[ClassMethodDecl] = {
    if (!tokens.peek(DEF)) {
      methods.reverse
    } else {
      val defDecl = Module.parseDef(false, tokens)
      tokens.consumeOptionals(NL)
      consumeClassMethodDecls(tokens, ClassMethodDecl(defDecl) :: methods)
    }
  }

}
