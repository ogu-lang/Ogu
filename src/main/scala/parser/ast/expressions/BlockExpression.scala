package parser.ast.expressions

import lexer.{DEDENT, INDENT, NL, TokenStream}
import parser.Expression


case class BlockExpression(expressions: List[Expression]) extends Expression

object BlockExpression extends ExpressionParser {

  def parse(tokens: TokenStream): Expression = {
    tokens.consume(INDENT)
    val listOfExpressions = consumeExpressions(tokens, Nil)
    tokens.consume(DEDENT)
    BlockExpression(listOfExpressions)
  }

  private[this] def consumeExpressions(tokens: TokenStream, expressions: List[Expression]): List[Expression] = {
    if (tokens.peek(DEDENT)) {
      expressions.reverse
    }
    else {
      val expr = ForwardPipeFuncCallExpression.parse(tokens)
      tokens.consumeOptionals(NL)
      consumeExpressions(tokens, expr :: expressions)
    }
  }
}