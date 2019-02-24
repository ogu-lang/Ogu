package parser.ast.types

import lexer._

import scala.annotation.tailrec

case class ADT(name: String, args: List[String])

object ADT {

  def parse(tokens:TokenStream): ADT = {
    val id = tokens.consume(classOf[TID]).value
    val args = if (tokens.peek(LPAREN)) {
      tokens.consume(LPAREN)
      val ids = consumeListOfIds(tokens, List(tokens.consume(classOf[ID]).value))
      tokens.consume(RPAREN)
      ids
    } else {
      List.empty
    }
    ADT(id, args)
  }

  private def consumeListOfIds(tokens: TokenStream, ids: List[String]) : List[String] = {
    if (!tokens.peek(COMMA)) {
      ids.reverse
    } else {
      tokens.consume(COMMA)
      val id = tokens.consume(classOf[ID]).value
      consumeListOfIds(tokens, id:: ids)
    }
  }

}
