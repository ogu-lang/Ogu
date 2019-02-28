package parser.ast.expressions.functions

import lexer.COMPOSE_BACKWARD
import parser.ast.expressions.{CallExpression, Expression, LeftAssociativeExpressionParser, PostfixExpression}

case class ComposeExpressionBackward(args: List[Expression]) extends CallExpression

object ComposeExpressionBackward extends LeftAssociativeExpressionParser(PostfixExpression, COMPOSE_BACKWARD) {

  override def build(args: List[Expression]): Expression = ComposeExpressionBackward(args)

}
