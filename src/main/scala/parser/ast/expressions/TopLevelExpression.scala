package parser.ast.expressions

import lexer.TokenStream
import parser.ast.LangNode
import parser.ast.expressions.functions.ForwardPipeFuncCallExpression

case class TopLevelExpression(expression: Expression) extends LangNode

object TopLevelExpression {

  def parse(tokens: TokenStream): TopLevelExpression = {
    val expr = ForwardPipeFuncCallExpression.parse(tokens)
    TopLevelExpression(expr)
  }

}
