package parser.ast.types

import lexer._
import parser.ast._


case class RecordDecl(name: String, args: List[String], traits: List[TraitDef]) extends LangNode

object RecordDecl {

  def parse(inner: Boolean, tokens:TokenStream): RecordDecl = {
    tokens.consume(RECORD)
    val name = tokens.consume(classOf[TID]).value
    tokens.consume(LCURLY)
    val args = consumeListOfIdsSepByComma(tokens)
    tokens.consume(RCURLY)
    tokens.consumeOptionals(NL)
    RecordDecl(name, args, TraitDef.parse(tokens))
  }
}
