package parser.ast.types

import lexer._
import parser.ast._
import parser.LangNode


case class RecordDecl(name: String, args: List[String]) extends LangNode

object RecordDecl {

  def parse(inner: Boolean, tokens:TokenStream): RecordDecl = {
    tokens.consume(RECORD)
    val name = tokens.consume(classOf[TID]).value
    tokens.consume(LCURLY)
    val args = consumeListOfIdsSepByComma(tokens)
    tokens.consume(RCURLY)
    RecordDecl(name, args)
  }
}
