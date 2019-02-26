package parser.ast.expressions.arithmetic

import lexer.PLUS
import parser.ast.expressions.{Expression, LeftAssociativeExpressionParser}

case class AddExpression(args: List[Expression]) extends Expression

object AddExpression extends LeftAssociativeExpressionParser(AddBigExpression, PLUS) {

  override def build(args: List[Expression]): Expression = AddExpression(args)

}
