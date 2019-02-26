package parser.ast.expressions.list_ops

import lexer.PLUS_PLUS
import parser.ast.expressions.{Expression, LeftAssociativeExpressionParser}
import parser.ast.expressions.arithmetic.MultiplyExpression

case class ConcatExpression(args: List[Expression]) extends Expression

object ConcatExpression extends LeftAssociativeExpressionParser(MultiplyExpression, PLUS_PLUS) {

  override def build(args: List[Expression]): Expression = ConcatExpression(args)
}
