package parser.ast.expressions

import lexer.{POW, TokenStream}
import parser.Expression

case class PowerExpression(base: Expression, exponent: Expression) extends Expression

object PowerExpression extends ExpressionParser {

  override def parse(tokens: TokenStream): Expression = {
    val expr = ComposeExpressionForward.parse(tokens)
    if (!tokens.peek(POW)) {
      expr
    } else {
      tokens.consume(POW)
      PowerExpression(expr, parse(tokens))
    }
  }

}
