package parser.ast.expressions.comparisons

import lexer.GT
import parser.ast.expressions.{Expression, LeftAssociativeExpressionParser}

case class GreaterThanExpression(args: List[Expression]) extends ComparativeExpression(args)

object GreaterThanExpression extends LeftAssociativeExpressionParser(GreaterOrEqualThanExpression, GT) {

  override def build(args: List[Expression]): Expression = GreaterThanExpression(args)

}
