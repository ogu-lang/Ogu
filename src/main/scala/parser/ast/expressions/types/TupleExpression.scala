package parser.ast.expressions.types

import lexer._
import parser.ast.expressions.{Expression, ExpressionParser, Identifier}
import parser.ast.expressions.functions.{ForwardPipeFuncCallExpression, FunctionCallExpression}

case class TupleExpression(expressions: List[Expression]) extends Expression

case class InfiniteTupleExpr(expressions: List[Expression]) extends Expression

object TupleExpression extends ExpressionParser {

  override def parse(tokens: TokenStream): Expression = {
    tokens.consume(LPAREN)
    val iniExpr = ForwardPipeFuncCallExpression.parse(tokens)
    val expr = if (!tokens.peek(COMMA)) {
      iniExpr
    } else {
      val tupleElements = consumeListOfPipedExpressions(tokens, List(iniExpr))
      if (!tokens.peek(DOTDOTDOT)) {
        TupleExpression(tupleElements)
      } else {
        tokens.consume(DOTDOTDOT)
        InfiniteTupleExpr(tupleElements)
      }
    }
    tokens.consume(RPAREN)
    if (!expr.isInstanceOf[Identifier]) {
      expr
    } else {
      FunctionCallExpression(expr, List.empty[Expression])
    }
  }

  private[this] def consumeListOfPipedExpressions(tokens: TokenStream, exprs: List[Expression]): List[Expression] = {
    if (!tokens.peek(COMMA)) {
      exprs.reverse
    }
    else {
      tokens.consume(COMMA)
      consumeListOfPipedExpressions(tokens, ForwardPipeFuncCallExpression.parse(tokens) :: exprs)
    }
  }

}
