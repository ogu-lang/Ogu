package parser.ast.expressions.functions

import lexer.PIPELEFTFIRSTARG
import parser.ast.expressions.{CallExpression, Expression, LeftAssociativeExpressionParser}

case class BackwardPipeFirstArgFuncCallExpression(args: List[Expression]) extends CallExpression

object BackwardPipeFirstArgFuncCallExpression
  extends LeftAssociativeExpressionParser(DotoForwardExpression, PIPELEFTFIRSTARG) {

  override def build(args: List[Expression]): Expression = BackwardPipeFirstArgFuncCallExpression(args.reverse)

}
