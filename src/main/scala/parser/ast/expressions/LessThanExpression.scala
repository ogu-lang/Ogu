package parser.ast.expressions

import lexer.LT
import parser.Expression

case class LessThanExpression(args: List[Expression]) extends ComparativeExpression(args)

object LessThanExpression extends LeftAssociativeExpressionParser(LessOrEqualThanExpression, LT) {

  override def build(args: List[Expression]): Expression = LessThanExpression(args)

}
