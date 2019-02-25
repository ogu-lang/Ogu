package parser.ast.expressions

import lexer.GE
import parser.Expression

case class GreaterOrEqualThanExpression(args: List[Expression]) extends ComparativeExpression(args)

object GreaterOrEqualThanExpression extends LeftAssociativeExpressionParser(EqualsExpression, GE) {

  override def build(args: List[Expression]): Expression = GreaterOrEqualThanExpression(args)

}
