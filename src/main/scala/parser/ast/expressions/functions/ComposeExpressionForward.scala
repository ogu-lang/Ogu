package parser.ast.expressions.functions

import lexer.COMPOSEFORWARD
import parser.ast.expressions.{CallExpression, Expression, LeftAssociativeExpressionParser}

case class ComposeExpressionForward(args: List[Expression]) extends CallExpression

object ComposeExpressionForward extends LeftAssociativeExpressionParser(ComposeExpressionBackward, COMPOSEFORWARD) {

  override def build(args: List[Expression]): Expression = ComposeExpressionForward(args)

}