package parser.ast.expressions

import lexer.{MATCHES, NL, TokenStream}
import parser.Expression

case class MatchesExpression(left: Expression, right: Expression) extends Expression

object MatchesExpression extends ExpressionParser {

  override def parse(tokens: TokenStream): Expression = {
    val expr = NoMatchExpr.parse(tokens)
    if (!tokens.peek(MATCHES)) {
      expr
    } else {
      tokens.consume(MATCHES)
      tokens.consumeOptionals(NL)
      MatchesExpression(expr, NoMatchExpr.parse(tokens))
    }
  }
}