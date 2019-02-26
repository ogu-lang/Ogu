package parser.ast.expressions.comparisons

import lexer.TokenStream
import parser.ast.expressions.{Expression, ExpressionParser}

class ComparativeExpression(args: List[Expression]) extends Expression

object ComparativeExpression extends ExpressionParser {

  def parse(tokens: TokenStream): Expression = LessThanExpression.parse(tokens)

}
