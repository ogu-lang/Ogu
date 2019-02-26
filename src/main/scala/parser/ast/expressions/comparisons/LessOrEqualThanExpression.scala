package parser.ast.expressions.comparisons

import lexer.LE
import parser.ast.expressions.{Expression, LeftAssociativeExpressionParser}

case class LessOrEqualThanExpression(args: List[Expression]) extends ComparativeExpression(args)

object LessOrEqualThanExpression extends LeftAssociativeExpressionParser(GreaterThanExpression, LE) {

  override def build(args: List[Expression]): Expression = LessOrEqualThanExpression(args)

}
