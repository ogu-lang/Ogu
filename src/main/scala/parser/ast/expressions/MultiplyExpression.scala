package parser.ast.expressions

import lexer.MULT
import parser.Expression

case class MultiplyExpression(args: List[Expression]) extends Expression

object MultiplyExpression extends LeftAssociativeExpressionParser(MultiplyBigExpression, MULT) {

  override def build(args: List[Expression]): Expression = MultiplyExpression(args)

}