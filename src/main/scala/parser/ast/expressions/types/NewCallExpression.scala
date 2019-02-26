package parser.ast.expressions.types

import lexer._
import parser.ast.CallExpression
import parser.ast.expressions.{Expression, ExpressionParser, parseListOfCommaSeparatedExpressions}

case class NewCallExpression(cls: String, args: List[Expression]) extends CallExpression

object NewCallExpression extends ExpressionParser {

  override def parse(tokens: TokenStream): Expression = {
    tokens.consume(NEW)
    val cls = tokens.consume(classOf[TID]).value
    tokens.consume(LPAREN)
    val args = if (tokens.peek(RPAREN)) Nil else parseListOfCommaSeparatedExpressions(tokens)
    tokens.consume(RPAREN)
    NewCallExpression(cls, args)
  }

}
