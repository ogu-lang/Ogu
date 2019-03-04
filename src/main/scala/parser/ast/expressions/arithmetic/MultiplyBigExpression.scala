package parser.ast.expressions.arithmetic

import lexer.MULTBIG
import parser.ast.expressions.{ArithmeticExpression, Expression, LeftAssociativeExpressionParser}

case class MultiplyBigExpression(args: List[Expression]) extends ArithmeticExpression

object MultiplyBigExpression extends LeftAssociativeExpressionParser(DivideExpression, MULTBIG) {

  override def build(args: List[Expression]): Expression = MultiplyBigExpression(args)

}
