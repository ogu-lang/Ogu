package parser.ast.expressions.functions

import lexer.DOTOBACK
import parser.ast.expressions.{CallExpression, Expression, LeftAssociativeExpressionParser}

case class DotoBackwardExpression(args: List[Expression]) extends CallExpression

object DotoBackwardExpression extends LeftAssociativeExpressionParser(FunctionCallWithDollarExpression, DOTOBACK) {

  override def build(args: List[Expression]): Expression = DotoForwardExpression(args)
}





