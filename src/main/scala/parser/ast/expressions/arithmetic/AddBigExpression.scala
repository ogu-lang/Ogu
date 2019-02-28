package parser.ast.expressions.arithmetic

import lexer.PLUS_BIG
import parser.ast.expressions.{ArithmeticExpression, Expression, LeftAssociativeExpressionParser}

case class AddBigExpression(args: List[Expression]) extends ArithmeticExpression

object AddBigExpression extends LeftAssociativeExpressionParser(SubstractExpression, PLUS_BIG) {

  override def build(args: List[Expression]): Expression = AddBigExpression(args)

}
