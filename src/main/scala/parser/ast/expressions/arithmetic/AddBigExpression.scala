package parser.ast.expressions.arithmetic

import lexer.PLUSBIG
import parser.ast.expressions.{ArithmeticExpression, Expression, LeftAssociativeExpressionParser}

case class AddBigExpression(args: List[Expression]) extends ArithmeticExpression

object AddBigExpression extends LeftAssociativeExpressionParser(SubstractExpression, PLUSBIG) {

  override def build(args: List[Expression]): Expression = AddBigExpression(args)

}
