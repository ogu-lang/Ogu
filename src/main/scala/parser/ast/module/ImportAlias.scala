package parser.ast.module

import lexer._

import scala.annotation.tailrec

case class ImportAlias(name:String, alias:Option[String])

object ImportAlias {

  def parse(tokens:TokenStream): ImportAlias = {
    val id = if (tokens.peek(classOf[TID])) tokens.consume(classOf[TID]).value else tokens.consume(classOf[ID]).value
    if (!tokens.peek(AS)) {
      ImportAlias(id, None)
    } else {
      tokens.consume(AS)
      ImportAlias(id, Some(tokens.consume(classOf[ID]).value))
    }
  }

  def parseListOfAlias(tokens:TokenStream) : List[ImportAlias] = {
    consumeListOfAlias(tokens, List(parse(tokens)))
  }

  @tailrec
  private[this] def consumeListOfAlias(tokens: TokenStream, list: List[ImportAlias]) : List[ImportAlias] = {
    if (!tokens.peek(COMMA)) {
      list.reverse
    }
    else {
      tokens.consume(COMMA)
      tokens.consumeOptionals(NL)
      val impAlias = ImportAlias.parse(tokens)
      consumeListOfAlias(tokens, impAlias :: list)
    }
  }
}
