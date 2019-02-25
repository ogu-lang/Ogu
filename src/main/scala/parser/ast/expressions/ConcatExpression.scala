package parser.ast.expressions

import lexer.PLUS_PLUS
import parser.Expression

case class ConcatExpression(args: List[Expression]) extends Expression

object ConcatExpression extends LeftAssociativeExpressionParser(MultiplyExpression, PLUS_PLUS) {

  override def build(args: List[Expression]): Expression = ConcatExpression(args)
}
