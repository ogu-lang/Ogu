package parser.ast.expressions.functions

import lexer.{ATOM, ID, TokenStream}
import parser.InvalidExpression
import parser.ast.expressions._
import parser.ast.expressions.literals.Atom

case class FunctionCallExpression(func: Expression, args:List[Expression]) extends CallExpression

object FunctionCallExpression extends ExpressionParser {

  override def parse(tokens: TokenStream): Expression = {
    val expr: Expression = if (tokens.peek(classOf[ID])) {
      Identifier(tokens.consume(classOf[ID]).value)
    }
    else if (tokens.peek(classOf[ATOM])) {
      Atom.parse(tokens)
    } else {
      println(s"@@@@!!!${tokens}")
      throw InvalidExpression(tokens.nextToken())
    }
    if (funcCallEndToken(tokens)) {
      expr
    } else {
      FunctionCallExpression(expr, parseCallArgs(tokens, Nil))
    }
  }

  private[this] def parseCallArgs(tokens: TokenStream, args: List[Expression]) : List[Expression] = {
    if (funcCallEndToken(tokens)) {
      args.reverse
    }
    else {
      val expr = if (tokens.peek(classOf[ID])) {
         Identifier(tokens.consume(classOf[ID]).value)
      } else {
        FunctionCallWithDollarExpression.parse(tokens)
      }
      parseCallArgs(tokens, expr :: args)
    }
  }

}