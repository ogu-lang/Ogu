package parser.ast.expressions.types

import lexer._
import parser.ast.expressions.literals.{Atom, LiteralExpression}
import parser.ast.expressions.{Expression, ExpressionParser, ParseExpr}

case class DictionaryExpression(items: List[(Expression, Expression)]) extends Expression

object DictionaryExpression extends ExpressionParser {

  override def parse(tokens: TokenStream): Expression = {
    tokens.consume(LCURLY)
    val key = parseKeyExpr(tokens)
    val value = ParseExpr.parse(tokens)
    val listOfPairs = consumePairs(tokens, List((key, value)))
    tokens.consume(RCURLY)
    DictionaryExpression(listOfPairs)
  }

  private[this]
  def consumePairs(tokens: TokenStream, pairs: List[(Expression, Expression)]): List[(Expression,Expression)] = {
    if (!tokens.peek(COMMA)) {
      pairs.reverse
    }
    else {
      tokens.consume(COMMA)
      tokens.consumeOptionals(NL)
      val key = parseKeyExpr(tokens)
      val value = ParseExpr.parse(tokens)
      consumePairs(tokens, (key, value) :: pairs)
    }
  }

  def parseKeyExpr(tokens:TokenStream) : Expression = {
    if (tokens.peek(classOf[ATOM])) {
      Atom.parse(tokens)
    }
    else {
      LiteralExpression.parse(tokens)
    }
  }

}