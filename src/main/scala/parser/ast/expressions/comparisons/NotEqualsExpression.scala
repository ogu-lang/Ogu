package parser.ast.expressions.comparisons

import lexer.NOTEQUALS
import parser.ast.expressions.list_ops.ContainsExpr
import parser.ast.expressions.{Expression, LeftAssociativeExpressionParser}

case class NotEqualsExpression(args: List[Expression]) extends ComparativeExpression(args)

object NotEqualsExpression extends LeftAssociativeExpressionParser(ContainsExpr, NOTEQUALS) {

  override def build(args: List[Expression]): Expression = NotEqualsExpression(args)

}
