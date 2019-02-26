package parser.ast.expressions
import lexer.{TOKEN, TokenStream}

import scala.annotation.tailrec

abstract class RightAssociativeExpressionParser(nextLevel: ExpressionParser, oper: TOKEN)
  extends AssociativeExpressionParser(nextLevel, oper) {

  override def parse(tokens: TokenStream): Expression = {
    val expr = nextLevel.parse(tokens)
    if (!hasOper(tokens)) {
      expr
    } else {
      build(consumeArgs(tokens, List(expr)))
    }
  }

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
