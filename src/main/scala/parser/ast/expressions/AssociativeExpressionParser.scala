package parser.ast.expressions

import lexer.{SYMBOL, TokenStream}

abstract class AssociativeExpressionParser(nextLevel: ExpressionParser, oper: SYMBOL) extends ExpressionParser {

  def build(args: List[Expression]): Expression

  def hasOper(tokens: TokenStream): Boolean = tokens.peek(oper)

  def consumeOper(tokens: TokenStream): Unit = tokens.consume(oper)

}
