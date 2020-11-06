package parser.ast.expressions.control

import lexer.{SYNC, TokenStream}
import parser.ast.expressions.{Expression, ExpressionParser, parsePipedOrBodyExpression}

case class SyncExpression(body: Expression) extends ControlExpression


object SyncExpression extends ExpressionParser {

  override def parse(tokens: TokenStream): Expression = {
    tokens.consume(SYNC)
    SyncExpression(parsePipedOrBodyExpression(tokens))
  }
}
