package parser.ast.expressions.comparisons

import lexer.GE
import parser.ast.expressions.{Expression, LeftAssociativeExpressionParser}

case class GreaterOrEqualThanExpression(args: List[Expression]) extends ComparativeExpression(args)

object GreaterOrEqualThanExpression extends LeftAssociativeExpressionParser(EqualsExpression, GE) {

  override def build(args: List[Expression]): Expression = GreaterOrEqualThanExpression(args)

}
