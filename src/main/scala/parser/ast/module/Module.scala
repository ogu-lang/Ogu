package parser.ast.module

import lexer._
import parser._
import parser.ast._
import parser.ast.expressions._
import parser.ast.expressions.functions.{ForwardPipeFuncCallExpression, FunctionCallWithDollarExpression, LambdaExpression}
import parser.ast.expressions.literals.Atom
import parser.ast.expressions.logical.LogicalExpression
import parser.ast.functions._
import parser.ast.types._

import scala.collection.mutable

case class Module(name: String, imports: Option[List[ImportClause]],decls: List[LangNode]) extends LangNode

object Module  {

  def parse(tokens: TokenStream, nameFromFile: String): Module = {
    val moduleName = if (!tokens.peek(MODULE)) {
      nameFromFile
    } else {
      tokens.consume(MODULE)
      if (tokens.peek(classOf[TID])) tokens.consume(classOf[TID]).value else tokens.consume(classOf[ID]).value
    }
    parseModule(moduleName, tokens)
  }

  private[this] def parseModule(moduleName: String, tokens: TokenStream) : Module = {
    tokens.consumeOptionals(NL)
    Module(moduleName, ImportClause.parse(tokens), parseModuleNodes(tokens))
  }

  def parseModuleNodes(tokens:TokenStream): List[LangNode] = {
    println(s"@@@ parse module nodes (tokens=$tokens)")
    var result = List.empty[LangNode]
    while (tokens.nonEmpty) {
      val inner = if (!tokens.peek(PRIVATE)) false else { tokens.consume(PRIVATE); true }
      result = tokens.nextToken() match {
        case None =>  result
        case Some(token) =>
          token match {
            case CLASS => ClassDecl.parse(inner, tokens) :: result
            case DATA => AdtDecl.parse(inner, tokens) :: result
            case DEF => multiDef(parseDef(inner, tokens)) :: result
            case DISPATCH => DispatchDecl.parse(inner, tokens) :: result
            case EXTENDS => ExtendsDecl.parse(inner, tokens) :: result
            case RECORD => RecordDecl.parse(inner, tokens) :: result
            case TRAIT => TraitDecl.parse(inner, tokens) :: result
            case _ => TopLevelExpression.parse(tokens) :: result
          }
      }
      tokens.consumeOptionals(NL)
    }
    filter(result.reverse)
  }

  val defs = mutable.HashMap.empty[String, MultiDefDecl]

  private[this] def multiDef(node: LangNode): LangNode = {
    node match {
      case decl: SimpleDefDecl =>
        if (defs.contains(decl.id)) {
          defs.get(decl.id).map { defDecl =>
            val decls = decl :: defDecl.decls
            val mDecl = MultiDefDecl(defDecl.id, decls)
            defs.update(mDecl.id, mDecl)
            mDecl
          }.get
        }
        else {
          val mDecl = MultiDefDecl(decl.id, List(decl))
          defs.put(mDecl.id, mDecl)
          mDecl
        }
      case d => d
    }
  }

  private[this] def filter(nodes: List[LangNode]): List[LangNode] = {
    var result = List.empty[LangNode]
    for (node <- nodes) {
      node match {
        case md: MultiDefDecl =>
          defs.get(md.id).map { md =>
            val multiDef = MultiDefDecl(md.id, md.decls.reverse)
            if (multiDef.decls.length == 1)
              result = multiDef.decls.head :: result
            else
              result = multiDef :: result
            defs.remove(md.id)
          }
        case _ =>
          result = node :: result
      }
    }
    result.reverse
  }

  def parseDef(inner: Boolean, tokens:TokenStream): DefDecl = {
    tokens.consume(DEF)
    val defId = tokens.consume(classOf[ID]).value
    val (matches, args) = parseDefArgs(tokens)
    if (tokens.peek(NL)) {
      tokens.consume(NL)
      val body = parseDefBodyGuards(tokens)
      body match {
        case bd: BodyGuardsExpresionAndWhere =>
          if (matches.isEmpty) {
            SimpleDefDecl(inner, defId, args, BodyGuardsExpresion(bd.guards), Some(bd.whereBlock))
          }
          else {
            MultiMethod(inner, defId, matches.get, args, BodyGuardsExpresion(bd.guards), Some(bd.whereBlock))
          }
        case _ =>
          val where = tryParseWhereBlock(tokens)
          if (matches.isEmpty) {
            SimpleDefDecl(inner, defId, args, body, where)
          }
          else {
            MultiMethod(inner, defId, matches.get, args, body, where)
          }
      }
    }
    else if (tokens.peek(ASSIGN)) {
      tokens.consume(ASSIGN)
      val body = if (!tokens.peek(NL)) {
        ForwardPipeFuncCallExpression.parse(tokens)
      }
      else {
        tokens.consume(NL)
        BlockExpression.parse(tokens)
      }
      val where = tryParseWhereBlock(tokens)
      if (matches.isEmpty) {
        SimpleDefDecl(inner, defId, args, body, where)
      } else {
        MultiMethod(inner, defId, matches.get, args, body, where)
      }
    } else {
      throw InvalidDef()
    }
  }

