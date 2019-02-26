package parser.ast.expressions.functions

import lexer.COMPOSE_BACKWARD
import parser.ast.expressions.{Expression, LeftAssociativeExpressionParser, PostfixExpression}

case class ComposeExpressionBackward(args: List[Expression]) extends Expression

object ComposeExpressionBackward extends LeftAssociativeExpressionParser(PostfixExpression, COMPOSE_BACKWARD) {

  override def build(args: List[Expression]): Expression = ComposeExpressionBackward(args)

}
