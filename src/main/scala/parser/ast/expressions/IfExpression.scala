package parser.ast.expressions

import lexer._
import parser.{ControlExpression, Expression}

import scala.annotation.tailrec


case class IfExpression(comp: Expression, thenPart: Expression, elifPart: List[ElifPart], elsePart: Expression)
  extends ControlExpression

case class ElifPart(comp: Expression, body: Expression)

object IfExpression extends ExpressionParser {

  def parse(tokens: TokenStream): Expression = {
    tokens.consume(IF)
    val comp = LogicalExpression.parse(tokens)
    tokens.consume(THEN)
    val thenPart = parsePipedOrBodyExpression(tokens)
    tokens.consumeOptionals(NL)
    val elif = if (tokens.peek(ELIF)) consumeElifPart(tokens, Nil) else Nil
    tokens.consume(ELSE)
    IfExpression(comp, thenPart, elif, parsePipedOrBodyExpression(tokens))
  }

  @tailrec
  private[this] def consumeElifPart(tokens: TokenStream, elifs: List[ElifPart]): List[ElifPart] = {
    if (!tokens.peek(ELIF)) {
      elifs.reverse
    }
    else {
      tokens.consume(ELIF)
      val elifComp = LogicalExpression.parse(tokens)
      tokens.consume(THEN)
      consumeElifPart(tokens, ElifPart(elifComp, parsePipedOrBodyExpression(tokens)) :: elifs)
    }
  }
}
