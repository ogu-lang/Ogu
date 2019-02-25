package parser.ast.expressions

import lexer.{DO, TokenStream, WHILE}
import parser.Expression

case class WhileExpression(comp: Expression, body: Expression) extends ControlExpression

object WhileExpression extends ExpressionParser {

  override def parse(tokens: TokenStream): Expression = {
    tokens.consume(WHILE)
    val comp = LogicalExpression.parse(tokens)
    tokens.consume(DO)
    WhileExpression(comp, parsePipedOrBodyExpression(tokens))
  }

}
