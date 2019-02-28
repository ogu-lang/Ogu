package parser.ast.expressions.logical

import lexer.OR
import parser.ast.expressions.{Expression, LeftAssociativeExpressionParser}

case class LogicalOrExpression(args: List[Expression]) extends LogicalExpression(args)

object LogicalOrExpression extends LeftAssociativeExpressionParser(LogicalAndExpression, OR)  {

  override def build(args: List[Expression]): Expression = LogicalOrExpression(args)

}
