package parser.ast.expressions

import lexer.COMPOSE_BACKWARD
import parser.Expression

case class ComposeExpressionBackward(args: List[Expression]) extends Expression

object ComposeExpressionBackward extends LeftAssociativeExpressionParser(PostfixExpression, COMPOSE_BACKWARD) {

  override def build(args: List[Expression]): Expression = ComposeExpressionBackward(args)

}
