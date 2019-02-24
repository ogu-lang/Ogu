package parser.ast.expressions
import lexer.TokenStream
import parser.Expression

class LogicalExpression(args: List[Expression]) extends Expression

object LogicalExpression extends ExpressionParser {

  def parse(tokens: TokenStream): Expression = {
    LogicalOrExpression.parse(tokens)
  }

}

