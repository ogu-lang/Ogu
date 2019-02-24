package parser.ast.expressions

import lexer._
import parser.ast.module.Module._
import parser.{CallExpression, Expression}

case class FunctionCallWithDollarExpression(func: Expression, args: List[Expression]) extends CallExpression

object FunctionCallWithDollarExpression extends ExpressionParser {

  override def parse(tokens: TokenStream): Expression = {
    val expr = ParseExpr.parse(tokens)
    if (tokens.peek(DOLLAR)) {
      tokens.consume(DOLLAR)
      FunctionCallWithDollarExpression(expr, parseArgs(tokens, List.empty))
    } else {
      expr
    }
  }

  private[this] def parseArgs(tokens: TokenStream, args: List[Expression]) : List[Expression] = {
    if (funcCallEndToken(tokens)) {
      args.reverse
    }
    else {
      parseArgs(tokens, parse(tokens) :: args)
    }
  }


}