  def parseDefBodyGuards(tokens:TokenStream): Expression = {
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

  def parseBodyGuard(tokens:TokenStream): DefBodyGuardExpr = {
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

  def tryParseWhereBlock(tokens:TokenStream): Option[WhereBlock] = {
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

  def parseWhereBlock(tokens:TokenStream): WhereBlock = {
    tokens.consume(INDENT)
    val whereBlock = parseUnindentedWhereBlock(tokens)
    tokens.consume(DEDENT)
    whereBlock
  }

  def parseUnindentedWhereBlock(tokens:TokenStream): WhereBlock = {
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

  def parseWhereDef(tokens:TokenStream): WhereDef = {
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

  def parseWhereGuard(tokens:TokenStream): WhereGuard = {
    tokens.consume(GUARD)
    val comp = if (tokens.peek(OTHERWISE)) {
      tokens.consume(OTHERWISE)
      None
    } else {
      Some(LogicalExpression.parse(tokens))
    }
    tokens.consume(ASSIGN)
    val body = if (tokens.peek(INDENT)) BlockExpression.parse(tokens) else ForwardPipeFuncCallExpression.parse(tokens)
    tokens.consume(NL)
    WhereGuard(comp, body)
  }

  def parseWhereArg(tokens: TokenStream) : Expression = {
    if (tokens.peek(classOf[ID])) {
      val id = tokens.consume(classOf[ID])
      Identifier(id.value)
    } else {
      LogicalExpression.parse(tokens)
    }
  }

  def parseDefArgs(tokens:TokenStream) : (Option[List[DefArg]], List[DefArg]) = {
    var result = List.empty[DefArg]
    var beforeQuestion = List.empty[DefArg]
    while (!tokens.peek(ASSIGN) && !tokens.peek(NL)) {
      if (tokens.peek(QUESTION)) {
        tokens.consume(QUESTION)
        beforeQuestion = result ++ beforeQuestion
        result = List.empty[DefArg]
      }
      if (tokens.peek(OTHERWISE)) {
        tokens.consume(OTHERWISE)
        result = DefOtherwiseArg :: result
      } else {
        val expr = parseDefArg(tokens)
        result = DefArg(expr) :: result
      }

    }
    (if (beforeQuestion.isEmpty) None else Some(beforeQuestion.reverse), result.reverse)
  }

  def parseDefArg(tokens:TokenStream) : Expression = {
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

  def parseLetExpr(tokens:TokenStream) : Expression = {
    LetDeclExpr(parseListOfLetVars(tokens, LET), parseInBodyOptExpr(tokens))
  }

  def parseVarExpr(tokens:TokenStream): Expression = {
    VarDeclExpr(parseListOfLetVars(tokens, VAR), parseInBodyOptExpr(tokens))
  }

  def parseBindExpr(tokens:TokenStream): Expression = {
    val listOfLetVars = parseListOfLetVars(tokens, BIND)
    parseInBodyOptExpr(tokens) match {
      case None => throw InvalidExpression()
      case Some(body) =>  BindDeclExpr(listOfLetVars.reverse, body)
    }
  }

  def parseListOfLetVars(tokens:TokenStream, token: TOKEN) : List[Variable] = {
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

  def parseInBodyExpr(tokens:TokenStream): Option[Expression] = {
    tokens.consume(IN)
    if (!tokens.peek(NL)) {
      Some(ForwardPipeFuncCallExpression.parse(tokens))
    } else {
      tokens.consume(NL)
      Some(BlockExpression.parse(tokens))
    }
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

  def parseLetVar(tokens:TokenStream) : Variable = {
    tokens.consumeOptionals(NL)
    val id = parseLetId(tokens)
    tokens.consume(ASSIGN)
    val expr = parsePipedOrBodyExpression(tokens)
    LetVariable(id, expr)
  }

  def parseLetId(tokens:TokenStream) : LetId = {
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



  def parseFuncCallExpr(tokens:TokenStream) : Expression = {
    var expr : Expression = null
    if (tokens.peek(classOf[ID])) {
      val id = tokens.consume(classOf[ID])
      expr = Identifier(id.value)
    }
    else if (tokens.peek(classOf[ATOM])) {
      expr = Atom.parse(tokens)
    }
    if (!funcCallEndToken(tokens)) {
      var args = List.empty[Expression]
      val func = expr
      while (!funcCallEndToken(tokens)) {
        if (tokens.peek(classOf[ID])) {
          val id = tokens.consume(classOf[ID])
          expr = Identifier(id.value)
        } else {
          expr = FunctionCallWithDollarExpression.parse(tokens)
        }
        args = expr :: args
      }
      FunctionCallExpression(func, args.reverse)
    } else {
      if (expr == null) {
        println(s"@@INVALID EXPRESSION TOKENS= $tokens")
        throw InvalidExpression()
      }
      expr
    }
  }



}
