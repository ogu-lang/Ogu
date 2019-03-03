package parser.ast.module

import lexer._

import scala.annotation.tailrec

sealed trait ImportAlias
object ImportAll extends ImportAlias
case class ImportSimple(name:String) extends ImportAlias
case class ImportRename(name:String, alias:String) extends ImportAlias

object ImportAlias {

  def parse(tokens:TokenStream): ImportAlias = {
    if (tokens.peek(MULT)) {
      tokens.consume(MULT)
      ImportAll
    }
    else {
      val id = if (tokens.peek(classOf[TID])) tokens.consume(classOf[TID]).value else tokens.consume(classOf[ID]).value
      if (!tokens.peek(AS)) {
        ImportSimple(id)
      } else {
        tokens.consume(AS)
        ImportRename(id, tokens.consume(classOf[ID]).value)
      }
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
