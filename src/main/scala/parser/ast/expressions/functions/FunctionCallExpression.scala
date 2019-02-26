package parser.ast.expressions.functions

import lexer.{ATOM, ID, TokenStream}
import parser.InvalidExpression
import parser.ast.expressions.literals.Atom
import parser.ast.expressions._

case class FunctionCallExpression(func: Expression, args:List[Expression]) extends CallExpression


object FunctionCallExpression extends ExpressionParser {

  override def parse(tokens: TokenStream): Expression = {
    var expr: Expression = null
    if (tokens.peek(classOf[ID])) {
      val id = tokens.consume(classOf[ID])
      expr = Identifier(id.value)
    }
    else if (tokens.peek(classOf[ATOM])) {
      expr = Atom.parse(tokens)
    }
    if (!funcCallEndToken(tokens)) {
      var args = List.empty[Expression]
      val func = expr
      while (!funcCallEndToken(tokens)) {
        if (tokens.peek(classOf[ID])) {
          val id = tokens.consume(classOf[ID])
          expr = Identifier(id.value)
        } else {
          expr = FunctionCallWithDollarExpression.parse(tokens)
        }
        args = expr :: args
      }
      FunctionCallExpression(func, args.reverse)
    } else {
      if (expr == null) {
        println(s"@@INVALID EXPRESSION TOKENS= $tokens")
        throw InvalidExpression()
      }
      expr
    }
  }

}