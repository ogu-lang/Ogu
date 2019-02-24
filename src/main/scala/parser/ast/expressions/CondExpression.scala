package parser.ast.expressions

import lexer._
import parser.{CondGuard, ControlExpression, Expression}
import parser.ast.module.Module.{parseLogicalExpr, parsePipedExpr}

case class CondExpression(guards: List[CondGuard]) extends ControlExpression

object CondExpression extends ExpressionParser {

  def parse(tokens: TokenStream): Expression = {
    tokens.consume(COND)
    tokens.consume(NL)
    tokens.consume(INDENT)
    val guards = consumeGuards(tokens, List.empty)
    tokens.consume(DEDENT)
    CondExpression(guards)
  }

  private[this] def consumeGuards(tokens: TokenStream, guards: List[CondGuard]): List[CondGuard] = {
    if (tokens.peek(DEDENT))
      guards.reverse
    else {
      val comp = if (tokens.peek(OTHERWISE)) {
        tokens.consume(OTHERWISE)
        None
      } else {
        Some(parseLogicalExpr(tokens))
      }
      tokens.consume(ARROW)
      val value = ForwardPipeFuncCallExpression.parse(tokens:TokenStream)
      tokens.consumeOptionals(NL)
      consumeGuards(tokens, CondGuard(comp, value) :: guards)
    }
  }

}
