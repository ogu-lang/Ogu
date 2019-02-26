package parser.ast.expressions.arithmetic

import lexer.DIV
import parser.ast.expressions.{Expression, LeftAssociativeExpressionParser}

case class DivideExpression(args: List[Expression]) extends Expression

object DivideExpression extends LeftAssociativeExpressionParser(ModExpression, DIV) {

  override def build(args: List[Expression]): Expression = DivideExpression(args)

}