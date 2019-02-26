package parser.ast.expressions.control

import lexer.{NL, THEN, TokenStream, WHEN}
import parser.ast.expressions.functions.ForwardPipeFuncCallExpression
import parser.ast.expressions.logical.LogicalExpression
import parser.ast.expressions.{BlockExpression, Expression, ExpressionParser}

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