package parser.ast.expressions

import lexer.PIPE_RIGHT
import parser.{CallExpression, Expression}


case class ForwardPipeFuncCallExpression(args: List[Expression]) extends CallExpression

object ForwardPipeFuncCallExpression
  extends LeftAssociativeExpressionParser(ForwardPipeFirstArgFuncCallExpression, PIPE_RIGHT) {

  override def build(args: List[Expression]): Expression = ForwardPipeFuncCallExpression(args)

}
