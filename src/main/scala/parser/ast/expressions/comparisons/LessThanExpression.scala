package parser.ast.expressions.comparisons

import lexer.LT
import parser.ast.expressions.{Expression, LeftAssociativeExpressionParser}

case class LessThanExpression(args: List[Expression]) extends ComparativeExpression(args)

object LessThanExpression extends LeftAssociativeExpressionParser(LessOrEqualThanExpression, LT) {

  override def build(args: List[Expression]): Expression = LessThanExpression(args)

}
