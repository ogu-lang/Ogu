package parser.ast.expressions.functions

import exceptions.InvalidLambdaExpression
import lexer._
import parser.ast._
import parser.ast.expressions._
import parser.ast.expressions.logical.LogicalExpression

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
        throw InvalidLambdaExpression(tokens.nextSymbol(), tokens.currentLine())
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
    tokens.nextSymbol() match {
      case LPAREN =>
        tokens.consume(LPAREN)
        val ids = consumeListOfIdsSepByComma(tokens)
        tokens.consume(RPAREN)
        LambdaTupleArg(ids)
      case _ if tokens.nextSymbol().isInstanceOf[ID] =>
        val arg = LambdaSimpleArg(tokens.consume(classOf[ID]).value)
        if (!tokens.peek(DOTDOTDOT))
          arg
        else {
          tokens.consume(DOTDOTDOT)
          LambdaVariadicArg(arg.name)
        }
      case _ =>
        throw InvalidLambdaExpression(tokens.nextSymbol(), tokens.currentLine())
    }
  }

}