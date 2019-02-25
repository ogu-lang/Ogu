package parser.ast.expressions

import lexer.LE
import parser.Expression

case class LessOrEqualThanExpression(args: List[Expression]) extends ComparativeExpression(args)

object LessOrEqualThanExpression extends LeftAssociativeExpressionParser(GreaterThanExpression, LE) {

  override def build(args: List[Expression]): Expression = LessOrEqualThanExpression(args)

}
