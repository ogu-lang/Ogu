package parser.ast.expressions

import lexer.{TOKEN, TokenStream}

abstract class AssociativeExpressionParser(nextLevel: ExpressionParser, oper: TOKEN) extends ExpressionParser {

  def build(args: List[Expression]): Expression

  def hasOper(tokens: TokenStream): Boolean = tokens.peek(oper)

  def consumeOper(tokens: TokenStream): Unit = tokens.consume(oper)

}
