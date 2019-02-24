package parser.ast.expressions

import lexer.{NL, THEN, TokenStream, WHEN}
import parser.Expression

case class WhenExpression(comp: Expression, body: Expression) extends ControlExpression

object WhenExpression extends ExpressionParser {

  override def parse(tokens: TokenStream): Expression = {
    tokens.consume(WHEN)
    val comp = LogicalExpression.parse(tokens)
    tokens.consume(THEN)
    if (!tokens.peek(NL)) {
      WhenExpression(comp, ForwardPipeFuncCallExpression.parse(tokens))
    }
    else {
      tokens.consume(NL)
      WhenExpression(comp, BlockExpression.parse(tokens))
    }
  }

}