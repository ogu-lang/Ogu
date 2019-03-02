package parser.ast.expressions.functions

import lexer.PIPE_LEFT_FIRST_ARG
import parser.ast.expressions.{CallExpression, Expression, LeftAssociativeExpressionParser}

case class BackwardPipeFirstArgFuncCallExpression(args: List[Expression]) extends CallExpression

object BackwardPipeFirstArgFuncCallExpression
  extends LeftAssociativeExpressionParser(DotoForwardExpression, PIPE_LEFT_FIRST_ARG) {

  override def build(args: List[Expression]): Expression = BackwardPipeFirstArgFuncCallExpression(args.reverse)

}
