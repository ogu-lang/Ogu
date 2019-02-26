package parser.ast.expressions.arithmetic

import lexer.MINUS
import parser.ast.expressions.{Expression, LeftAssociativeExpressionParser}

case class SubstractExpression(args: List[Expression]) extends Expression

object SubstractExpression extends LeftAssociativeExpressionParser(SubstractBigExpression, MINUS) {

  override def build(args: List[Expression]): Expression = SubstractExpression(args)

}
