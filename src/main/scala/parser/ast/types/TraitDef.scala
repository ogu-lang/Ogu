package parser.ast.types

import lexer._
import parser.ast.LangNode
import parser.ast.decls.ClassMethodDecl

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
      val traitMethods = ClassMethodDecl.consumeClassMethodDecls(tokens, List.empty)
      tokens.consume(DEDENT)
      consumeTraitDefs(tokens, TraitDef(traitName, traitMethods) :: traits)
    }
  }



}
