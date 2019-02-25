package parser.ast.expressions

import lexer.{DO, TokenStream, UNTIL}
import parser.Expression

case class UntilExpression(comp: Expression, body: Expression) extends ControlExpression

object UntilExpression extends ExpressionParser {

  override def parse(tokens: TokenStream): Expression = {
    tokens.consume(UNTIL)
    val comp = LogicalExpression.parse(tokens)
    tokens.consume(DO)
    UntilExpression(comp, parsePipedOrBodyExpression(tokens))
  }

}
