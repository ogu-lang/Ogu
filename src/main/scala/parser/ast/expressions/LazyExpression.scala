package parser.ast.expressions

import lexer.{LAZY, TokenStream}
import parser.Expression

case class LazyExpression(expr: Expression) extends Expression

object LazyExpression extends ExpressionParser {

  override def parse(tokens: TokenStream): Expression = {
    tokens.consume(LAZY)
    LazyExpression(ForwardPipeFuncCallExpression.parse(tokens))
  }

}