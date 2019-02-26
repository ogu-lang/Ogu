package parser.ast.expressions.vars

import lexer.{LET, TokenStream}
import parser.ast.expressions.{Expression, ExpressionParser}

case class LetDeclExpression(decls: List[Variable], inExpr: Option[Expression]) extends LetDeclExpressionTrait

object LetDeclExpression extends ExpressionParser {

  override def parse(tokens: TokenStream): Expression = {
      LetDeclExpression(VariableParser.parseListOfLetVars(tokens, LET), VariableParser.parseInBodyOptExpr(tokens))
  }

}
