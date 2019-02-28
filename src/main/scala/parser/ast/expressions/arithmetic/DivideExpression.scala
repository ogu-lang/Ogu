package parser.ast.expressions.arithmetic

import lexer.DIV
import parser.ast.expressions.{ArithmeticExpression, Expression, LeftAssociativeExpressionParser}

case class DivideExpression(args: List[Expression]) extends ArithmeticExpression

object DivideExpression extends LeftAssociativeExpressionParser(ModExpression, DIV) {

  override def build(args: List[Expression]): Expression = DivideExpression(args)

}