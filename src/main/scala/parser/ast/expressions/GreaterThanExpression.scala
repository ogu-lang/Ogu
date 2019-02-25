package parser.ast.expressions

import lexer.GT
import parser.Expression

case class GreaterThanExpression(args: List[Expression]) extends ComparativeExpression(args)

object GreaterThanExpression extends LeftAssociativeExpressionParser(GreaterOrEqualThanExpression, GT) {

  override def build(args: List[Expression]): Expression = GreaterThanExpression(args)

}
