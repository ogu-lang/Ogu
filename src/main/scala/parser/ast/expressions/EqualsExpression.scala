package parser.ast.expressions

import lexer.EQUALS
import parser.Expression

case class EqualsExpression(args: List[Expression]) extends ComparativeExpression(args)

object EqualsExpression extends LeftAssociativeExpressionParser(NotEqualsExpression, EQUALS) {

  override def build(args: List[Expression]): Expression = EqualsExpression(args)

}
