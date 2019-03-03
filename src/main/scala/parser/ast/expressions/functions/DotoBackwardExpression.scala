package parser.ast.expressions.functions

import lexer.DOTO_BACK
import parser.ast.expressions.{CallExpression, Expression, LeftAssociativeExpressionParser}

case class DotoBackwardExpression(args: List[Expression]) extends CallExpression

object DotoBackwardExpression extends LeftAssociativeExpressionParser(FunctionCallWithDollarExpression, DOTO_BACK) {

  override def build(args: List[Expression]): Expression = DotoForwardExpression(args)
}





