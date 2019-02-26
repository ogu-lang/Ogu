package parser.ast.expressions.functions

import lexer.PIPE_LEFT_FIRST_ARG
import parser.ast.expressions.{CallExpression, Expression, LeftAssociativeExpressionParser}

case class BackwardPipeFuncCallExpression(args: List[Expression]) extends CallExpression

object BackwardPipeFuncCallExpression
  extends LeftAssociativeExpressionParser(BackwardPipeFirstArgFuncCallExpression, PIPE_LEFT_FIRST_ARG) {

  override def build(args: List[Expression]): Expression = BackwardPipeFuncCallExpression(args)

}
