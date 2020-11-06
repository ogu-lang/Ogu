package parser.ast.expressions.control

import lexer._
import parser.ast.expressions.functions.ForwardPipeFuncCallExpression
import parser.ast.expressions.logical.LogicalExpression
import parser.ast.expressions.{Expression, ExpressionParser}
import scala.annotation.tailrec

case class CondGuard(comp: Option[Expression], value: Expression)
case class CondExpression(guards: List[CondGuard]) extends ControlExpression

object CondExpression extends ExpressionParser {

  def parse(tokens: TokenStream): Expression = {
    tokens.consume(COND)
    tokens.consume(NL)
    tokens.consume(INDENT)
    val guards = consumeGuards(tokens, Nil)
    tokens.consume(DEDENT)
    CondExpression(guards)
  }

  @tailrec
  private[this] def consumeGuards(tokens: TokenStream, guards: List[CondGuard]): List[CondGuard] = {
    if (tokens.peek(DEDENT))
      guards.reverse
    else {
      val comp = if (!tokens.peek(OTHERWISE)) {
        Some(LogicalExpression.parse(tokens))
      }
      else {
        tokens.consume(OTHERWISE)
        None
      }
      tokens.consume(ARROW)
      val value = ForwardPipeFuncCallExpression.parse(tokens:TokenStream)
      tokens.consumeOptionals(NL)
      consumeGuards(tokens, CondGuard(comp, value) :: guards)
    }
  }

}
