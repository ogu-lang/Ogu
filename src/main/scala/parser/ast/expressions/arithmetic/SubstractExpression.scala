package parser.ast.expressions.arithmetic

import lexer.MINUS
import parser.ast.expressions.{ArithmeticExpression, Expression, LeftAssociativeExpressionParser}

case class SubstractExpression(args: List[Expression]) extends ArithmeticExpression

object SubstractExpression extends LeftAssociativeExpressionParser(SubstractBigExpression, MINUS) {

  override def build(args: List[Expression]): Expression = SubstractExpression(args)

}
