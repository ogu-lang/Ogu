package parser.ast.expressions.types

import lexer._
import parser.ast.expressions.{Expression, ExpressionParser, Identifier, ParseExpr}
import parser.ast.expressions.literals.{Atom, AtomicExpression, LiteralExpression}
import parser.InvalidExpression

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
    tokens.nextToken() match {
      case _:ATOM => Atom.parse(tokens)
      case _:TID if !tokens.peek(2, LPAREN) =>  Identifier(tokens.consume(classOf[TID]).value)
      case _:ID if !tokens.peek(2, LPAREN) =>  Identifier(tokens.consume(classOf[ID]).value)
      case _:LITERAL => LiteralExpression.parse(tokens)
      case LPAREN => AtomicExpression.parse(tokens)
      case LBRACKET => AtomicExpression.parse(tokens)
      case _ =>
        println(s"@@! ${tokens}")
        throw InvalidExpression(tokens.nextToken())
    }
  }

}