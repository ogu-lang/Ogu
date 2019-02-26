package parser.ast.expressions.list_ops

import lexer.{CONS, NL, TokenStream}
import parser.ast.expressions.arithmetic.AddExpression
import parser.ast.expressions.{Expression, RightAssociativeExpressionParser}

case class ConsExpression(args: List[Expression]) extends Expression

object ConsExpression extends RightAssociativeExpressionParser(AddExpression, CONS) {

  override def build(args: List[Expression]): Expression = ConsExpression(args)

  override def consumeOper(tokens: TokenStream): Unit = {
    tokens.consume(CONS)
    tokens.consumeOptionals(NL)
  }
}
