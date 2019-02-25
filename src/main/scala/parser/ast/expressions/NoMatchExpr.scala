package parser.ast.expressions

import lexer._
import parser.Expression

case class NoMatchExpr(left: Expression, right: Expression) extends Expression

object NoMatchExpr extends ExpressionParser {

  override def parse(tokens: TokenStream): Expression = {
    var expr = ReMatchExpr.parse(tokens)
    if (tokens.peek(NOT_MATCHES)) {
      val oper = tokens.consume(NOT_MATCHES)
      tokens.consumeOptionals(NL)
      expr = NoMatchExpr(expr, ReMatchExpr.parse(tokens))
    }
    expr
  }
}