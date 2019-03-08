package parser.ast.expressions.control

import exceptions.CantAssignToExpression
import lexer.{ASSIGN, SET, TokenStream}
import parser.ast.expressions.functions.ForwardPipeFuncCallExpression
import parser.ast.expressions.{AssignableExpression, Expression, ExpressionParser, parsePipedOrBodyExpression}

case class SimpleAssignExpression(left: Expression, right: Expression) extends ControlExpression with AssignableExpression

object SimpleAssignExpression extends ExpressionParser {
  override def parse(tokens: TokenStream): Expression = {
    val line = tokens.currentLine()
    tokens.consume(SET)
    val expr = parsePipedOrBodyExpression(tokens)
    if (!expr.isInstanceOf[AssignableExpression])
      throw CantAssignToExpression(line)
    tokens.consume(ASSIGN)
    SimpleAssignExpression(expr, ForwardPipeFuncCallExpression.parse(tokens))
  }

}