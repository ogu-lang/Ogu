package parser.ast.expressions.arithmetic

import lexer.MULT_BIG
import parser.ast.expressions.{Expression, LeftAssociativeExpressionParser}

case class MultiplyBigExpression(args: List[Expression]) extends Expression

object MultiplyBigExpression extends LeftAssociativeExpressionParser(DivideExpression, MULT_BIG) {

  override def build(args: List[Expression]): Expression = MultiplyBigExpression(args)

}
