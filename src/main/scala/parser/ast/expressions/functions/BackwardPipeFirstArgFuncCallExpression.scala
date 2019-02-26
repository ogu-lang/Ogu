package parser.ast.expressions.functions

import lexer.PIPE_LEFT_FIRST_ARG
import parser.ast.CallExpression
import parser.ast.expressions.{Expression, LeftAssociativeExpressionParser}

case class BackwardPipeFirstArgFuncCallExpression(args: List[Expression]) extends CallExpression

object BackwardPipeFirstArgFuncCallExpression
  extends LeftAssociativeExpressionParser(FunctionCallWithDollarExpression, PIPE_LEFT_FIRST_ARG) {

  override def build(args: List[Expression]): Expression = BackwardPipeFirstArgFuncCallExpression(args)

}
