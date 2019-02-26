package parser.ast.decls

import lexer._
import parser.InvalidDef
import parser.ast.{LangNode, consumeListOfIdsSepByComma}
import parser.ast.expressions.{Expression, Identifier, parsePipedOrBodyExpression}
import parser.ast.expressions.logical.LogicalExpression

trait WhereDef
case class WhereDefSimple(id: String, args: Option[List[Expression]], body: Expression) extends WhereDef
case class WhereDefTupled(idList: List[String], args: Option[List[Expression]], body: Expression) extends WhereDef
case class WhereDefWithGuards(id: String, args: Option[List[Expression]], guards: List[WhereGuard]) extends WhereDef
case class WhereDefTupledWithGuards(idList: List[String], args: Option[List[Expression]], guards: List[WhereGuard]) extends WhereDef

object WhereDef {

  def parse(tokens:TokenStream): WhereDef = {
    val listOfIds = if (!tokens.peek(LPAREN)) {
      List(tokens.consume(classOf[ID]).value)
    } else {
      tokens.consume(LPAREN)
      val l = consumeListOfIdsSepByComma(tokens)
      tokens.consume(RPAREN)
      l
    }
    var listOfArgs = List.empty[Expression]
    while (!tokens.peek(ASSIGN) && !tokens.peek(GUARD) && !tokens.peek(NL)) {
      val expr = parseWhereArg(tokens)
      listOfArgs = expr :: listOfArgs
    }
    if (tokens.peek(ASSIGN)) {
      tokens.consume(ASSIGN)
      val body = parsePipedOrBodyExpression(tokens)
      if (listOfIds.size == 1)
        WhereDefSimple(listOfIds.head, if (listOfArgs.isEmpty) None else Some(listOfArgs.reverse), body)
      else
        WhereDefTupled(listOfIds, if (listOfArgs.isEmpty) None else Some(listOfArgs.reverse), body)
    }
    else if (tokens.peek(GUARD) || tokens.peek(NL)) {
      var inIndent = false
      tokens.consumeOptionals(NL)
      if (tokens.peek(INDENT)) {
        inIndent = true
        tokens.consume(INDENT)
      }
      var guards = List.empty[WhereGuard]
      while (tokens.peek(GUARD) || tokens.peek(INDENT)) {
        if (!tokens.peek(INDENT)) {
          guards = parseWhereGuard(tokens) :: guards
        }
        else {
          tokens.consume(INDENT)
          while (tokens.peek(GUARD)) {
            guards = parseWhereGuard(tokens) :: guards
          }
          tokens.consume(DEDENT)
        }
      }

      if (inIndent) {
        tokens.consume(DEDENT)
      }
      if (listOfIds.size == 1)
        WhereDefWithGuards(listOfIds.head, if (listOfArgs.isEmpty) None else Some(listOfArgs), guards.reverse)
      else
        WhereDefTupledWithGuards(listOfIds, if (listOfArgs.isEmpty) None else Some(listOfArgs), guards.reverse)
    }
    else {
      throw InvalidDef()
    }
  }

  private[this] def parseWhereGuard(tokens:TokenStream): WhereGuard = {
    tokens.consume(GUARD)
    val comp = if (tokens.peek(OTHERWISE)) {
      tokens.consume(OTHERWISE)
      None
    } else {
      Some(LogicalExpression.parse(tokens))
    }
    tokens.consume(ASSIGN)
    val body = parsePipedOrBodyExpression(tokens)
    tokens.consume(NL)
    WhereGuard(comp, body)
  }

  private[this] def parseWhereArg(tokens: TokenStream) : Expression = {
    if (tokens.peek(classOf[ID])) {
      Identifier(tokens.consume(classOf[ID]).value)
    } else {
      LogicalExpression.parse(tokens)
    }
  }

}
