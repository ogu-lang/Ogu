package parser.ast.types

import lexer._
import parser.LangNode

case class AdtDecl(name: String, defs: List[ADT]) extends LangNode

object AdtDecl {

  def parse(inner: Boolean, tokens: TokenStream) : AdtDecl = {
    tokens.consume(DATA)
    val id = tokens.consume(classOf[TID]).value
    tokens.consume(ASSIGN)
    var indents = 0
    if (tokens.peek(NL)) {
      tokens.consumeOptionals(NL)
      tokens.consume(INDENT)
      indents += 1
    }
    var adts = List.empty[ADT]
    val adt = ADT.parse(tokens)
    adts = adt :: adts
    while (tokens.peek(GUARD)) {
      tokens.consume(GUARD)
      tokens.consumeOptionals(NL)
      if (tokens.peek(INDENT)) {
        tokens.consume(INDENT)
        indents += 1
      }
      val adt = ADT.parse(tokens)
      adts = adt :: adts
    }
    while (indents > 0) {
      tokens.consume(DEDENT)
      indents -= 1
    }
    AdtDecl(id, adts.reverse)
  }

  private def consumeADTs(tokens: TokenStream, adts: List[ADT]) : List[ADT] = {
    ???
  }

}
