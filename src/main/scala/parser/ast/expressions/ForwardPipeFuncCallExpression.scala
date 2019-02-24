package parser.ast.expressions

import lexer.{PIPE_RIGHT, TokenStream}
import parser.{CallExpression, Expression}

import scala.annotation.tailrec

case class ForwardPipeFuncCallExpression(args: List[Expression]) extends CallExpression

object ForwardPipeFuncCallExpression {

  def parse(tokens: TokenStream): Expression = {
    val expr = ForwardPipeFirstArgFuncCallExpression.parse(tokens)
    if (!tokens.peek(PIPE_RIGHT)) {
      expr
    }
    else {
      ForwardPipeFuncCallExpression(consumeArgs(tokens, List(expr)))
    }
  }

  @tailrec
  private def consumeArgs(tokens: TokenStream, args: List[Expression]): List[Expression] = {
    if (!tokens.peek(PIPE_RIGHT)) {
      args.reverse
    }
    else {
      tokens.consume(PIPE_RIGHT)
      consumeArgs(tokens, ForwardPipeFirstArgFuncCallExpression.parse(tokens) :: args)
    }
  }
}
