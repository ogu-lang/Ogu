package parser.ast.expressions.functions

import lexer.COMPOSE_FORWARD
import parser.ast.expressions.{CallExpression, Expression, LeftAssociativeExpressionParser}

case class ComposeExpressionForward(args: List[Expression]) extends CallExpression

object ComposeExpressionForward extends LeftAssociativeExpressionParser(ComposeExpressionBackward, COMPOSE_FORWARD) {

  override def build(args: List[Expression]): Expression = ComposeExpressionForward(args)

}