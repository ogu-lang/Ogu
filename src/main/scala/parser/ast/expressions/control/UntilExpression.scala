package parser.ast.expressions.control

import lexer.{DO, TokenStream, UNTIL}
import parser.ast.expressions.logical.LogicalExpression
import parser.ast.expressions.{Expression, ExpressionParser, parsePipedOrBodyExpression}

case class UntilExpression(comp: Expression, body: Expression) extends ControlExpression

object UntilExpression extends ExpressionParser {

  override def parse(tokens: TokenStream): Expression = {
    tokens.consume(UNTIL)
    val comp = LogicalExpression.parse(tokens)
    tokens.consume(DO)
    UntilExpression(comp, parsePipedOrBodyExpression(tokens))
  }

}
