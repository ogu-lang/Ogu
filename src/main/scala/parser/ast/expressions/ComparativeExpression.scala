package parser.ast.expressions

import lexer.TokenStream
import parser.Expression

class ComparativeExpression(args: List[Expression]) extends Expression

object ComparativeExpression extends ExpressionParser {

  def parse(tokens: TokenStream): Expression = LessThanExpression.parse(tokens)

}
