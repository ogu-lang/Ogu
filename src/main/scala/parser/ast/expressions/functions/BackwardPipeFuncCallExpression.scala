package parser.ast.expressions.functions

import lexer.{PIPELEFT, PIPELEFTFIRSTARG}
import parser.ast.expressions.{CallExpression, Expression, LeftAssociativeExpressionParser}

case class BackwardPipeFuncCallExpression(args: List[Expression]) extends CallExpression

object BackwardPipeFuncCallExpression
  extends LeftAssociativeExpressionParser(BackwardPipeFirstArgFuncCallExpression, PIPELEFT) {

  override def build(args: List[Expression]): Expression = BackwardPipeFuncCallExpression(args.reverse)

}
