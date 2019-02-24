package parser.ast.expressions

import lexer.PIPE_LEFT_FIRST_ARG
import parser.{CallExpression, Expression}

case class BackwardPipeFirstArgFuncCallExpression(args: List[Expression]) extends CallExpression

object BackwardPipeFirstArgFuncCallExpression
  extends LeftAssociativeExpressionParser(FunctionCallWithDollarExpression, PIPE_LEFT_FIRST_ARG) {

  override def build(args: List[Expression]): Expression = BackwardPipeFirstArgFuncCallExpression(args)

}
