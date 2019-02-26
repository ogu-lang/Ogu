package parser.ast.expressions.control

import lexer.{LAZY, TokenStream}
import parser.ast.expressions.functions.ForwardPipeFuncCallExpression
import parser.ast.expressions.{Expression, ExpressionParser}

case class LazyExpression(expr: Expression) extends Expression

object LazyExpression extends ExpressionParser {

  override def parse(tokens: TokenStream): Expression = {
    tokens.consume(LAZY)
    LazyExpression(ForwardPipeFuncCallExpression.parse(tokens))
  }

}