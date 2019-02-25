package parser.ast.expressions

import lexer.{CONS, NL, TokenStream}
import parser.Expression

case class ConsExpression(args: List[Expression]) extends Expression

object ConsExpression extends RightAssociativeExpressionParser(AddExpression, CONS) {

  override def build(args: List[Expression]): Expression = ConsExpression(args)

  override def consumeOper(tokens: TokenStream): Unit = {
    tokens.consume(CONS)
    tokens.consumeOptionals(NL)
  }
}
