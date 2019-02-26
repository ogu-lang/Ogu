package parser.ast.expressions.types

import lexer._
import parser.ast.expressions.{CallExpression, ExpressionParser, _}

case class ConstructorExpression(cls: String, args: List[Expression]) extends CallExpression
case class RecordConstructorExpression(rec: String, args: List[Expression]) extends CallExpression

object ConstructorExpression extends ExpressionParser {

  override def parse(tokens: TokenStream): Expression = {
    val cls = tokens.consume(classOf[TID]).value
    tokens.nextToken() match {
      case LPAREN =>
        tokens.consume(LPAREN)
        val args = if (tokens.peek(RPAREN)) List.empty else parseListOfCommaSeparatedExpressions(tokens)
        tokens.consume(RPAREN)
        ConstructorExpression(cls, args)
      case LCURLY =>
        tokens.consume(LCURLY)
        val args = if (tokens.peek(RCURLY)) List.empty else parseListOfCommaSeparatedExpressions(tokens)
        tokens.consume(RCURLY)
        RecordConstructorExpression(cls, args)
      case _ =>
        RecordConstructorExpression(cls, List.empty)
    }
  }

}
