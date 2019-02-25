package parser.ast.expressions

import lexer._
import parser.Expression

case class NoMatchExpr(left: Expression, right: Expression) extends Expression

object NoMatchExpr extends ExpressionParser {

  override def parse(tokens: TokenStream): Expression = {
    val expr = ReMatchExpr.parse(tokens)
    if (!tokens.peek(NOT_MATCHES)) {
      expr
    }
    else {
      tokens.consume(NOT_MATCHES)
      tokens.consumeOptionals(NL)
      NoMatchExpr(expr, ReMatchExpr.parse(tokens))
    }
  }

}