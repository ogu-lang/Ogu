package parser.ast.expressions

import lexer.NOT_EQUALS
import parser.Expression

case class NotEqualsExpression(args: List[Expression]) extends ComparativeExpression(args)

object NotEqualsExpression extends LeftAssociativeExpressionParser(ContainsExpr, NOT_EQUALS) {

  override def build(args: List[Expression]): Expression = NotEqualsExpression(args)

}
