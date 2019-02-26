package parser.ast.expressions.functions

import lexer.PIPE_RIGHT
import parser.ast.CallExpression
import parser.ast.expressions.{Expression, LeftAssociativeExpressionParser}


case class ForwardPipeFuncCallExpression(args: List[Expression]) extends CallExpression

object ForwardPipeFuncCallExpression
  extends LeftAssociativeExpressionParser(ForwardPipeFirstArgFuncCallExpression, PIPE_RIGHT) {

  override def build(args: List[Expression]): Expression = ForwardPipeFuncCallExpression(args)

}
