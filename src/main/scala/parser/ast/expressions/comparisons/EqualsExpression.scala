package parser.ast.expressions.comparisons

import lexer.EQUALS
import parser.ast.expressions.{Expression, LeftAssociativeExpressionParser}

case class EqualsExpression(args: List[Expression]) extends ComparativeExpression(args)

object EqualsExpression extends LeftAssociativeExpressionParser(NotEqualsExpression, EQUALS) {

  override def build(args: List[Expression]): Expression = EqualsExpression(args)

}
