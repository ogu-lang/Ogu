package parser.ast.expressions.functions

import lexer.DOTO
import parser.ast.expressions.{CallExpression, Expression, LeftAssociativeExpressionParser}

case class DotoForwardExpression(args: List[Expression]) extends CallExpression

object DotoForwardExpression extends LeftAssociativeExpressionParser(DotoBackwardExpression, DOTO) {

  override def build(args: List[Expression]): Expression = DotoForwardExpression(args)
}


