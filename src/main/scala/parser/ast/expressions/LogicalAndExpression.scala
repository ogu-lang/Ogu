package parser.ast.expressions

import lexer.AND
import parser.Expression

case class LogicalAndExpression(args: List[Expression]) extends LogicalExpression(args)

object LogicalAndExpression extends LeftAssociativeExpressionParser(ComparativeExpression, AND) {

  override def build(args: List[Expression]): Expression = LogicalAndExpression(args)

}