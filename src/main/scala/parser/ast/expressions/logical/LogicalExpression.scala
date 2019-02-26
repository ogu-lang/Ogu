package parser.ast.expressions.logical

import lexer.TokenStream
import parser.ast.expressions.{Expression, ExpressionParser}

class LogicalExpression(args: List[Expression]) extends Expression

object LogicalExpression extends ExpressionParser {

  def parse(tokens: TokenStream): Expression = {
    LogicalOrExpression.parse(tokens)
  }

}

