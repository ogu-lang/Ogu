package parser.ast.expressions

import lexer.COMPOSE_FORWARD
import parser.Expression

case class ComposeExpressionForward(args: List[Expression]) extends Expression

object ComposeExpressionForward extends LeftAssociativeExpressionParser(ComposeExpressionBackward, COMPOSE_FORWARD) {

  override def build(args: List[Expression]): Expression = ComposeExpressionForward(args)

}