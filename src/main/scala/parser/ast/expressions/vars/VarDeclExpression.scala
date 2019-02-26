package parser.ast.expressions.vars

import lexer.{TokenStream, VAR}
import parser.ast.expressions.{Expression, ExpressionParser}

case class VarDeclExpression(decls: List[Variable], inExpr: Option[Expression]) extends Expression

object VarDeclExpression extends ExpressionParser {

  override def parse(tokens: TokenStream): Expression = {
    VarDeclExpression(VariableParser.parseListOfLetVars(tokens, VAR), VariableParser.parseInBodyOptExpr(tokens))

  }

}