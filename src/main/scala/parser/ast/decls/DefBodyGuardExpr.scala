package parser.ast.decls

import lexer._
import parser.ast.expressions._
import parser.ast.expressions.Expression
import parser.ast.expressions.logical.LogicalExpression

trait DefBodyGuardExpr

case class DefBodyGuardExpression(comp: Expression, body: Expression) extends DefBodyGuardExpr
case class DefBodyGuardOtherwiseExpression(body: Expression) extends DefBodyGuardExpr
case class BodyGuardsExpresion(guards: List[DefBodyGuardExpr]) extends Expression
case class BodyGuardsExpresionAndWhere(guards: List[DefBodyGuardExpr], whereBlock: WhereBlock) extends Expression

object DefBodyGuardExpr {

  def parse(tokens: TokenStream): Expression = {
    tokens.consume(INDENT)
    val listOfGuards = parseDefBodyGuards(tokens, List(parseBodyGuard(tokens)))
    val result = if (tokens.peek(WHERE)) {
      BodyGuardsExpresionAndWhere(listOfGuards, WhereBlock.parseUnindented(tokens))
    } else {
      BodyGuardsExpresion(listOfGuards)
    }
    tokens.consume(DEDENT)
    result
  }

  private[this] def parseDefBodyGuards(tokens: TokenStream, guards: List[DefBodyGuardExpr]): List[DefBodyGuardExpr] = {
    if (!tokens.peek(GUARD)) {
      guards.reverse
    }
    else {
      parseDefBodyGuards(tokens, parseBodyGuard(tokens) :: guards)
    }
  }

  private[this] def parseBodyGuard(tokens: TokenStream): DefBodyGuardExpr = {
    tokens.consume(GUARD)
    if (tokens.peek(OTHERWISE)) {
      tokens.consume(OTHERWISE)
      tokens.consume(ASSIGN)
      val body = parsePipedOrBodyExpression(tokens)
      tokens.consumeOptionals(NL)
      DefBodyGuardOtherwiseExpression(body)

    } else {
      val guardExpr = LogicalExpression.parse(tokens)
      tokens.consume(ASSIGN)
      val body = parsePipedOrBodyExpression(tokens)
      tokens.consumeOptionals(NL)
      DefBodyGuardExpression(guardExpr, body)
    }
  }
}