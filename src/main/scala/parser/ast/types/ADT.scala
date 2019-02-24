package parser.ast.types

import lexer._
import parser.ast._

case class ADT(name: String, args: List[String])

object ADT {

  def parse(tokens:TokenStream): ADT = {
    val id = tokens.consume(classOf[TID]).value
    val args = if (tokens.peek(LPAREN)) {
      tokens.consume(LPAREN)
      val ids = consumeListOfIdsSepByComma(tokens, List(tokens.consume(classOf[ID]).value))
      tokens.consume(RPAREN)
      ids
    } else {
      List.empty
    }
    ADT(id, args)
  }

}
