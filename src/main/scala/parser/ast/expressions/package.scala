package parser.ast

import lexer._
import parser.Expression

package object expressions {

  def funcCallEndToken(tokens:TokenStream) : Boolean =
    tokens.nextToken() match {
      case None => true
      case Some(token) =>
        token match {
          case NL | INDENT | DEDENT | ASSIGN | DOLLAR | COMMA | LET | VAR | DO | THEN | ELSE |
            RPAREN | IN | RBRACKET | RCURLY | WHERE => true
          case pipe if pipe.isInstanceOf[PIPE_OPER] => true
          case oper if oper.isInstanceOf[OPER] => true
          case decl if decl.isInstanceOf[DECL] => true
          case _ => false
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
