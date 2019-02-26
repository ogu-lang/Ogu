package parser.ast.expressions.vars

import lexer.{BIND, TokenStream}
import parser.InvalidExpression
import parser.ast.expressions.{Expression, ExpressionParser}

case class BindDeclExpression(decls: List[Variable], inExpr: Expression) extends Expression

object BindDeclExpression extends ExpressionParser {

  override def parse(tokens: TokenStream): Expression = {
    val listOfLetVars = VariableParser.parseListOfLetVars(tokens, BIND)
    VariableParser.parseInBodyOptExpr(tokens) match {
      case None => throw InvalidExpression()
      case Some(body) => BindDeclExpression(listOfLetVars.reverse, body)
    }
  }

}
