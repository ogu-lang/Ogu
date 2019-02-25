package parser.ast.expressions
import lexer._
import parser._
import parser.ast.module.Module.{parseDictionaryExpr, parseLiteral, parseRangeExpr, parseSetExpr}

object AtomicExpression extends ExpressionParser {

  override def parse(tokens: TokenStream): Expression = {
    var expr: Expression = null
    tokens.nextToken() match {
      case None => throw InvalidExpression()
      case Some(token) =>
        token match {
          case LPAREN =>
            parseTupleExpression(tokens)
          case LBRACKET =>
            parseRangeExpr(tokens)
          case LCURLY =>
            parseDictionaryExpr(tokens)
          case HASHLCURLY =>
            parseSetExpr(tokens)
          case _ =>
            parseLiteral(tokens)
        }
    }
  }

  private[this] def parseTupleExpression(tokens: TokenStream): Expression = {
    tokens.consume(LPAREN)
    val iniExpr = ForwardPipeFuncCallExpression.parse(tokens)
    val expr = if (!tokens.peek(COMMA)) {
      iniExpr
    } else {
      val tupleElements = consumeListOfPipedExpressions(tokens, List(iniExpr))
      if (!tokens.peek(DOTDOTDOT)) {
        TupleExpr(tupleElements)
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
