package parser.ast.expressions

import lexer.{PIPE_RIGHT_FIRST_ARG, TokenStream}
import parser.{CallExpression, Expression}
import parser.ast.module.Module.{parseBackwardPipeExpr}

import scala.annotation.tailrec

case class ForwardPipeFirstArgFuncCallExpression(args: List[Expression]) extends CallExpression


object ForwardPipeFirstArgFuncCallExpression {

  def parse(tokens: TokenStream): Expression = {
    val expr = parseBackwardPipeExpr(tokens)
    if (!tokens.peek(PIPE_RIGHT_FIRST_ARG)) {
      expr
    }
    else {
      ForwardPipeFirstArgFuncCallExpression(consumeArgs(tokens, List(expr)))
    }
  }

  @tailrec
  private def consumeArgs(tokens: TokenStream, args: List[Expression]): List[Expression] = {
    if (!tokens.peek(PIPE_RIGHT_FIRST_ARG)) {
      args.reverse
    }
    else {
      tokens.consume(PIPE_RIGHT_FIRST_ARG)
      consumeArgs(tokens, parseBackwardPipeExpr(tokens) :: args)
    }
  }
}
