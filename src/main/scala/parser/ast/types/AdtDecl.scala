package parser.ast.types

import lexer._
import parser.LangNode

import scala.annotation.tailrec

case class AdtDecl(name: String, defs: List[ADT]) extends LangNode

object AdtDecl {

  def parse(inner: Boolean, tokens: TokenStream) : AdtDecl = {
    tokens.consume(DATA)
    val id = tokens.consume(classOf[TID]).value
    tokens.consume(ASSIGN)
    val initialIndents = if (!tokens.peek(NL)) { 0 } else {
      tokens.consumeOptionals(NL)
      tokens.consume(INDENT)
      1
    }
    val (adts, indents) = consumeADTs(tokens, initialIndents, List(ADT.parse(tokens)))
    tokens.consume(indents, DEDENT)
    AdtDecl(id, adts)
  }

  @tailrec
  private def consumeADTs(tokens: TokenStream, indents: Int, adts: List[ADT]) : (List[ADT], Int) = {
    if (!tokens.peek(GUARD)) {
      (adts.reverse, indents)
    }
    else {
      tokens.consume(GUARD)
      tokens.consumeOptionals(NL)
      if (!tokens.peek(INDENT)) {
        consumeADTs(tokens, indents, ADT.parse(tokens) :: adts)
      } else {
        tokens.consume(INDENT)
        consumeADTs(tokens, indents+1, ADT.parse(tokens) :: adts)
      }
    }
  }


}
