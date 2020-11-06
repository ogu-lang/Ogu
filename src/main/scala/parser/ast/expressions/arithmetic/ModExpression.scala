package parser.ast.expressions.arithmetic

import lexer.{MOD, NL, TokenStream}
import parser.ast.expressions.{ArithmeticExpression, Expression, ExpressionParser}

case class ModExpression(left: Expression, right: Expression) extends ArithmeticExpression

object ModExpression extends ExpressionParser {

  override def parse(tokens: TokenStream): Expression = {
    val expr = PowerExpression.parse(tokens)
    if (!tokens.peek(MOD)) {
      expr
    } else {
      tokens.consume(MOD)
      tokens.consumeOptionals(NL)
      ModExpression(expr, PowerExpression.parse(tokens))
    }
  }
}