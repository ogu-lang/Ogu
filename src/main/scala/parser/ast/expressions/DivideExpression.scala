package parser.ast.expressions

import lexer.DIV
import parser.Expression

case class DivideExpression(args: List[Expression]) extends Expression

object DivideExpression extends LeftAssociativeExpressionParser(ModExpression, DIV) {

  override def build(args: List[Expression]): Expression = DivideExpression(args)

}