package parser.ast.expressions

import lexer.OR
import parser.Expression

case class LogicalOrExpression(args: List[Expression]) extends LogicalExpression(args)

object LogicalOrExpression extends LeftAssociativeExpressionParser(LogicalAndExpression, OR)  {

  override def build(args: List[Expression]): Expression = LogicalAndExpression(args)

}
