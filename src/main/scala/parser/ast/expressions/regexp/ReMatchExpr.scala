package parser.ast.expressions.regexp

import lexer.{MATCH, NL, TokenStream}
import parser.ast.expressions.list_ops.ConsExpression
import parser.ast.expressions.{Expression, ExpressionParser}

case class ReMatchExpr(left: Expression, right: Expression) extends RegexExpression

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