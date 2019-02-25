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
            TupleExpression.parse(tokens)
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


}
