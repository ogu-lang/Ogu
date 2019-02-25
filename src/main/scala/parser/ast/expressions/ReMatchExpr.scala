package parser.ast.expressions

import lexer.{MATCH, NL, TokenStream}
import parser.Expression
import parser.ast.module.Module.parseConsExpr


case class ReMatchExpr(left: Expression, right: Expression) extends Expression

object ReMatchExpr extends ExpressionParser {

  override def parse(tokens: TokenStream): Expression = {
    var expr = parseConsExpr(tokens)
    if (tokens.peek(MATCH)) {
      val oper = tokens.consume(MATCH)
      tokens.consumeOptionals(NL)
      expr = ReMatchExpr(expr, parseConsExpr(tokens))
    }
    expr
  }
}