package parser.ast.expressions.control

import lexer.{DO, TokenStream, WHILE}
import parser.ast.expressions.logical.LogicalExpression
import parser.ast.expressions.{Expression, ExpressionParser, parsePipedOrBodyExpression}

case class WhileExpression(comp: Expression, body: Expression) extends ControlExpression

object WhileExpression extends ExpressionParser {

  override def parse(tokens: TokenStream): Expression = {
    tokens.consume(WHILE)
    val comp = LogicalExpression.parse(tokens)
    tokens.consume(DO)
    WhileExpression(comp, parsePipedOrBodyExpression(tokens))
  }

}
