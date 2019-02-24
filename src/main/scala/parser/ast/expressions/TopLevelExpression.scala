package parser.ast.expressions

import lexer.TokenStream
import parser.{Expression, LangNode}

case class TopLevelExpression(expression: Expression) extends LangNode

object TopLevelExpression {

  def parse(tokens: TokenStream): TopLevelExpression = {
    val expr = ForwardPipeFuncCallExpression.parse(tokens)
    TopLevelExpression(expr)
  }

}
