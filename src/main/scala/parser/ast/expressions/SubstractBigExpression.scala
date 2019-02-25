package parser.ast.expressions

import lexer.MINUS_BIG
import parser.Expression

case class SubstractBigExpression(args: List[Expression]) extends Expression

object SubstractBigExpression extends LeftAssociativeExpressionParser(ConcatExpression, MINUS_BIG) {

  override def build(args: List[Expression]): Expression = SubstractBigExpression(args)

}


