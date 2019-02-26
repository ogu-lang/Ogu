package parser.ast.expressions

import lexer.{TOKEN, TokenStream}

import scala.annotation.tailrec

abstract class LeftAssociativeExpressionParser(nextLevel: ExpressionParser, oper: TOKEN) extends ExpressionParser {

  def parse(tokens: TokenStream): Expression = {
    val expr = nextLevel.parse(tokens)
    if (!hasOper(tokens)) {
      expr
    }
    else {
      build(consumeArgs(tokens, nextLevel,  List(expr)))
    }
  }

  def build(args: List[Expression]): Expression

  def hasOper(tokens: TokenStream): Boolean = tokens.peek(oper)

  def consumeOper(tokens: TokenStream): Unit = tokens.consume(oper)

  @tailrec
  private[this] def consumeArgs(tokens: TokenStream, nextLevel: ExpressionParser, args: List[Expression]): List[Expression] = {
    if (!hasOper(tokens)) {
      args.reverse
    }
    else {
      consumeOper(tokens)
      consumeArgs(tokens, nextLevel, nextLevel.parse(tokens) :: args)
    }
  }

}
