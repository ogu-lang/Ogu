package parser.ast.expressions

import lexer.TokenStream

trait ExpressionParser {

  def parse(tokens: TokenStream) : Expression

}



