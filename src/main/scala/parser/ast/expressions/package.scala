package parser.ast

import lexer._
import parser.Expression

package object expressions {

  def funcCallEndToken(tokens:TokenStream) : Boolean = if (tokens.isEmpty) true else {
      tokens.nextToken().exists { next =>
        next == NL || next.isInstanceOf[PIPE_OPER] || next.isInstanceOf[OPER] || next.isInstanceOf[DECL] ||
          next == INDENT || next == DEDENT || next == ASSIGN ||
          next == DOLLAR || next == COMMA || next == LET || next == VAR || next == DO || next == THEN ||
          next == ELSE || next == RPAREN || next == IN || next == RBRACKET || next == RCURLY || next == WHERE
      }
    }

  def parsePipedOrBodyExpression(tokens:TokenStream): Expression = if (!tokens.peek(NL))
    ForwardPipeFuncCallExpression.parse(tokens)
  else {
    tokens.consume(NL)
    BlockExpression.parse(tokens)
  }

  def parseListOfExpressions(tokens:TokenStream) : List[Expression] = {
    consumeListOfExpression(tokens, ParseExpr, List(ParseExpr.parse(tokens)))
  }

  def parseListOfPipedExpressions(tokens:TokenStream) : List[Expression] = {
    consumeListOfExpression(tokens, ForwardPipeFuncCallExpression, List(ForwardPipeFuncCallExpression.parse(tokens)))
  }

  private[this]
  def consumeListOfExpression(tokens: TokenStream, parser: ExpressionParser, expressions: List[Expression])
  : List[Expression] = {
    if (!tokens.peek(COMMA)) {
      expressions.reverse
    }
    else {
      tokens.consume(COMMA)
      tokens.consumeOptionals(NL)
      consumeListOfExpression(tokens, parser, parser.parse(tokens) :: expressions)
    }
  }


}
