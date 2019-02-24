package parser.ast.expressions

import lexer.TokenStream
import parser.Expression

trait ExpressionParser {

  def parse(tokens: TokenStream) : Expression

}



