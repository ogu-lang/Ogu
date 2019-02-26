package parser.ast.expressions.functions

import lexer.COMPOSE_FORWARD
import parser.ast.expressions.{Expression, LeftAssociativeExpressionParser}

case class ComposeExpressionForward(args: List[Expression]) extends Expression

object ComposeExpressionForward extends LeftAssociativeExpressionParser(ComposeExpressionBackward, COMPOSE_FORWARD) {

  override def build(args: List[Expression]): Expression = ComposeExpressionForward(args)

}