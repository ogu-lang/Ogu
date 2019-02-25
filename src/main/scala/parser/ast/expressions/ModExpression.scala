package parser.ast.expressions

import lexer.{MOD, NL, TokenStream}
import parser.Expression
import parser.ast.module.Module

case class ModExpression(left: Expression, right: Expression) extends Expression

object ModExpression extends ExpressionParser {

  override def parse(tokens: TokenStream): Expression = {
    var expr = Module.parsePowExpr(tokens)
    if (tokens.peek(MOD)) {
      val oper = tokens.consume(MOD)
      tokens.consumeOptionals(NL)
      expr = ModExpression(expr, Module.parsePowExpr(tokens))
    }
    expr
  }
}