package parser.ast.expressions

import lexer.{ATOM, TokenStream}
import parser.Expression

case class Atom(value: String) extends Expression

object Atom extends ExpressionParser {

  override def parse(tokens: TokenStream): Expression = {
    Atom(tokens.consume(classOf[ATOM]).value)
  }
}