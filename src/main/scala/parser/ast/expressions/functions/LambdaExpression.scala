package parser.ast.expressions.functions

import lexer._
import parser.InvalidLambdaExpression
import parser.ast._
import parser.ast.expressions.logical.LogicalExpression
import parser.ast.expressions._

import scala.annotation.tailrec

case class LambdaExpression(args: List[LambdaArg], expr: Expression) extends Expression

object LambdaExpression extends ExpressionParser {

  def parse(tokens:TokenStream) : Expression = {
    if (!tokens.peek(LAMBDA)) {
      LogicalExpression.parse(tokens)
    }
    else {
      tokens.consume(LAMBDA)
      val arg = parseLambdaArg(tokens)
      val args = parseListOfLambdaArgs(tokens, List(arg))
      if (!tokens.peek(ARROW)) {
        throw InvalidLambdaExpression(tokens.nextToken())
      }
      tokens.consume(ARROW)
      LambdaExpression(args, ParseExpr.parse(tokens))
    }
  }

  @tailrec
  private[this] def parseListOfLambdaArgs(tokens: TokenStream, args: List[LambdaArg]): List[LambdaArg] = {
    if (tokens.peek(ARROW)) {
      args.reverse
    }
    else {
      parseListOfLambdaArgs(tokens, parseLambdaArg(tokens) :: args)
    }
  }

  private[this] def parseLambdaArg(tokens:TokenStream) : LambdaArg = {
    if (tokens.peek(classOf[ID])) {
      LambdaSimpleArg(tokens.consume(classOf[ID]).value)
    }
    else if (tokens.peek(LPAREN)) {
      tokens.consume(LPAREN)
      val ids = consumeListOfIdsSepByComma(tokens)
      tokens.consume(RPAREN)
      LambdaTupleArg(ids)
    } else {
      throw InvalidLambdaExpression(tokens.nextToken())
    }
  }

}