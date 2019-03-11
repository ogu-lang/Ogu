package parser.ast.expressions.vars

import exceptions.InvalidExpression
import lexer.{BIND, TokenStream}
import parser.ast.expressions.{Expression, ExpressionParser}

case class BindDeclExpression(decls: List[LetVariable], inExpr: Expression) extends Expression

object BindDeclExpression extends ExpressionParser {

  override def parse(tokens: TokenStream): Expression = {
    val listOfLetVars = VariableParser.parseListOfLetVars(tokens, BIND)
    VariableParser.parseInBodyOptExpr(tokens) match {
      case None => throw InvalidExpression(tokens.nextSymbol(), tokens.currentLine())
      case Some(body) => BindDeclExpression(listOfLetVars.reverse, body)
    }
  }

}
