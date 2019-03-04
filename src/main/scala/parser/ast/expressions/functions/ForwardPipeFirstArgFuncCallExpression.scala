package parser.ast.expressions.functions

import lexer.PIPERIGHTFIRSTARG
import parser.ast.expressions.{CallExpression, Expression, LeftAssociativeExpressionParser}

case class ForwardPipeFirstArgFuncCallExpression(args: List[Expression]) extends CallExpression

object ForwardPipeFirstArgFuncCallExpression
  extends LeftAssociativeExpressionParser(BackwardPipeFuncCallExpression, PIPERIGHTFIRSTARG) {

  override def build(args: List[Expression]): Expression = ForwardPipeFirstArgFuncCallExpression(args)

}

