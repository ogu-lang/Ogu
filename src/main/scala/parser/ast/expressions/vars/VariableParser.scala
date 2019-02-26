package parser.ast.expressions.vars

import lexer._
import parser.ast.{LetId, LetSimpleId, LetTupledId}
import parser.ast.expressions.functions.ForwardPipeFuncCallExpression
import parser.ast.expressions.{BlockExpression, Expression, ExpressionParser, parsePipedOrBodyExpression}

trait Variable

object VariableParser {

  def parseListOfLetVars(tokens: TokenStream, token: TOKEN): List[Variable] = {
    tokens.consume(token)
    tokens.consumeOptionals(NL)
    var insideIndent = if (tokens.peek(INDENT)) 1 else 0
    if (insideIndent == 1)
      tokens.consume(INDENT)
    var letVar = parseLetVar(tokens)
    var listOfLetVars = List.empty[Variable]
    listOfLetVars = letVar :: listOfLetVars
    while (tokens.peek(COMMA)) {
      tokens.consume(COMMA)
      tokens.consumeOptionals(NL)
      if (tokens.peek(INDENT)) {
        tokens.consume(INDENT)
        insideIndent += 1
      }
      letVar = parseLetVar(tokens)
      listOfLetVars = letVar :: listOfLetVars
    }

    while (insideIndent > 0) {
      tokens.consumeOptionals(NL)
      tokens.consume(DEDENT)
      insideIndent -= 1
    }
    listOfLetVars.reverse
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
      val idToken = tokens.consume(classOf[ID])
      LetSimpleId(idToken.value)
    } else {
      tokens.consume(LPAREN)
      var ids = List.empty[LetId]
      val id = parseLetId(tokens)
      ids =id :: ids
      while (tokens.peek(COMMA)) {
        tokens.consume(COMMA)
        val id = parseLetId(tokens)
        ids = id :: ids
      }
      tokens.consume(RPAREN)
      LetTupledId(ids)
    }
  }

}