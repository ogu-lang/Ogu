package parser.ast.expressions.functions

import lexer.PIPERIGHT
import parser.ast.expressions.{CallExpression, Expression, LeftAssociativeExpressionParser}


case class ForwardPipeFuncCallExpression(args: List[Expression]) extends CallExpression

object ForwardPipeFuncCallExpression
  extends LeftAssociativeExpressionParser(ForwardPipeFirstArgFuncCallExpression, PIPERIGHT) {

  override def build(args: List[Expression]): Expression = ForwardPipeFuncCallExpression(args)

}
