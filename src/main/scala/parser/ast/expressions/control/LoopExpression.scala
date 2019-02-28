package parser.ast.expressions.control

import lexer._
import parser._
import parser.ast.expressions._
import parser.ast.expressions.ExpressionParser
import parser.ast.expressions.functions.ForwardPipeFuncCallExpression
import parser.ast.expressions.logical.LogicalExpression

import scala.annotation.tailrec

case class LoopVarDecl(id: String, initialValue: Expression) extends LoopDeclVariable

trait LoopGuard extends Expression
case class WhileGuardExpr(comp:Expression) extends LoopGuard
case class UntilGuardExpr(comp:Expression) extends LoopGuard

case class LoopExpression(variables: List[LoopVarDecl], guard: Option[LoopGuard], body: Expression)
  extends ControlExpression


object LoopExpression extends ExpressionParser {

  def parse(tokens: TokenStream): Expression = {
    tokens.consume(LOOP)
    val loopDecls = parseLoopDecls(tokens)
    tokens.consumeOptionals(NL)
    val guardExpr = if (tokens.peek(WHILE)) {
      tokens.consume(WHILE)
      Some(WhileGuardExpr(LogicalExpression.parse(tokens)))
    }
    else if (tokens.peek(UNTIL)) {
      tokens.consume(UNTIL)
      Some(UntilGuardExpr(LogicalExpression.parse(tokens)))
    }
    else {
      None
    }

    tokens.consume(DO)
    LoopExpression(loopDecls, guardExpr, parsePipedOrBodyExpression(tokens))
  }

  def parseLoopDecls(tokens:TokenStream) : List[LoopVarDecl] = {
    consumeLoopVars(tokens, List(parseLoopVarDecl(tokens)))
  }

  @tailrec
  private[this] def consumeLoopVars(tokens: TokenStream, vars: List[LoopVarDecl]): List[LoopVarDecl] = {
    if (!tokens.peek(COMMA)) {
      vars.reverse
    }
    else {
      tokens.consume(COMMA)
      consumeLoopVars(tokens, parseLoopVarDecl(tokens) :: vars)
    }
  }

  private[this] def parseLoopVarDecl(tokens:TokenStream) : LoopVarDecl = {
    val id = tokens.consume(classOf[ID])
    if (tokens.peek(ASSIGN)) {
      tokens.consume(ASSIGN)
      LoopVarDecl(id.value, ForwardPipeFuncCallExpression.parse(tokens))
    } else {
      throw UnexpectedTokenClassException(tokens.nextToken())
    }
  }

}