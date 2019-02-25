package parser.ast.expressions

import lexer.MINUS
import parser.Expression

case class SubstractExpression(args: List[Expression]) extends Expression

object SubstractExpression extends LeftAssociativeExpressionParser(SubstractBigExpression, MINUS) {

  override def build(args: List[Expression]): Expression = SubstractExpression(args)

}
