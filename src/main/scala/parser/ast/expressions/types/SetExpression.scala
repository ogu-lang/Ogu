package parser.ast.expressions.types

import lexer._
import parser.ast.expressions.{Expression, ExpressionParser, parseListOfCommaSeparatedExpressions}

case class SetExpression(values: List[Expression]) extends Expression

object SetExpression extends ExpressionParser {

  override def parse(tokens: TokenStream): Expression = {
    tokens.consume(HASHLCURLY)
    val listOfValues = parseListOfCommaSeparatedExpressions(tokens)
    tokens.consume(RCURLY)
    SetExpression(listOfValues)
  }

}