package parser.ast.expressions.types

import lexer._
import parser.ast._
import parser.ast.expressions._
import parser.ast.expressions.arithmetic.SubstractExpression
import parser.ast.expressions.functions.ForwardPipeFuncCallExpression
import parser.ast.expressions.logical.LogicalExpression

import scala.annotation.tailrec

trait ValidRangeExpression extends Expression
case class RangeExpression(rangeInit:Expression, rangeEnd:Expression) extends ValidRangeExpression
case class RangeExpressionUntil(rangeInit:Expression, rangeEnd:Expression) extends ValidRangeExpression
case class RangeWithIncrementExpression(rangeInit:Expression, rangeIncrement: Expression, rangeEnd:Expression) extends ValidRangeExpression
case class RangeWithIncrementExpressionUntil(rangeInit:Expression, rangeIncrement: Expression, rangeEnd:Expression) extends ValidRangeExpression
case class InfiniteRangeExpression(rangeInit: Expression) extends ValidRangeExpression
case class InfiniteRangeWithIncrementExpression(rangeInit: Expression, rangeIncrement: Expression) extends ValidRangeExpression
object EmptyListExpresion extends ValidRangeExpression

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
      EmptyListExpresion
    }
    else {
      val exprs = parseListOfCommaSeparatedExpressions(tokens)
      val expr = if (1 == exprs.length) exprs(0) else ListExpression(exprs, None)
      val finalExpr = tokens.nextToken() match {
        case DOTDOT =>
          tokens.consume(DOTDOT)
          parseEndRange(tokens, expr, include = true)
        case DOTDOTLESS =>
          tokens.consume(DOTDOTLESS)
          parseEndRange(tokens, expr, include = false)
        case DOTDOTDOT =>
          tokens.consume(DOTDOTDOT)
          InfiniteRangeExpression(expr)
        case GUARD =>
          tokens.consume(GUARD)
          ListExpression(List(expr), Some(consumeListGuards(tokens, List(parseListGuard(tokens)))))
        case _ =>
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
      case ListExpression(exprs, _) if 2 == exprs.size  =>
        val rangeInit = exprs.head
        val rangeEnd = exprs(1)
        val rangeIncrement = SubstractExpression(List(rangeEnd, rangeInit))
        if (include)
          RangeWithIncrementExpression(rangeInit, rangeIncrement, LogicalExpression.parse(tokens))
        else
          RangeWithIncrementExpressionUntil(rangeInit, rangeIncrement, LogicalExpression.parse(tokens))
      case rangeInit: Expression =>
        if (include)
          RangeExpression(rangeInit, LogicalExpression.parse(tokens))
        else
          RangeExpressionUntil(rangeInit, LogicalExpression.parse(tokens))
    }
  }

  def parseListGuard(tokens: TokenStream): ListGuard = {
    tokens.nextToken() match {
      case LPAREN if tokens.peek(2, classOf[ID]) && tokens.peek(3, COMMA) =>
        tokens.consume(LPAREN)
        val listOfIds = consumeListOfIdsSepByComma(tokens)
        tokens.consume(RPAREN)
        tokens.consume(BACK_ARROW)
        ListGuardDeclTupled(listOfIds, ForwardPipeFuncCallExpression.parse(tokens))
      case _: ID if tokens.peek(2, BACK_ARROW) =>
        val id = tokens.consume(classOf[ID]).value
        tokens.consume(BACK_ARROW)
        ListGuardDecl(id, ForwardPipeFuncCallExpression.parse(tokens))
      case _ =>
        ListGuardExpr(ForwardPipeFuncCallExpression.parse(tokens))
    }
  }

}