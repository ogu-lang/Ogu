package parser.ast.expressions.list_ops

import lexer.PLUSPLUS
import parser.ast.expressions.{Expression, LeftAssociativeExpressionParser}
import parser.ast.expressions.arithmetic.MultiplyExpression

case class ConcatExpression(args: List[Expression]) extends ListOpExpresion

object ConcatExpression extends LeftAssociativeExpressionParser(MultiplyExpression, PLUSPLUS) {

  override def build(args: List[Expression]): Expression = ConcatExpression(args)
}
