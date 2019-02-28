package parser.ast.expressions.arithmetic

import lexer.MINUS_BIG
import parser.ast.expressions.list_ops.ConcatExpression
import parser.ast.expressions.{ArithmeticExpression, Expression, LeftAssociativeExpressionParser}

case class SubstractBigExpression(args: List[Expression]) extends ArithmeticExpression

object SubstractBigExpression extends LeftAssociativeExpressionParser(ConcatExpression, MINUS_BIG) {

  override def build(args: List[Expression]): Expression = SubstractBigExpression(args)

}


