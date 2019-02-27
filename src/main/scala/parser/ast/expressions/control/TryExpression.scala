package parser.ast.expressions.control

import lexer._
import parser.ast.expressions._

case class CatchExpression(id: Option[String], ex: String, body: Expression) extends Expression

case class TryExpression(body: Expression, catches: List[CatchExpression], fin: Option[Expression]) extends Expression

object TryExpression extends ExpressionParser {

  override def parse(tokens: TokenStream): Expression = {
    tokens.consume(TRY)
    val body = parsePipedOrBodyExpression(tokens)
    tokens.consumeOptionals(NL)
    val initialIndents = if (tokens.peek(INDENT)) {
      tokens.consume(INDENT)
      1
    } else {
      0
    }
    val (indents, catches) = parseCatches(tokens, Nil, initialIndents)
    val finallyExpr = parseFinallyExpr(tokens)
    tokens.consume(indents, DEDENT)
    TryExpression(body, catches, finallyExpr)
  }

  private[this]
  def parseCatches(tokens: TokenStream, catches: List[CatchExpression], indents: Int) : (Int, List[CatchExpression]) = {
    if (!tokens.peek(CATCH)) {
      (indents, catches.reverse)
    }
    else {
      val catchExpr = parseCatchExpr(tokens)
      tokens.consumeOptionals(NL)
      if (!tokens.peek(INDENT)) {
        parseCatches(tokens, catchExpr :: catches, indents)

      } else {
        tokens.consume(INDENT)
        parseCatches(tokens, catchExpr :: catches, indents + 1)
      }
    }
  }

  def parseCatchExpr(tokens:TokenStream): CatchExpression = {
    tokens.consume(CATCH)
    if (tokens.peek(classOf[ID])) {
      val id = tokens.consume(classOf[ID]).value
      tokens.consume(COLON)
      val ex = tokens.consume(classOf[TID]).value
      tokens.consume(ARROW)
      CatchExpression(Some(id), ex, parsePipedOrBodyExpression(tokens))
    } else {
      val ex = tokens.consume(classOf[TID]).value
      tokens.consume(ARROW)
      CatchExpression(None, ex, parsePipedOrBodyExpression(tokens))
    }
  }

  private[this] def parseFinallyExpr(tokens:TokenStream) : Option[Expression] = {
    if (!tokens.peek(FINALLY)) {
      None
    }
    else {
      tokens.consume(FINALLY)
      tokens.consume(ARROW)
      Some(parsePipedOrBodyExpression(tokens))
    }
  }

}