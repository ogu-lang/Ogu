package parser.ast.expressions

import lexer._
import parser.Expression
import parser.ast._

import scala.annotation.tailrec

trait ValidRangeExpression extends Expression
case class RangeExpression(rangeInit:Expression, rangeEnd:Expression) extends ValidRangeExpression
case class RangeExpressionUntil(rangeInit:Expression, rangeEnd:Expression) extends ValidRangeExpression
case class RangeWithIncrementExpression(rangeInit:Expression, rangeIncrement: Expression, rangeEnd:Expression) extends ValidRangeExpression
case class RangeWithIncrementExpressionUntil(rangeInit:Expression, rangeIncrement: Expression, rangeEnd:Expression) extends ValidRangeExpression
case class InfiniteRangeExpression(rangeInit: Expression) extends ValidRangeExpression
case class InfiniteRangeWithIncrementExpression(rangeInit: Expression, rangeIncrement: Expression)
case class EmptyListExpresion() extends ValidRangeExpression

trait ListGuard
case class ListGuardDecl(id: String, value: Expression) extends ListGuard
case class ListGuardExpr(value: Expression) extends ListGuard
case class ListGuardDeclTupled(ids: List[String], value: Expression) extends ListGuard


case class ListExpression(expressions: List[Expression], guards: Option[List[ListGuard]]) extends ValidRangeExpression

object ListExpression extends ExpressionParser {

  override def parse(tokens: TokenStream): Expression = {
    tokens.consume(LBRACKET)
    if (tokens.peek(RBRACKET)) {
      tokens.consume(RBRACKET)
      EmptyListExpresion()
    }
    else {
      val exprs = parseListOfCommaSeparatedExpressions(tokens)
      val expr = if (exprs.length == 1) exprs.head else ListExpression(exprs, None)
      val finalExpr = if (tokens.peek(DOTDOT)) {
        tokens.consume(DOTDOT)
        parseEndRange(tokens, expr, include = true)
      }
      else if (tokens.peek(DOTDOTLESS)) {
        tokens.consume(DOTDOTLESS)
        parseEndRange(tokens, expr, include = false)
      }
      else if (tokens.peek(DOTDOTDOT)) {
        tokens.consume(DOTDOTDOT)
        InfiniteRangeExpression(expr)
      }
      else if (tokens.peek(GUARD)) {
        tokens.consume(GUARD)
        ListExpression(List(expr), Some(consumeListGuards(tokens, List(parseListGuard(tokens)))))
      } else {
        expr
      }
      tokens.consume(RBRACKET)
      if (!finalExpr.isInstanceOf[ValidRangeExpression])
        ListExpression(List(finalExpr), None)
      else
        finalExpr
    }
  }

  @tailrec
  private[this] def consumeListGuards(tokens: TokenStream, guards: List[ListGuard]): List[ListGuard] = {
    if (!tokens.peek(COMMA)) {
      guards.reverse
    } else {
      tokens.consume(COMMA)
      consumeListGuards(tokens, parseListGuard(tokens) :: guards)
    }
  }

  private[this] def parseEndRange(tokens: TokenStream, expression: Expression, include: Boolean): Expression = {
    expression match {
      case ListExpression(exprs, _) if exprs.size == 2 =>
        val rangeInit = exprs(0)
        val rangeEnd = exprs(1)
        val rangeIncrement = SubstractExpression(List(rangeEnd, rangeInit))
        if (include)
          RangeWithIncrementExpression(rangeInit, rangeIncrement, LogicalExpression.parse(tokens))
        else
          RangeWithIncrementExpressionUntil(rangeInit, rangeIncrement, LogicalExpression.parse(tokens))
      case rangeInit =>
        if (include)
          RangeExpression(rangeInit, LogicalExpression.parse(tokens))
        else
          RangeExpressionUntil(rangeInit, LogicalExpression.parse(tokens))
    }
  }

  def parseListGuard(tokens: TokenStream): ListGuard = {
    if (tokens.peek(LPAREN) && tokens.peek(2, classOf[ID]) && tokens.peek(3, COMMA)) {
      tokens.consume(LPAREN)
      val listOfIds = consumeListOfIdsSepByComma(tokens)
      tokens.consume(RPAREN)
      tokens.consume(BACK_ARROW)
      val expr = ForwardPipeFuncCallExpression.parse(tokens)
      ListGuardDeclTupled(listOfIds, expr)
    }
    else if (tokens.peek(classOf[ID]) && tokens.peek(2, BACK_ARROW)) {
      val id = tokens.consume(classOf[ID]).value
      tokens.consume(BACK_ARROW)
      val expr = ForwardPipeFuncCallExpression.parse(tokens)
      ListGuardDecl(id, expr)
    } else {
      ListGuardExpr(ForwardPipeFuncCallExpression.parse(tokens))
    }
  }

}