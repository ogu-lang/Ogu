package parser.ast.expressions

import lexer.{ASSIGN, SET, TokenStream}
import parser.{AssignableExpression, CantAssignToExpression, Expression}

case class SimpleAssignExpression(left: Expression, right: Expression) extends Expression with AssignableExpression

object SimpleAssignExpression extends ExpressionParser {

  override def parse(tokens: TokenStream): Expression = {
    tokens.consume(SET)
    val expr = parsePipedOrBodyExpression(tokens)
    if (!expr.isInstanceOf[AssignableExpression])
      throw CantAssignToExpression()
    tokens.consume(ASSIGN)
    SimpleAssignExpression(expr, ForwardPipeFuncCallExpression.parse(tokens))
  }

}