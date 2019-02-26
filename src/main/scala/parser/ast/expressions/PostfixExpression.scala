package parser.ast.expressions

import lexer.{ARROBA, TokenStream}
import parser.ast.expressions.logical.LogicalExpression

case class ArrayAccessExpression(array: Expression, index: Expression) extends Expression with AssignableExpression

object PostfixExpression extends ExpressionParser {

  override def parse(tokens: TokenStream): Expression = {
    val expr = PrimaryExpression.parse(tokens)
    if (!tokens.peek(ARROBA)) {
      expr
    } else {
      tokens.consume(ARROBA)
      ArrayAccessExpression(expr, LogicalExpression.parse(tokens))
    }
  }

}
