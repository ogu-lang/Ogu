package parser.ast.decls

import exceptions.InvalidDef
import lexer._
import parser.ast.consumeListOfIdsSepByComma
import parser.ast.expressions._
import parser.ast.expressions.logical.LogicalExpression

trait WhereDef
case class WhereDefSimple(id: String, args: Option[List[Expression]], body: Expression) extends WhereDef
case class WhereDefTupled(idList: List[String], args: Option[List[Expression]], body: Expression) extends WhereDef
case class WhereDefWithGuards(id: String, args: Option[List[Expression]], guards: List[WhereGuard]) extends WhereDef
case class WhereDefTupledWithGuards(idList: List[String], args: Option[List[Expression]], guards: List[WhereGuard]) extends WhereDef

object WhereDef {

  def parse(tokens:TokenStream): WhereDef = {
    val listOfIds = parseListOfIds(tokens)
    val listOfArgs = parseListOfArgs(tokens, Nil)

    tokens.nextToken() match {
      case ASSIGN =>
        tokens.consume(ASSIGN)
        val body = parsePipedOrBodyExpression(tokens)
        listOfIds.size match {
          case 1 =>  WhereDefSimple(listOfIds.head, listOfArgs, body)
          case _ => WhereDefTupled(listOfIds, listOfArgs, body)
        }

      case GUARD | NL =>
        tokens.consumeOptionals(NL)
        val indent = tokens.peek(INDENT)
        if (indent) tokens.consume(INDENT)
        val guards = parseListOfWhereGuards(tokens, Nil)
        if (indent) tokens.consume(DEDENT)
        listOfIds.size match {
          case 1 => WhereDefWithGuards(listOfIds.head, listOfArgs, guards)
          case _ => WhereDefTupledWithGuards(listOfIds, listOfArgs, guards)
        }

      case _ => throw InvalidDef()
    }
  }

  private[this] def parseListOfIds(tokens: TokenStream) : List[String] = {
    if (!tokens.peek(LPAREN)) {
      List(tokens.consume(classOf[ID]).value)
    } else {
      tokens.consume(LPAREN)
      val list = consumeListOfIdsSepByComma(tokens)
      tokens.consume(RPAREN)
      list
    }
  }

  private[this] def parseListOfArgs(tokens: TokenStream, args: List[Expression]) : Option[List[Expression]] = {
    if (tokens.peek(ASSIGN) || tokens.peek(GUARD) || tokens.peek(NL)) {
      if (args.isEmpty) None else Some(args.reverse)
    }
    else {
      parseListOfArgs(tokens, parseWhereArg(tokens) :: args)
    }
  }

  private[this] def parseListOfWhereGuards(tokens: TokenStream, guards: List[WhereGuard]) : List[WhereGuard] = {
    tokens.nextToken() match {
      case GUARD =>
        parseListOfWhereGuards(tokens, parseWhereGuard(tokens) :: guards)
      case INDENT =>
        tokens.consume(INDENT)
        val result = parseListOfWhereGuards(tokens, guards).reverse
        tokens.consume(DEDENT)
        parseListOfWhereGuards(tokens, result)
      case _ => guards.reverse
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
