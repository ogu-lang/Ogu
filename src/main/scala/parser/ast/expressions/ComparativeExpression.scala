package parser.ast.expressions

import lexer.TokenStream
import parser.Expression
import parser.ast.module.Module

object ComparativeExpression extends ExpressionParser {

  def parse(tokens: TokenStream): Expression = Module.parseComparativeExpr(tokens)

}
