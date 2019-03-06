package parser.ast.expressions.functions

import lexer.{ATOM, ID, TokenStream}
import parser.ast.expressions._
import parser.ast.expressions.literals.Atom

case class FunctionCallExpression(func: Expression, args:List[Expression]) extends CallExpression

object FunctionCallExpression extends ExpressionParser {

  override def parse(tokens: TokenStream): Expression = {
    val expr = parsePrim(tokens)
    if (funcCallEndToken(tokens)) {
      expr
    } else {
      FunctionCallExpression(expr, parseCallArgs(tokens, Nil))
    }
  }

  def parseCallArgs(tokens: TokenStream, args: List[Expression]) : List[Expression] = {
    if (funcCallEndToken(tokens)) {
      args.reverse
    }
    else {
      parseCallArgs(tokens, parsePrim(tokens) :: args)
    }
  }

  private[this] def parsePrim(tokens: TokenStream): Expression = {
    tokens.nextToken() match {
      case _:ID => Identifier(tokens.consume(classOf[ID]).value)
      case _:ATOM => Atom.parse(tokens)
      case _=>  FunctionCallWithDollarExpression.parse(tokens)
    }
  }
}