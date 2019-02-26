package parser.ast.expressions.literals

import lexer.{ATOM, TokenStream}
import parser.ast.expressions.{Expression, ExpressionParser}

case class Atom(value: String) extends Expression

object Atom extends ExpressionParser {

  override def parse(tokens: TokenStream): Expression = {
    Atom(tokens.consume(classOf[ATOM]).value)
  }
}