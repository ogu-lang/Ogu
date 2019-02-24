package parser

import lexer.{COMMA, ID, TokenStream}

import scala.annotation.tailrec

package object ast {

  def consumeListOfIdsSepByComma(tokens: TokenStream) : List[String] = {
    consumeListOfIdsSepByComma(tokens, List(tokens.consume(classOf[ID]).value))
  }

  @tailrec
  def consumeListOfIdsSepByComma(tokens: TokenStream, ids: List[String]) : List[String] = {
    if (!tokens.peek(COMMA)) {
      ids.reverse
    } else {
      tokens.consume(COMMA)
      val id = tokens.consume(classOf[ID]).value
      consumeListOfIdsSepByComma(tokens, id:: ids)
    }
  }

  @tailrec
  def consumeListOfIdsSepByBlank(tokens: TokenStream, args: List[String]) :List[String] = {
    if (!tokens.peek(classOf[ID])) {
      args.reverse
    } else {
      consumeListOfIdsSepByBlank(tokens, tokens.consume(classOf[ID]).value :: args)
    }
  }
}
