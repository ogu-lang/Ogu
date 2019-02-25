package parser.ast.expressions

import lexer.MULT_BIG
import parser.Expression

case class MultiplyBigExpression(args: List[Expression]) extends Expression

object MultiplyBigExpression extends LeftAssociativeExpressionParser(DivideExpression, MULT_BIG) {

  override def build(args: List[Expression]): Expression = MultiplyBigExpression(args)

}
