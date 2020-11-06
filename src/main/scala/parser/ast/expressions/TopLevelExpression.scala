package parser.ast.expressions

import lexer.TokenStream
import parser.ast.expressions.functions.ForwardPipeFuncCallExpression
import parser.ast.LangNode

case class TopLevelExpression(expression: Expression) extends LangNode

object TopLevelExpression {

  def parse(tokens: TokenStream): TopLevelExpression = {
    val expr = ForwardPipeFuncCallExpression.parse(tokens)
    TopLevelExpression(expr)
  }

}
