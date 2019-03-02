package parser.ast.decls

import lexer._
import parser.ast.expressions.logical.LogicalExpression
import parser.ast.expressions.{Expression, Identifier}

import scala.annotation.tailrec

case class DefArg(expression: Expression)

object DefOtherwiseArg extends DefArg(null)

case class IdIsType(id: String, cl: String) extends Expression
case class VariadicArg(id: String) extends Expression

object DefArg {

  def parse(tokens:TokenStream) : DefArg = {
    if (!tokens.peek(classOf[ID])) {
      DefArg(LogicalExpression.parse(tokens))
    }
    else {
      val id = tokens.consume(classOf[ID]).value
      tokens.nextToken() match {
        case COLON =>
          tokens.consume(COLON)
          DefArg(IdIsType(id, tokens.consume(classOf[TID]).value))
        case DOTDOTDOT =>
          tokens.consume(DOTDOTDOT)
          DefArg(VariadicArg(id))
        case _ =>
          DefArg(Identifier(id))
      }
    }
  }

  def parseDefArgs(tokens: TokenStream): (List[DefArg], List[DefArg]) = consumeDefArgs(tokens, Nil, Nil)

  @tailrec
  private[this]
  def consumeDefArgs(tokens: TokenStream, args: List[DefArg], before: List[DefArg]): (List[DefArg], List[DefArg]) = {
    if (tokens.peek(ASSIGN) || tokens.peek(NL)) {
      (args.reverse, before.reverse)
    }
    else {
      val (newArgs, newBefore) =
        if (!tokens.peek(QUESTION)) {
          (args, before)
        }
        else {
          tokens.consume(QUESTION)
          (Nil, args ++ before)
        }
      if (tokens.peek(OTHERWISE)) {
        tokens.consume(OTHERWISE)
        consumeDefArgs(tokens, DefOtherwiseArg :: newArgs, newBefore)

      } else {
        consumeDefArgs(tokens, DefArg.parse(tokens) :: newArgs, newBefore)
      }
    }
  }

}