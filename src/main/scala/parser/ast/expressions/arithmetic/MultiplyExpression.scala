package parser.ast.expressions.arithmetic

import lexer.MULT
import parser.ast.expressions.{Expression, LeftAssociativeExpressionParser}

case class MultiplyExpression(args: List[Expression]) extends Expression

object MultiplyExpression extends LeftAssociativeExpressionParser(MultiplyBigExpression, MULT) {

  override def build(args: List[Expression]): Expression = MultiplyExpression(args)

}