package parser.ast.expressions.logical

import lexer.AND
import parser.ast.expressions.comparisons.ComparativeExpression
import parser.ast.expressions.{Expression, LeftAssociativeExpressionParser}

case class LogicalAndExpression(args: List[Expression]) extends LogicalExpression(args)

object LogicalAndExpression extends LeftAssociativeExpressionParser(ComparativeExpression, AND) {

  override def build(args: List[Expression]): Expression = LogicalAndExpression(args)

}