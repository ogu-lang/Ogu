package parser.ast.expressions.arithmetic

import lexer.{POW, TokenStream}
import parser.ast.expressions.functions.ComposeExpressionForward
import parser.ast.expressions.{ArithmeticExpression, Expression, ExpressionParser}

case class PowerExpression(base: Expression, exponent: Expression) extends ArithmeticExpression

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
