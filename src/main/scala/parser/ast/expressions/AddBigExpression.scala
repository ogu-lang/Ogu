package parser.ast.expressions

import lexer.PLUS_BIG
import parser.Expression

case class AddBigExpression(args: List[Expression]) extends Expression

object AddBigExpression extends LeftAssociativeExpressionParser(SubstractExpression, PLUS_BIG) {

  override def build(args: List[Expression]): Expression = AddBigExpression(args)

}
