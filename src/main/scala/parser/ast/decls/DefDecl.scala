package parser.ast.decls

import lexer._
import parser.InvalidDef
import parser.ast._
import parser.ast.expressions._
import parser.ast.expressions.functions.ForwardPipeFuncCallExpression
import parser.ast.expressions.logical.LogicalExpression

class DefDecl(id: String) extends LangNode

object DefDecl {

  def parse(inner: Boolean, tokens: TokenStream): DefDecl = {
    tokens.consume(DEF)
    val defId = tokens.consume(classOf[ID]).value
    val (dispatchers, args) = parseDefArgs(tokens)
    tokens.nextToken() match {
      case None => throw InvalidDef()
      case Some(token) =>
        token match {
          case ASSIGN =>
            tokens.consume(ASSIGN)
            val body = parsePipedOrBodyExpression(tokens)
            val where = tryParseWhereBlock(tokens)
            dispatchers match {
              case None => SimpleDefDecl(inner, defId, args, body, where)
              case Some(d) => MultiMethod(inner, defId, d, args, body, where)
            }
          case NL =>
            tokens.consume(NL)
            val body = parseDefBodyGuards(tokens)
            body match {
              case BodyGuardsExpresionAndWhere(guards, whereBlock) =>
                dispatchers match {
                  case None => SimpleDefDecl(inner, defId, args, BodyGuardsExpresion(guards), Some(whereBlock))
                  case Some(d) => MultiMethod(inner, defId, d, args, BodyGuardsExpresion(guards), Some(whereBlock))
                }

              case _ =>
                val where = tryParseWhereBlock(tokens)
                dispatchers match {
                  case None => SimpleDefDecl(inner, defId, args, body, where)
                  case Some(d) => MultiMethod(inner, defId, d, args, body, where)
                }
            }
          case _ => throw InvalidDef()
        }
    }
  }

  def parseDefArgs(tokens: TokenStream): (Option[List[DefArg]], List[DefArg]) = {
    val (result, beforeQuestion) = consumeDefArgs(tokens, Nil, Nil)
    (if (beforeQuestion.isEmpty) None else Some(beforeQuestion), result)
  }

  private[this]
  def consumeDefArgs(tokens: TokenStream, args: List[DefArg], before: List[DefArg]): (List[DefArg], List[DefArg]) = {
    if (tokens.peek(ASSIGN) || tokens.peek(NL)) {
      (args.reverse, before.reverse)
    } else {
      var newArgs = args
      var newBefore = before
      if (tokens.peek(QUESTION)) {
        tokens.consume(QUESTION)
        newArgs = Nil
        newBefore = args ++ before
      }
      if (tokens.peek(OTHERWISE)) {
        tokens.consume(OTHERWISE)
        newArgs = DefOtherwiseArg :: newArgs
      } else {
        val expr = parseDefArg(tokens)
        newArgs = DefArg(expr) :: newArgs
      }
      consumeDefArgs(tokens, newArgs, newBefore)
    }
  }

  private[this] def parseDefBodyGuards(tokens: TokenStream): Expression = {
    tokens.consume(INDENT)
    var listOfGuards = List.empty[DefBodyGuardExpr]
    var guard = parseBodyGuard(tokens)
    listOfGuards = guard :: listOfGuards
    while (tokens.peek(GUARD)) {
      guard = parseBodyGuard(tokens)
      listOfGuards = guard :: listOfGuards
    }
    val result = if (tokens.peek(WHERE)) {
      BodyGuardsExpresionAndWhere(listOfGuards.reverse, parseUnindentedWhereBlock(tokens))
    } else {
      BodyGuardsExpresion(listOfGuards.reverse)
    }
    tokens.consume(DEDENT)
    result
  }


  private[this] def parseBodyGuard(tokens: TokenStream): DefBodyGuardExpr = {
    tokens.consume(GUARD)
    if (tokens.peek(OTHERWISE)) {
      tokens.consume(OTHERWISE)
      tokens.consume(ASSIGN)
      var expr: Expression = null
      if (!tokens.peek(NL)) {
        expr = ForwardPipeFuncCallExpression.parse(tokens)
        tokens.consumeOptionals(NL)
      }
      else {
        tokens.consume(NL)
        expr = BlockExpression.parse(tokens)
      }
      DefBodyGuardOtherwiseExpression(expr)

    } else {
      val guardExpr = LogicalExpression.parse(tokens)
      tokens.consume(ASSIGN)
      var expr: Expression = null
      if (!tokens.peek(NL)) {
        expr = ForwardPipeFuncCallExpression.parse(tokens)
        tokens.consume(NL)
      }
      else {
        tokens.consume(NL)
        expr = BlockExpression.parse(tokens)
      }
      DefBodyGuardExpression(guardExpr, expr)
    }
  }



  private[this] def tryParseWhereBlock(tokens:TokenStream): Option[WhereBlock] = {
    if (tokens.peek(NL) && tokens.peek(2, INDENT)) {
      tokens.consume(NL)
      Some(parseWhereBlock(tokens))
    }
    else if (tokens.peek(WHERE)) {
      Some(parseUnindentedWhereBlock(tokens))
    }
    else {
      None
    }
  }


 private[this] def parseWhereBlock(tokens:TokenStream): WhereBlock = {
    tokens.consume(INDENT)
    val whereBlock = parseUnindentedWhereBlock(tokens)
    tokens.consume(DEDENT)
    whereBlock
  }


  private[this] def parseDefArg(tokens:TokenStream) : Expression = {
    if (tokens.peek(classOf[ID])) {
      val id = tokens.consume(classOf[ID])
      if (!tokens.peek(COLON)) {
        Identifier(id.value)
      } else {
        tokens.consume(COLON)
        IdIsType(id.value, tokens.consume(classOf[TID]).value)
      }
    }
    else {
      LogicalExpression.parse(tokens)
    }
  }

  private[this] def parseUnindentedWhereBlock(tokens:TokenStream): WhereBlock = {
    tokens.consume(WHERE)
    var listOfWhereDefs = List.empty[WhereDef]
    if (!tokens.peek(NL)) {
      val whereDef = parseWhereDef(tokens)
      listOfWhereDefs = whereDef :: listOfWhereDefs
      tokens.consumeOptionals(NL)
    } else {
      tokens.consume(NL)
    }
    if (tokens.peek(INDENT)) {
      tokens.consume(INDENT)
      while (!tokens.peek(DEDENT)) {
        val whereDef = parseWhereDef(tokens)
        listOfWhereDefs = whereDef :: listOfWhereDefs
        tokens.consumeOptionals(NL)
      }
      tokens.consume(DEDENT)
    }
    WhereBlock(listOfWhereDefs.reverse)
  }



  private[this] def parseWhereDef(tokens:TokenStream): WhereDef = {
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
      val body = if (!tokens.peek(NL)) {
        ForwardPipeFuncCallExpression.parse(tokens)
      } else {
        tokens.consume(NL)
        BlockExpression.parse(tokens)
      }
      if (listOfIds.size == 1)
        WhereDefSimple(listOfIds.head, if (listOfArgs.isEmpty) None else Some(listOfArgs.reverse), body)
      else
        WhereDefTupled(listOfIds, if (listOfArgs.isEmpty) None else Some(listOfArgs.reverse), body)
    } else if (tokens.peek(GUARD) || tokens.peek(NL)) {
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
