package parser.ast.expressions.arithmetic

import lexer.MULT_BIG
import parser.ast.expressions.{ArithmeticExpression, Expression, LeftAssociativeExpressionParser}

case class MultiplyBigExpression(args: List[Expression]) extends ArithmeticExpression

object MultiplyBigExpression extends LeftAssociativeExpressionParser(DivideExpression, MULT_BIG) {

  override def build(args: List[Expression]): Expression = MultiplyBigExpression(args)

}
