package parser.ast.expressions

import lexer.PLUS
import parser.Expression

case class AddExpression(args: List[Expression]) extends Expression

object AddExpression extends LeftAssociativeExpressionParser(AddBigExpression, PLUS) {

  override def build(args: List[Expression]): Expression = AddExpression(args)

}
