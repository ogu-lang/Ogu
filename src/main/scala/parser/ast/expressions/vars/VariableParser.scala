package parser.ast.expressions.vars

import lexer._
import parser.ast.expressions.functions.ForwardPipeFuncCallExpression
import parser.ast.expressions.{BlockExpression, Expression, parsePipedOrBodyExpression}

import scala.annotation.tailrec

trait Variable

object VariableParser {

  def parseListOfLetVars(tokens: TokenStream, token: TOKEN): List[Variable] = {
    tokens.consume(token)
    tokens.consumeOptionals(NL)
    var initIndent = if (tokens.peek(INDENT)) 1 else 0
    if (initIndent == 1)
      tokens.consume(INDENT)
    var letVar = parseLetVar(tokens)
    val (insideIndent, listOfLetVars) = parseListOfLetVars(tokens, initIndent, List(letVar))
    tokens.consumeOptionals(NL)
    tokens.consume(insideIndent, DEDENT)
    listOfLetVars
  }

  def parseInBodyOptExpr(tokens:TokenStream) : Option[Expression] = {
    if (tokens.peek(IN)) {
      parseInBodyExpr(tokens)
    } else if (tokens.peek(NL) && tokens.peek(2, IN)) {
      tokens.consume(NL)
      parseInBodyExpr(tokens)
    } else if (tokens.peek(NL) && tokens.peek(2, INDENT) && tokens.peek(3, IN)) {
      tokens.consume(NL)
      tokens.consume(INDENT)
      val result = parseInBodyExpr(tokens)
      tokens.consume(DEDENT)
      result
    } else {
      None
    }
  }

  @tailrec
  private[this]
  def parseListOfLetVars(tokens: TokenStream, indent: Int, vars: List[Variable]) : (Int, List[Variable]) = {
    if (!tokens.peek(COMMA)) {
      (indent, vars.reverse)
    }
    else {
      tokens.consume(COMMA)
      tokens.consumeOptionals(NL)
      if (!tokens.peek(INDENT)) {
        parseListOfLetVars(tokens, indent, parseLetVar(tokens) :: vars)
      }
      else {
        tokens.consume(INDENT)
        parseListOfLetVars(tokens, indent+1, parseLetVar(tokens) :: vars)
      }
    }
  }


  private[this] def parseLetVar(tokens:TokenStream) : Variable = {
    tokens.consumeOptionals(NL)
    val id = parseLetId(tokens)
    tokens.consume(ASSIGN)
    val expr = parsePipedOrBodyExpression(tokens)
    LetVariable(id, expr)
  }

  private[this] def parseInBodyExpr(tokens:TokenStream): Option[Expression] = {
    tokens.consume(IN)
    if (!tokens.peek(NL)) {
      Some(ForwardPipeFuncCallExpression.parse(tokens))
    } else {
      tokens.consume(NL)
      Some(BlockExpression.parse(tokens))
    }
  }

  private[this] def parseLetId(tokens:TokenStream) : LetId = {
    if (!tokens.peek(LPAREN)) {
      LetSimpleId(tokens.consume(classOf[ID]).value)
    } else {
      tokens.consume(LPAREN)
      val ids = consumeListOfLetIds(tokens, List(parseLetId(tokens)))
      tokens.consume(RPAREN)
      LetTupledId(ids)
    }
  }

  @tailrec
  private[this] def consumeListOfLetIds(tokens: TokenStream, ids: List[LetId]) : List[LetId] = {
    if (!tokens.peek(COMMA)) {
      ids.reverse
    }
    else {
      tokens.consume(COMMA)
      consumeListOfLetIds(tokens, parseLetId(tokens) :: ids)
    }
  }

}