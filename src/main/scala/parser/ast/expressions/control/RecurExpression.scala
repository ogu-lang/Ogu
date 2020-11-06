package parser.ast.expressions.control

import lexer.{RECUR, TokenStream}
import parser.ast.expressions.functions.ForwardPipeFuncCallExpression
import parser.ast.expressions.{Expression, ExpressionParser, funcCallEndToken}

import scala.annotation.tailrec

case class RecurExpression(args: List[Expression]) extends ControlExpression


object RecurExpression extends ExpressionParser {

  override def parse(tokens: TokenStream): Expression = {
    tokens.consume(RECUR)
    RecurExpression(consumeRecurArgs(tokens, Nil))
  }

  @tailrec
  private[this] def consumeRecurArgs(tokens: TokenStream, args: List[Expression]) : List[Expression] = {
    if (funcCallEndToken(tokens)) {
      args.reverse
    }
    else {
      consumeRecurArgs(tokens, ForwardPipeFuncCallExpression.parse(tokens) :: args)
    }
  }

}