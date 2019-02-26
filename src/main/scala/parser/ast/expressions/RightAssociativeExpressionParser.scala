package parser.ast.expressions
import lexer.{TOKEN, TokenStream}

import scala.annotation.tailrec

abstract class RightAssociativeExpressionParser(nextLevel: ExpressionParser, oper: TOKEN) extends ExpressionParser {

  override def parse(tokens: TokenStream): Expression = {
    val expr = nextLevel.parse(tokens)
    if (!hasOper(tokens)) {
      expr
    } else {
      build(consumeArgs(tokens, List(expr)))
    }
  }

  def build(args: List[Expression]): Expression

  def hasOper(tokens: TokenStream): Boolean = tokens.peek(oper)

  def consumeOper(tokens: TokenStream): Unit = tokens.consume(oper)

  @tailrec
  private[this] def consumeArgs(tokens: TokenStream, expressions: List[Expression]): List[Expression] = {
    if (!hasOper(tokens)) {
      expressions.reverse
    }
    else {
      consumeOper(tokens)
      consumeArgs(tokens, parse(tokens) :: expressions)
    }
  }

}
