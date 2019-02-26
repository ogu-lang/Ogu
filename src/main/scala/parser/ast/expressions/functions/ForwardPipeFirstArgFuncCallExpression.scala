package parser.ast.expressions.functions

import lexer.PIPE_RIGHT_FIRST_ARG
import parser.ast.expressions.{CallExpression, Expression, LeftAssociativeExpressionParser}

case class ForwardPipeFirstArgFuncCallExpression(args: List[Expression]) extends CallExpression

object ForwardPipeFirstArgFuncCallExpression
  extends LeftAssociativeExpressionParser(BackwardPipeFuncCallExpression, PIPE_RIGHT_FIRST_ARG) {

  override def build(args: List[Expression]): Expression = ForwardPipeFirstArgFuncCallExpression(args)

}

