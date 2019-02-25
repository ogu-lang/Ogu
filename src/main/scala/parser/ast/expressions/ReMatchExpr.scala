package parser.ast.expressions

import lexer.{MATCH, NL, TokenStream}
import parser.Expression


case class ReMatchExpr(left: Expression, right: Expression) extends Expression

object ReMatchExpr extends ExpressionParser {

  override def parse(tokens: TokenStream): Expression = {
    val expr = ConsExpression.parse(tokens)
    if (!tokens.peek(MATCH)) {
      expr
    } else {
      tokens.consume(MATCH)
      tokens.consumeOptionals(NL)
      ReMatchExpr(expr, ConsExpression.parse(tokens))
    }
  }
}