package parser

import lexer._

import scala.collection.mutable


class Parser(filename:String, val tokens: TokenStream, defaultSymbolTable: Option[SymbolTable]) {


  def parse() : LangNode = {
    if (tokens.peek(MODULE)) {
      parseModule()
    }
    else {
      val nameOfModule = filename.substring(filename.lastIndexOf('/')+1, filename.lastIndexOf('.'))
      Module(nameOfModule, parseImports(), parseModuleNodes())
    }
  }

  def parseModule() : Module = {
    tokens.consume(MODULE)
    val moduleName = if (tokens.peek(classOf[TID])) tokens.consume(classOf[TID]).value else tokens.consume(classOf[ID]).value
    tokens.consumeOptionals(NL)
    Module(moduleName, parseImports(), parseModuleNodes())
  }

  def parseImports(): Option[List[ImportClause]] = {
    tokens.consumeOptionals(NL)
    var listOfImports = List.empty[ImportClause]
    while (tokens.peek(IMPORT) || tokens.peek(FROM)) {
      if (tokens.peek(IMPORT)) {
        listOfImports = parseImport() :: listOfImports
      }
      else if (tokens.peek(FROM)) {
        listOfImports = parseFromImport() :: listOfImports
      }
      tokens.consumeOptionals(NL)
    }
    if (listOfImports.isEmpty) {
      None
    }
    else {
      Some(listOfImports.reverse)
    }
  }

  def parseImport() : ImportClause = {
    tokens.consume(IMPORT)
    var tag : String = ""
    if (tokens.peek(LBRACKET)) {
      tokens.consume(LBRACKET)
      tag = tokens.consume(classOf[ATOM]).value
      tokens.consume(RBRACKET)
    }
    var listOfAlias = List.empty[ImportAlias]
    val impAlias = parseImportAlias()
    listOfAlias = impAlias :: listOfAlias
    while (tokens.peek(COMMA)) {
      tokens.consume(COMMA)
      tokens.consumeOptionals(NL)
      val impAlias = parseImportAlias()
      listOfAlias = impAlias :: listOfAlias
    }
    if (tag == ":jvm") {
      JvmImport(listOfAlias.reverse)
    }
    else {
      CljImport(listOfAlias.reverse)
    }
  }

  def parseFromImport() : ImportClause = {
    tokens.consume(FROM)
    var tag : String = ""
    if (tokens.peek(LBRACKET)) {
      tokens.consume(LBRACKET)
      tag = tokens.consume(classOf[ATOM]).value
      tokens.consume(RBRACKET)
    }
    val name = tokens.consume(classOf[ID]).value
    tokens.consume(IMPORT)
    var listOfAlias = List.empty[ImportAlias]
    val impAlias = parseImportAlias()
    listOfAlias = impAlias :: listOfAlias
    while (tokens.peek(COMMA)) {
      tokens.consume(COMMA)
      tokens.consumeOptionals(NL)
      val impAlias = parseImportAlias()
      listOfAlias = impAlias :: listOfAlias
    }
    if (tag == ":jvm") {
      FromJvmRequire(name, listOfAlias.reverse)
    }
    else {
      FromCljRequire(name, listOfAlias.reverse)
    }
  }

  def parseImportAlias() : ImportAlias = {
    val id = if (tokens.peek(classOf[TID])) tokens.consume(classOf[TID]).value else  tokens.consume(classOf[ID]).value
    val alias = if (!tokens.peek(AS)) {
      None
    } else {
      tokens.consume(AS)
      Some(tokens.consume(classOf[ID]).value)
    }
    ImportAlias(id, alias)
  }

  def parseModuleNodes() : List[LangNode] = {
    println(s"@@@ parse module nodes (tokens=$tokens)")
    var result = List.empty[LangNode]
    while (tokens.nonEmpty) {
      var inner = false
      if (tokens.peek(PRIVATE)) {
        tokens.consume(PRIVATE)
        inner = true
      }
      if (tokens.peek(DEF)) {
        result = multiDef(parseDef(inner)) :: result
      }
      else if (tokens.peek(DISPATCH)) {
        result = parseDispatch(inner) :: result
      }
      else if (tokens.peek(DATA)) {
        result = parseData(inner) :: result
      }
      else if (tokens.peek(TRAIT)) {
        result = parseTrait(inner) :: result
      }
      else{
        result = TopLevelExpression(parsePipedExpr()) :: result
      }
      tokens.consumeOptionals(NL)
      //println(s"PARSED SO FAR: ${result.reverse}\n\n")
    }
    filter(result.reverse)
  }

  def parseDispatch(inner: Boolean): LangNode = {
    tokens.consume(DISPATCH)
    val id = tokens.consume(classOf[ID]).value
    tokens.consume(WITH)
    if (tokens.peek(CLASS)) {
      tokens.consume(CLASS)
      DispatchDecl(id, ClassDispatcher)
    } else {
      val expr = parsePipedExpr()
      DispatchDecl(id, ExpressionDispatcher(expr))
    }
  }

  def parseData(inner:Boolean): LangNode = {
    tokens.consume(DATA)
    val id = tokens.consume(classOf[TID]).value
    tokens.consume(ASSIGN)
    var indents = 0
    if (tokens.peek(NL)) {
      tokens.consumeOptionals(NL)
      tokens.consume(INDENT)
      indents += 1
    }
    var adts = List.empty[ADT]
    val adt = parseADT()
    adts = adt :: adts
    while (tokens.peek(GUARD)) {
      tokens.consume(GUARD)
      tokens.consumeOptionals(NL)
      if (tokens.peek(INDENT)) {
        tokens.consume(INDENT)
        indents += 1
      }
      val adt = parseADT()
      adts = adt :: adts
    }
    while (indents > 0) {
      tokens.consume(DEDENT)
      indents -= 1
    }
    AdtDecl(id, adts.reverse)
  }

  def parseADT(): ADT = {
    val id = tokens.consume(classOf[TID]).value
    var args = List.empty[String]
    if (tokens.peek(LPAREN)) {
      tokens.consume(LPAREN)
      val arg = tokens.consume(classOf[ID]).value
      args = arg :: args
      while (tokens.peek(COMMA)) {
        tokens.consume(COMMA)
        tokens.consumeOptionals(NL)
        val arg = tokens.consume(classOf[ID]).value
        args = arg :: args
      }
      tokens.consume(RPAREN)
    }
    ADT(id, args.reverse)
  }

  def parseTrait(inner: Boolean): LangNode = {
    tokens.consume(TRAIT)
    val id = tokens.consume(classOf[TID]).value
    tokens.consume(NL)
    tokens.consumeOptionals(NL)
    var decls = List.empty[TraitMethodDecl]
    if (tokens.peek(INDENT)) {
      tokens.consume(INDENT)
      val decl = parseTraitMethodDecl()
      decls = decl :: decls
      tokens.consume(DEDENT)
    }
    TraitDecl(inner, id, decls)
  }

  def parseTraitMethodDecl(): TraitMethodDecl = {
    tokens.consume(DEF)
    val id = tokens.consume(classOf[ID]).value
    var args = List.empty[String]
    while (tokens.peek(classOf[ID])) {
      val arg = tokens.consume(classOf[ID]).value
      args = arg :: args
    }
    TraitMethodDecl(id, args.reverse)
  }

  var defs = mutable.HashMap.empty[String, MultiDefDecl]

  private[this] def multiDef(node: LangNode) : LangNode = {
    node match {
      case decl:SimpleDefDecl =>
        if (defs.contains(decl.id)) {
          defs.get(decl.id).map { defDecl =>
            val decls = decl :: defDecl.decls
            val mDecl = MultiDefDecl(defDecl.id, decls)
            defs.update(mDecl.id,  mDecl)
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

  private[this] def filter(nodes: List[LangNode]) : List[LangNode] = {
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

  def parseDef(inner: Boolean) : LangNode = {
    tokens.consume(DEF)
    val defId = tokens.consume(classOf[ID]).value
    val (matches, args) = parseDefArgs()
    if (tokens.peek(NL)) {
      tokens.consume(NL)
      val body = parseDefBodyGuards()
      body match {
        case bd: BodyGuardsExpresionAndWhere =>
          if (matches.isEmpty) {
            SimpleDefDecl(inner, defId, args, BodyGuardsExpresion(bd.guards), Some(bd.whereBlock))
          }
          else {
            MultiMethod(inner, defId, matches.get, args, BodyGuardsExpresion(bd.guards), Some(bd.whereBlock))
          }
        case _ =>
          val where = tryParseWhereBlock()
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
        parsePipedExpr()
      }
      else {
        tokens.consume(NL)
        parseBlockExpr()
      }
      val where = tryParseWhereBlock()
      if (matches.isEmpty) {
        SimpleDefDecl(inner, defId, args, body, where)
      } else {
        MultiMethod(inner, defId, matches.get, args, body, where)
      }
    } else {
      throw InvalidDef()
    }
  }

  def parseDefBodyGuards() : Expression = {
    tokens.consume(INDENT)
    var listOfGuards = List.empty[DefBodyGuardExpr]
    var guard = parseBodyGuard()
    listOfGuards = guard :: listOfGuards
    while (tokens.peek(GUARD)) {
      guard = parseBodyGuard()
      listOfGuards = guard :: listOfGuards
    }
    val result = if (tokens.peek(WHERE)) {
      BodyGuardsExpresionAndWhere(listOfGuards.reverse, parseUnindentedWhereBlock())
    } else {
      BodyGuardsExpresion(listOfGuards.reverse)
    }
    tokens.consume(DEDENT)
    result
  }

  def parseBodyGuard() : DefBodyGuardExpr = {
    tokens.consume(GUARD)
    if (tokens.peek(OTHERWISE)) {
      tokens.consume(OTHERWISE)
      tokens.consume(ASSIGN)
      var expr: Expression = null
      if (!tokens.peek(NL)) {
        expr = parsePipedExpr()
        tokens.consumeOptionals(NL)
      }
      else {
        tokens.consume(NL)
        expr = parseBlockExpr()
      }
      DefBodyGuardOtherwiseExpression(expr)

    } else {
      val guardExpr = parseLogicalExpr()
      tokens.consume(ASSIGN)
      var expr: Expression = null
      if (!tokens.peek(NL)) {
        expr = parsePipedExpr()
        tokens.consume(NL)
      }
      else {
        tokens.consume(NL)
        expr = parseBlockExpr()
      }
      DefBodyGuardExpression(guardExpr, expr)
    }
  }

  def tryParseWhereBlock() : Option[WhereBlock] = {
    if (tokens.peek(NL) && tokens.peek(2, INDENT)) {
      tokens.consume(NL)
      Some(parseWhereBlock())
    }
    else if (tokens.peek(WHERE)) {
      Some(parseUnindentedWhereBlock())
    }
    else {
      None
    }
  }

  def parseWhereBlock(): WhereBlock = {
    tokens.consume(INDENT)
    val whereBlock = parseUnindentedWhereBlock()
    tokens.consume(DEDENT)
    whereBlock
  }

  def parseUnindentedWhereBlock() : WhereBlock = {
    tokens.consume(WHERE)
    var listOfWhereDefs = List.empty[WhereDef]
    if (!tokens.peek(NL)) {
      val whereDef = parseWhereDef()
      listOfWhereDefs = whereDef :: listOfWhereDefs
      tokens.consumeOptionals(NL)
    } else {
      tokens.consume(NL)
    }
    if (tokens.peek(INDENT)) {
      tokens.consume(INDENT)
      while (!tokens.peek(DEDENT)) {
        val whereDef = parseWhereDef()
        listOfWhereDefs = whereDef :: listOfWhereDefs
        tokens.consumeOptionals(NL)
      }
      tokens.consume(DEDENT)
    }
    WhereBlock(listOfWhereDefs.reverse)
  }

  def parseWhereDef() : WhereDef = {
    var listOfIds = List.empty[String]
    if (!tokens.peek(LPAREN)) {
      val id = tokens.consume(classOf[ID])
      listOfIds = id.value :: listOfIds
    } else {
      tokens.consume(LPAREN)
      val id = tokens.consume(classOf[ID])
      listOfIds = id.value :: listOfIds
      while (tokens.peek(COMMA)) {
        tokens.consume(COMMA)
        val id = tokens.consume(classOf[ID])
        listOfIds = id.value :: listOfIds
      }
      tokens.consume(RPAREN)
    }
    var listOfArgs = List.empty[Expression]
    while (!tokens.peek(ASSIGN) && !tokens.peek(GUARD) && !tokens.peek(NL)) {
      val expr = parseWhereArg()
      listOfArgs = expr :: listOfArgs
    }
    if (tokens.peek(ASSIGN)) {
      tokens.consume(ASSIGN)
      val body = if (!tokens.peek(NL)) {
        parsePipedExpr()
      } else {
        tokens.consume(NL)
        parseBlockExpr()
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
        if (tokens.peek(INDENT)) {
          tokens.consume(INDENT)
          while (tokens.peek(GUARD)) {
            tokens.consume(GUARD)
            val comp = if (tokens.peek(OTHERWISE)) {
              tokens.consume(OTHERWISE)
              None
            } else {
              Some(parseLogicalExpr())
            }
            tokens.consume(ASSIGN)
            val body = if (tokens.peek(INDENT)) parseBlockExpr() else parsePipedExpr()
            val guard = WhereGuard(comp, body)
            guards = guard :: guards
            tokens.consume(NL)
          }
          tokens.consume(DEDENT)
        } else {
          tokens.consume(GUARD)
          val comp = if (tokens.peek(OTHERWISE)) {
            tokens.consume(OTHERWISE)
            None
          } else {
            Some(parseLogicalExpr())
          }
          tokens.consume(ASSIGN)
          val body = if (tokens.peek(INDENT)) parseBlockExpr() else parsePipedExpr()
          val guard = WhereGuard(comp, body)
          guards = guard :: guards
          tokens.consume(NL)
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

  def parseWhereArg() : Expression = {
    if (tokens.peek(classOf[ID])) {
      val id = tokens.consume(classOf[ID])
      Identifier(id.value)
    } else {
      parseLogicalExpr()
    }
  }

  def parseDefArgs() : (Option[List[DefArg]], List[DefArg]) = {
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
        val expr = parseDefArg()
        result = DefArg(expr) :: result
      }

    }
    (if (beforeQuestion.isEmpty) None else Some(beforeQuestion.reverse), result.reverse)
  }

  def parseDefArg() : Expression = {
    if (tokens.peek(classOf[ID])) {
      val id = tokens.consume(classOf[ID])
      Identifier(id.value)
    }
    else {
      parseLogicalExpr()
    }
  }

  def parseLetExpr() : Expression = {
    tokens.consume(LET)
    tokens.consumeOptionals(NL)
    var insideIndent = if (tokens.peek(INDENT)) 1 else 0
    if (insideIndent == 1)
      tokens.consume(INDENT)
    var letVar = parseLetVar()
    var listOfLetVars = List.empty[Variable]
    listOfLetVars = letVar :: listOfLetVars
    while (tokens.peek(COMMA)) {
      tokens.consume(COMMA)
      tokens.consumeOptionals(NL)
      if (tokens.peek(INDENT)) {
        tokens.consume(INDENT)
        insideIndent += 1
      }
      letVar = parseLetVar()
      listOfLetVars = letVar :: listOfLetVars
    }

    while (insideIndent > 0) {
      tokens.consumeOptionals(NL)
      tokens.consume(DEDENT)
      insideIndent -= 1
    }

    val body: Option[Expression] =
      if (tokens.peek(IN)) {
        parseInBodyExpr()
      } else if (tokens.peek(NL) && tokens.peek(2, IN)) {
        tokens.consume(NL)
        parseInBodyExpr()
      } else if (tokens.peek(NL) && tokens.peek(2, INDENT) && tokens.peek(3, IN)) {
        tokens.consume(NL)
        tokens.consume(INDENT)
        val result = parseInBodyExpr()
        tokens.consume(DEDENT)
        result
      } else {
        None
      }


    LetDeclExpr(listOfLetVars.reverse, body)
  }

  def parseBindExpr(): Expression = {
    tokens.consume(BIND)
    tokens.consumeOptionals(NL)
    var insideIndent = if (tokens.peek(INDENT)) 1 else 0
    if (insideIndent == 1)
      tokens.consume(INDENT)
    var letVar = parseLetVar()
    var listOfLetVars = List.empty[Variable]
    listOfLetVars = letVar :: listOfLetVars
    while (tokens.peek(COMMA)) {
      tokens.consume(COMMA)
      tokens.consumeOptionals(NL)
      if (tokens.peek(INDENT)) {
        tokens.consume(INDENT)
        insideIndent += 1
      }
      letVar = parseLetVar()
      listOfLetVars = letVar :: listOfLetVars
    }
    while (insideIndent > 0) {
      tokens.consumeOptionals(NL)
      tokens.consume(DEDENT)
      insideIndent -= 1
    }
    val body: Option[Expression] =
      if (tokens.peek(IN)) {
        parseInBodyExpr()
      }
      else if (tokens.peek(NL) && tokens.peek(2, IN)) {
        tokens.consume(NL)
        parseInBodyExpr()
      } else if (tokens.peek(NL) && tokens.peek(2, INDENT) && tokens.peek(3, IN)) {
        tokens.consume(NL)
        tokens.consume(INDENT)
        val result = parseInBodyExpr()
        tokens.consume(DEDENT)
        result
      } else {
        None
      }
    if (body.isEmpty) {
      throw InvalidExpression()
    }
    BindDeclExpr(listOfLetVars.reverse, body.get)
  }

  def parseInBodyExpr(): Option[Expression] = {
    tokens.consume(IN)
    if (!tokens.peek(NL)) {
      Some(parsePipedExpr())
    } else {
      tokens.consume(NL)
      Some(parseBlockExpr())
    }
  }

  def parseVarExpr(): Expression = {
    tokens.consume(VAR)
    tokens.consumeOptionals(NL)
    var insideIndent = if (tokens.peek(INDENT)) 1 else 0
    if (insideIndent == 1)
      tokens.consume(INDENT)
    var letVar = parseLetVar()
    var listOfLetVars = List.empty[Variable]
    listOfLetVars = letVar :: listOfLetVars
    while (tokens.peek(COMMA)) {
      tokens.consume(COMMA)
      tokens.consumeOptionals(NL)
      if (tokens.peek(INDENT)) {
        tokens.consume(INDENT)
        insideIndent += 1
      }
      letVar = parseLetVar()
      listOfLetVars = letVar :: listOfLetVars
    }

    while (insideIndent > 0) {
      tokens.consumeOptionals(NL)
      tokens.consume(DEDENT)
      insideIndent -= 1
    }
    val body: Option[Expression] =
      if (tokens.peek(IN)) {
        parseInBodyExpr()
      } else if (tokens.peek(NL) && tokens.peek(2, IN)) {
        tokens.consume(NL)
        parseInBodyExpr()
      } else if (tokens.peek(NL) && tokens.peek(2, INDENT) && tokens.peek(3, IN)) {
        tokens.consume(NL)
        tokens.consume(INDENT)
        val result = parseInBodyExpr()
        tokens.consume(DEDENT)
        result
      } else {
        None
      }
    VarDeclExpr(listOfLetVars.reverse, body)
  }

  def parseLetVar() : Variable = {
    tokens.consumeOptionals(NL)
    val id = parseLetId()
    tokens.consume(ASSIGN)
    val expr = parsePipedOrBodyExpression()
    LetVariable(id, expr)
  }

  def parseLetId() : LetId = {
    if (!tokens.peek(LPAREN)) {
      val idToken = tokens.consume(classOf[ID])
      LetSimpleId(idToken.value)
    } else {
      tokens.consume(LPAREN)
      var ids = List.empty[LetId]
      val id = parseLetId()
      ids =id :: ids
      while (tokens.peek(COMMA)) {
        tokens.consume(COMMA)
        val id = parseLetId()
        ids = id :: ids
      }
      tokens.consume(RPAREN)
      LetTupledId(ids)
    }
  }

  private def parsePipedOrBodyExpression(): Expression = {
    if (!tokens.peek(NL))
      parsePipedExpr()
    else {
      tokens.consume(NL)
      parseBlockExpr()
    }
  }

  // pipedExpr = expr (PIPE_OPER pipedExpr)*
  def parsePipedExpr() : Expression = {
    val expr = parseForwardPipeExpr()
    expr
  }

  def parseForwardPipeExpr() : Expression = {
    var expr = parseForwardPipeFirstArgExpr()
    if (tokens.peek(PIPE_RIGHT)) {
      var value = expr
      var args = List.empty[Expression]
      while (tokens.peek(PIPE_RIGHT)) {
        args = value :: args
        tokens.consume(PIPE_RIGHT)
        value = parseForwardPipeFirstArgExpr()
      }
      args = value :: args
      expr = ForwardPipeFuncCallExpression(args.reverse)
    }
    expr
  }

  def parseForwardPipeFirstArgExpr() : Expression = {
    var expr = parseBackwardPipeExpr()
    if (tokens.peek(PIPE_RIGHT_FIRST_ARG)) {
      var value = expr
      var args = List.empty[Expression]
      while (tokens.peek(PIPE_RIGHT_FIRST_ARG)) {
        args = value :: args
        tokens.consume(PIPE_RIGHT_FIRST_ARG)
        value = parseDollarExpr()
      }
      args = value :: args
      expr = ForwardPipeFirstArgFuncCallExpression(args.reverse)
    }
    expr
  }

  def parseBackwardPipeExpr() : Expression = {
    var expr = parseBackwardFirstArgPipeExpr()
    if (tokens.peek(PIPE_LEFT)) {
      var value = expr
      var args = List.empty[Expression]
      while (tokens.peek(PIPE_LEFT)) {
        args = value :: args
        tokens.consume(PIPE_LEFT)
        value = parseBackwardFirstArgPipeExpr()
      }
      args = value :: args
      expr = BackwardPipeFuncCallExpression(args)
    }
    expr
  }

  def parseBackwardFirstArgPipeExpr() : Expression = {
    var expr = parseDollarExpr()
    if (tokens.peek(PIPE_LEFT_FIRST_ARG)) {
      var value = expr
      var args = List.empty[Expression]
      while (tokens.peek(PIPE_LEFT_FIRST_ARG)) {
        args = value :: args
        tokens.consume(PIPE_LEFT_FIRST_ARG)
        value = parseDollarExpr()
      }
      args = value :: args
      expr = BackwardPipeFirstArgFuncCallExpression(args)
    }
    expr
  }

  def parseDollarExpr() : Expression = {
    var expr = parseExpr()
    if (tokens.peek(DOLLAR)) {
      val func = expr
      tokens.consume(DOLLAR)
      var args = List.empty[Expression]
      while (!funcCallEndToken()) {
        expr = parseDollarExpr()
        args = expr :: args
      }
      expr = FunctionCallWithDollarExpression(func, args.reverse)
    }
    expr
  }


  // funcCallExpr ::= control_expr | lambda_expr
  def parseExpr() : Expression = {
    // if control
    if (tokens.peek(classOf[CONTROL]))
      parseControlExpr()
    else if (tokens.peek(LET))
      parseLetExpr()
    else if (tokens.peek(VAR))
      parseVarExpr()
    else if (tokens.peek(BIND))
      parseBindExpr()
    else
      parseLambdaExpr()
  }

  def parseControlExpr() : Expression = {
    if (tokens.peek(COND)) {
      parseCondExpr()
    }
    else if (tokens.peek(FOR)) {
      parseForExpr()
    }
    else if (tokens.peek(IF)) {
      parseIfExpr()
    }
    else if (tokens.peek(WHEN)) {
      parseWhenExpr()
    }
    else if (tokens.peek(LOOP)) {
      parseLoopExpr()
    }
    else if (tokens.peek(UNTIL)) {
      parseWhileExpr()
    }
    else if (tokens.peek(WHILE)) {
      parseWhileExpr()
    }
    else if (tokens.peek(REPEAT)) {
      parseRepeatExpr()
    }
    else if (tokens.peek(RECUR)) {
      parseRecurExpr()
    }
    else if (tokens.peek(SET)) {
      parseAssignExpr()
    }
    else if (tokens.peek(TRY)) {
      parseTryExpr()
    }
    else if (tokens.peek(THROW)) {
      parseThrowExpr()
    }
    else {
      println(s"ERROR PARSE CONTROL tokens= $tokens")
      throw InvalidNodeException(tokens.nextToken())
    }
  }

  def parseThrowExpr() : Expression = {
    tokens.consume(THROW)
    val ctor = parseConstructorExpr()
    tokens.consumeOptionals(NL)
    ThrowExpression(ctor)
  }

  def parseTryExpr() : Expression = {
    tokens.consume(TRY)
    val body = parsePipedOrBodyExpression()
    tokens.consumeOptionals(NL)
    var catches = List.empty[CatchExpression]
    var indents = 0
    if (tokens.peek(INDENT)) {
      tokens.consume(INDENT)
      indents += 1
    }
    while (tokens.peek(CATCH)) {
      val catchExpr = parseCatchExpr()
      catches = catchExpr :: catches
      tokens.consumeOptionals(NL)
      if (tokens.peek(INDENT)) {
        tokens.consume(INDENT)
        indents += 1
      }
    }
    val finallyExpr = if (tokens.peek(FINALLY)) {
      Some(parseFinallyExpr())
    } else {
      None
    }
    while (indents > 0) {
      tokens.consume(DEDENT)
      indents -= 1
    }
    TryExpression(body, catches.reverse, finallyExpr)
  }

  def parseCatchExpr(): CatchExpression = {
    tokens.consume(CATCH)
    if (tokens.peek(classOf[ID])) {
      val id = tokens.consume(classOf[ID]).value
      tokens.consume(COLON)
      val ex = tokens.consume(classOf[TID]).value
      tokens.consume(ARROW)
      CatchExpression(Some(id), ex, parsePipedOrBodyExpression())
    } else {
      val ex = tokens.consume(classOf[TID]).value
      tokens.consume(ARROW)
      CatchExpression(None, ex, parsePipedOrBodyExpression())
    }
  }

  def parseFinallyExpr() : Expression = {
    tokens.consume(FINALLY)
    tokens.consume(ARROW)
    parsePipedOrBodyExpression()
  }

  def parseCondExpr(): Expression = {
    tokens.consume(COND)
    tokens.consume(NL)
    tokens.consume(INDENT)
    var guards = List.empty[CondGuard]
    while (!tokens.peek(DEDENT)) {
      val comp = if (tokens.peek(OTHERWISE)) {
        tokens.consume(OTHERWISE)
        None
      } else {
        Some(parseLogicalExpr())
      }
      tokens.consume(ARROW)
      val value = parsePipedExpr()
      tokens.consumeOptionals(NL)
      guards = CondGuard(comp, value) :: guards
    }
    tokens.consume(DEDENT)
    CondExpression(guards.reverse)
  }

  def parseRepeatExpr(): Expression = {
    tokens.consume(REPEAT)
    if (tokens.peek(WITH))
      tokens.consume(WITH)
    var repeatVariables = List.empty[RepeatNewVarValue]
    var repVar = parseRepeatNewValue()
    repeatVariables = repVar :: repeatVariables
    while (tokens.peek(COMMA)) {
      tokens.consume(COMMA)
      repVar = parseRepeatNewValue()
      repeatVariables = repVar :: repeatVariables
    }
    tokens.consumeOptionals(NL)
    RepeatExpr(Some(repeatVariables.reverse))
  }

  def parseRepeatNewValue() : RepeatNewVarValue = {
    if (tokens.peek(classOf[ID]) && tokens.peek(2, ASSIGN)) {
      val id = tokens.consume(classOf[ID])
      tokens.consume(ASSIGN)
      val expr = parsePipedExpr()
      RepeatNewVarValue(id.value, expr)
    } else {
      RepeatNewVarValue(genId(), parseExpr())
    }
  }

  def genId() : String = {
    s"id_${java.util.UUID.randomUUID.toString}"
  }

  def parseForExpr() : Expression = {
    tokens.consume(FOR)
    val forDecls = parseForDecls()
    val forBody = parseForBody()
    ForExpression(forDecls, forBody)
  }

  def parseForDecls() : List[LoopDeclVariable] = {
    var listOfDecls = List.empty[LoopDeclVariable]
    val forVarDecl = parseForVarDecl()
    listOfDecls = forVarDecl :: listOfDecls

    while (tokens.peek(COMMA)) {
      tokens.consume(COMMA)
      val forVarDecl = parseForVarDecl()
      listOfDecls = forVarDecl :: listOfDecls
    }
    listOfDecls.reverse
  }

  def parseForVarDecl() : LoopDeclVariable = {
    if (!tokens.peek(LPAREN)) {
      val id = tokens.consume(classOf[ID])
      tokens.consume(IN)
      ForVarDeclIn(id.value, parsePipedExpr())
    }
    else {
      tokens.consume(LPAREN)
      var ids = List.empty[String]
      val id = tokens.consume(classOf[ID])
      ids = id.value :: ids
      while (tokens.peek(COMMA)) {
        tokens.consume(COMMA)
        val id = tokens.consume(classOf[ID])
        ids = id.value :: ids
      }
      tokens.consume(RPAREN)
      tokens.consume(IN)
      ForVarDeclTupledIn(ids, parsePipedExpr())
    }
  }

  def parseWhileExpr() : Expression = {
    tokens.consume(WHILE)
    val comp = parseLogicalExpr()
    tokens.consume(DO)
    WhileExpression(comp, parsePipedOrBodyExpression())
  }


  def parseUntilExpr() : Expression = {
    tokens.consume(UNTIL)
    val comp = parseLogicalExpr()
    tokens.consume(DO)
    UntilExpression(comp, parsePipedOrBodyExpression())
  }

  def parseLoopExpr() : Expression = {
    tokens.consume(LOOP)

    val loopDecls = parseLoopDecls()

    var guardExpr : Option[LoopGuard] = None
    tokens.consumeOptionals(NL)

    if (tokens.peek(WHILE)) {
      tokens.consume(WHILE)
      guardExpr = Some(WhileGuardExpr(parseLogicalExpr()))
    }


    if (tokens.peek(UNTIL)) {
      if (guardExpr.isDefined)
        throw InvalidUntilAlreadyHasWhile()
      tokens.consume(UNTIL)
      guardExpr = Some(UntilGuardExpr(parseLogicalExpr()))
    }

    tokens.consume(DO)
    if (!tokens.peek(NL)) {
      LoopExpression(loopDecls, guardExpr, parsePipedExpr())
    }
    else {
      tokens.consume(NL)
      LoopExpression(loopDecls, guardExpr, parseBlockExpr())
    }
  }

  def parseLoopDecls() : List[LoopVarDecl] = {
    var listOfDecls = List.empty[LoopVarDecl]
    var loopVarDecl = parseLoopVarDecl()
    listOfDecls = loopVarDecl :: listOfDecls
    while (tokens.peek(COMMA)) {
      tokens.consume(COMMA)
      loopVarDecl = parseLoopVarDecl()
      listOfDecls = loopVarDecl :: listOfDecls
    }
    listOfDecls.reverse
  }

  def parseLoopVarDecl() : LoopVarDecl = {
    val id = tokens.consume(classOf[ID])
    if (tokens.peek(ASSIGN)) {
      tokens.consume(ASSIGN)
      LoopVarDecl(id.value, parsePipedExpr())
    } else {
      throw UnexpectedTokenClassException()
    }
  }

  def parseForBody() : Expression = {
    tokens.consume(DO)
    parsePipedOrBodyExpression()
  }

  def parseBlockExpr() : Expression = {
    tokens.consume(INDENT)
    var listOfExpressions = List.empty[Expression]
    var loop = 0
    while (!tokens.peek(DEDENT)) {
      loop += 1
      val expr = parsePipedExpr()
      tokens.consumeOptionals(NL)
      listOfExpressions = expr :: listOfExpressions
    }
    tokens.consume(DEDENT)
    BlockExpression(listOfExpressions.reverse)
  }

  def parseIfExpr() : Expression = {
    tokens.consume(IF)
    val comp = parseLogicalExpr()
    tokens.consume(THEN)
    val thenPart = if (!tokens.peek(NL)) {
      parsePipedExpr()
    }
    else {
      tokens.consume(NL)
      parseBlockExpr()
    }
    while (tokens.peek(NL)) tokens.consume(NL)
    var elif = List.empty[ElifPart]
    if (tokens.peek(ELIF)) {
      while (tokens.peek(ELIF)) {
        tokens.consume(ELIF)
        val elifComp = parseLogicalExpr()
        tokens.consume(THEN)
        val elifPart = if (!tokens.peek(NL)) {
          ElifPart(elifComp, parsePipedExpr())
        }
        else {
          while (tokens.peek(NL)) tokens.consume(NL)
          ElifPart(elifComp, parseBlockExpr())
        }
        elif = elifPart :: elif
      }
    }
    if (tokens.peek(ELSE)) {
      tokens.consume(ELSE)
      if (!tokens.peek(NL)) {
        return IfExpression(comp, thenPart, elif.reverse, parsePipedExpr())
      } else {
        while (tokens.peek(NL)) tokens.consume(NL)
        return IfExpression(comp, thenPart, elif.reverse, parseBlockExpr())
      }
    }
    throw InvalidIfExpression()
  }

  def parseWhenExpr() : Expression = {
    tokens.consume(WHEN)
    val comp = parseLogicalExpr()
    tokens.consume(THEN)
    if (!tokens.peek(NL)) {
      WhenExpression(comp, parsePipedExpr())
    }
    else {
      tokens.consume(NL)
      WhenExpression(comp, parseBlockExpr())
    }
  }

  def parseAssignExpr() : Expression = {
    tokens.consume(SET)
    val expr = parsePipedOrBodyExpression()
    if (!expr.isInstanceOf[AssignableExpression])
      throw CantAssignToExpression()
    tokens.consume(ASSIGN)
    val right = parsePipedExpr()
    SimpleAssignExpr(expr, right)
  }

  def parseLambdaExpr() : Expression = {
    if (!tokens.peek(LAMBDA)) {
      parseLogicalExpr()
    }
    else {
      tokens.consume(LAMBDA)
      var args = List.empty[LambdaArg]
      val arg = parseLambdaArg()
      args = arg :: args
      while (tokens.peek(COMMA)) {
        tokens.consume(COMMA)
        val arg = parseLambdaArg()
        args = arg :: args
      }
      if (!tokens.peek(ARROW)) {
        throw InvalidLambdaExpression(tokens.nextToken())
      }
      tokens.consume(ARROW)
      val expr = LambdaExpression(args.reverse, parseExpr())
      expr
    }
  }

  def parseLambdaArg() : LambdaArg = {
    if (tokens.peek(classOf[ID])) {
      val id = tokens.consume(classOf[ID])
      LambdaSimpleArg(id.value)
    }
    else if (tokens.peek(LPAREN)) {
      tokens.consume(LPAREN)
      var ids = List.empty[String]
      val id = tokens.consume(classOf[ID]).value
      ids = id :: ids
      while (tokens.peek(COMMA)) {
        tokens.consume(COMMA)
        val id = tokens.consume(classOf[ID]).value
        ids = id :: ids
      }
      tokens.consume(RPAREN)
      LambdaTupleArg(ids.reverse)
    } else {
      throw InvalidLambdaExpression(tokens.nextToken())
    }
  }

  def parseLogicalExpr() : Expression = {
    var expr = parseComparativeExpr()
    while (tokens.peek(classOf[LOGICAL_BIN_OPER])) {
      val logicalOper = tokens.consume(classOf[LOGICAL_BIN_OPER])
      while (tokens.peek(NL)) tokens.consume(NL)
      expr = classifyLogicalExpr(logicalOper, expr, parseLogicalExpr())
    }
    expr
  }

  def classifyLogicalExpr(oper: LOGICAL_BIN_OPER, left: Expression, right: Expression) : LogicalExpression = {
    oper match {
      case AND => LogicalAndExpression(left, right)
      case OR => LogicalOrExpression(left, right)
    }
  }

  def parseComparativeExpr() : Expression = {
    var expr = parseConsExpr()
    while (tokens.peek(classOf[COMPARATIVE_BIN_OPER])) {
      val oper = tokens.consume(classOf[COMPARATIVE_BIN_OPER])
      while (tokens.peek(NL)) tokens.consume(NL)
      expr = classifyComparativeExpr(oper, expr, parseConsExpr())
    }
    expr
  }

  def classifyComparativeExpr(oper: COMPARATIVE_BIN_OPER, left: Expression, right: Expression) : ComparativeExpression = {
    oper match {
      case LT => LessThanExpr(left, right)
      case GT => GreaterThanExpr(left, right)
      case LE => LessOrEqualThanExpr(left, right)
      case GE => GreaterOrEqualThanExpr(left, right)
      case EQUALS => EqualsExpr(left, right)
      case NOT_EQUALS => NotEqualsExpr(left, right)
      case MATCH => ReMatchExpr(left, right)
      case MATCHES => MatchExpr(left, right)
      case NOT_MATCHES => NoMatchExpr(left, right)
      case CONTAINS => ContainsExpr(left, right)
    }
  }

  def parseConsExpr() : Expression = {
    var expr = parseSumExpr()
    while (tokens.peek(CONS)) {
      tokens.consume(CONS)
      while (tokens.peek(NL)) tokens.consume(NL)
      expr = ConsExpression(expr, parseConsExpr())
    }
    expr
  }

  def parseSumExpr() : Expression = {
    var expr = parseMulExpr()
    while (tokens.peek(classOf[SUM_OPER])) {
      val oper : SUM_OPER = tokens.consume().get
      while (tokens.peek(NL)) tokens.consume(NL)
      expr = classifySumExpr(oper, expr, parseMulExpr())
    }
    expr
  }

  def classifySumExpr(oper: SUM_OPER, left: Expression, right: Expression) : SumExpression = {
    oper match {
      case PLUS => AddExpression(left, right)
      case MINUS => SubstractExpression(left, right)
      case PLUS_PLUS => ConcatExpression(left, right)
    }
  }

  def parseMulExpr() : Expression = {
    var expr = parsePowExpr()
    while (tokens.peek(classOf[MUL_OPER])) {
      val oper = tokens.consume(classOf[MUL_OPER])
      while (tokens.peek(NL)) tokens.consume(NL)
      expr = classifyMulExpr(oper, expr, parsePowExpr())
    }
    expr
  }

  def classifyMulExpr(oper: MUL_OPER, left: Expression, right: Expression) : MultExpression = {
    oper match {
      case MULT => MultiplyExpression(left, right)
      case DIV => DivideExpression(left, right)
      case MOD => ModExpression(left, right)
      case MULT_BIG => MultiplyBigExpression(left, right)
    }
  }


  def parsePowExpr() : Expression = {
    var expr = parseComposeExpr()
    while (tokens.peek(POW)) {
      tokens.consume(POW)
      expr = PowerExpression(expr, parsePowExpr())
    }
    expr
  }

  def parseComposeExpr() : Expression = {
    var expr = parsePostfixExpr()
    while (tokens.peek(classOf[COMPOSE_OPER])) {
      val op = tokens.consume(classOf[COMPOSE_OPER])
      expr = op match {
        case COMPOSE_FORWARD => ComposeExpressionForward(expr, parseComposeExpr())
        case COMPOSE_BACKWARD => ComposeExpressionBackward(expr, parseComposeExpr())
      }
    }
    expr
  }

  def parsePostfixExpr() : Expression = {
    var expr = parsePrimExpr()
    if (tokens.peek(ARROBA)) {
      val array = expr
      tokens.consume(ARROBA)
      val arg = parseLogicalExpr()
      expr = ArrayAccessExpression(array, arg)
    }
    expr
  }

  def parsePrimExpr() : Expression = {
    if (tokens.peek(LPAREN) && tokens.peek(2, classOf[OPER])) {
      parsePartialOper()
    }
    else if (tokens.peek(LPAREN) || tokens.peek(LBRACKET) || tokens.peek(LCURLY) || tokens.peek(HASHLCURLY)) {
      parseAtomicExpr()
    }
    else if (tokens.peek(classOf[LITERAL])) {
      parseAtomicExpr()
    }
    else if (tokens.peek(LAZY)) {
      tokens.consume(LAZY)
      LazyExpression(parsePipedExpr())
    }
    else if (tokens.peek(NEW)) {
      parseNewCtorExpression()
    }
    else if (tokens.peek(classOf[TID])) {
      parseConstructorExpr()
    }
    else {
      parseFuncCallExpr()
    }
  }

  def parseConstructorExpr() : ConstructorExpression = {
    val cls = tokens.consume(classOf[TID]).value
    var args = List.empty[Expression]
    if (tokens.peek(LPAREN)) {
      tokens.consume(LPAREN)
      if (!tokens.peek(RPAREN)) {
        val expr = parseExpr()
        args = expr :: args
        while (tokens.peek(COMMA)) {
          tokens.consume(COMMA)
          tokens.consumeOptionals(NL)
          val expr = parseExpr()
          args = expr :: args
        }
      }
      tokens.consume(RPAREN)
    }
    ConstructorExpression(cls, args.reverse)
  }

  def parseNewCtorExpression() : Expression = {
    tokens.consume(NEW)
    val cls = tokens.consume(classOf[TID]).value
    tokens.consume(LPAREN)
    var args = List.empty[Expression]
    if (!tokens.peek(RPAREN)) {
      val expr = parseExpr()
      args = expr :: args
      while (tokens.peek(COMMA)) {
        tokens.consume(COMMA)
        tokens.consumeOptionals(NL)
        val expr = parseExpr()
        args = expr :: args
      }
    }
    tokens.consume(RPAREN)
    NewCallExpression(cls, args.reverse)
  }

  def parseRecurExpr() : Expression = {
    tokens.consume(RECUR)
    var recurArgs = List.empty[Expression]
    while (!funcCallEndToken()) {
      val arg = parsePipedExpr()
      recurArgs = arg :: recurArgs
    }
    RecurExpr(recurArgs.reverse)
  }

  def parseFuncCallExpr() : Expression = {
    var expr : Expression = null
    //println(s"@@parseFunCallExpr (tokens=${tokens})")
    if (tokens.peek(classOf[ID])) {
      val id = tokens.consume(classOf[ID])
      expr = Identifier(id.value)
    }
    else if (tokens.peek(classOf[ATOM])) {
      expr = parseAtom()
    }
    if (!funcCallEndToken()) {
      var args = List.empty[Expression]
      val func = expr
      while (!funcCallEndToken()) {
        if (tokens.peek(classOf[ID])) {
          val id = tokens.consume(classOf[ID])
          expr = Identifier(id.value)
        } else {
          expr = parseDollarExpr()
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

  def funcCallEndToken() : Boolean = {
    if (tokens.isEmpty) {
      true
    } else {
      tokens.nextToken().exists { next =>
        next == NL || next.isInstanceOf[PIPE_OPER] || next.isInstanceOf[OPER] || next.isInstanceOf[DECL] ||
          next == INDENT || next == DEDENT || next == ASSIGN  ||
          next == DOLLAR || next == COMMA || next == LET || next == VAR || next == DO || next == THEN ||
          next == ELSE || next == RPAREN || next == IN || next == RBRACKET || next == RCURLY || next == WHERE
      }
    }

  }

  def parseAtomicExpr() : Expression = {
    var expr: Expression = null
    if (tokens.peek(LPAREN)) {
      tokens.consume(LPAREN)
      expr = parsePipedExpr()
      if (tokens.peek(COMMA)) {
        var tupleElem = expr
        var tupleElements = List.empty[Expression]
        tupleElements = tupleElem :: tupleElements
        while (tokens.peek(COMMA)) {
          tokens.consume(COMMA)
          tupleElem = parsePipedExpr()
          tupleElements = tupleElem :: tupleElements
        }
        if (tokens.peek(DOTDOTDOT)) {
          tokens.consume(DOTDOTDOT)
          expr = InfiniteTupleExpr(tupleElements.reverse)

        } else {
          expr = TupleExpr(tupleElements.reverse)
        }
      }
      if (expr.isInstanceOf[Identifier]) {
        expr = FunctionCallExpression(expr, List.empty[Expression])
      }
      tokens.consume(RPAREN)
    }
    else if (tokens.peek(LBRACKET)) {
      expr = parseRangeExpr()
    }
    else if (tokens.peek(LCURLY)) {
      expr = parseDictionaryExpr()
    }
    else if (tokens.peek(HASHLCURLY)) {
      expr = parseSetExpr()
    }
    else {
      expr = parseLiteral()
    }


    if (expr == null) {
      println(s"!!! expr == null, tokens = $tokens")
      throw UnexpectedTokenClassException()
    }
    expr
  }

  def parseAtom() : Expression = {
    val atom = tokens.consume(classOf[ATOM])
    Atom(atom.value)
  }

  def parseLiteral() : Expression = {
    if (tokens.peek(classOf[BOOL_LITERAL])) {
      BoolLiteral(tokens.consume(classOf[BOOL_LITERAL]).value)
    }
    else if (tokens.peek(classOf[INT_LITERAL])) {
      IntLiteral(tokens.consume(classOf[INT_LITERAL]).value)
    }
    else if (tokens.peek(classOf[LONG_LITERAL])) {
      LongLiteral(tokens.consume(classOf[LONG_LITERAL]).value)
    }
    else if (tokens.peek(classOf[BIGINT_LITERAL])) {
      BigIntLiteral(tokens.consume(classOf[BIGINT_LITERAL]).value)
    }
    else if (tokens.peek(classOf[DOUBLE_LITERAL])) {
      DoubleLiteral(tokens.consume(classOf[DOUBLE_LITERAL]).value)
    }
    else if (tokens.peek(classOf[STRING_LITERAL])) {
      StringLiteral(tokens.consume(classOf[STRING_LITERAL]).value)
    }
    else if (tokens.peek(classOf[ISODATETIME_LITERAL])) {
      DateTimeLiteral(tokens.consume(classOf[ISODATETIME_LITERAL]).value)
    }
    else if (tokens.peek(classOf[CHAR_LITERAL])) {
      CharLiteral(tokens.consume(classOf[CHAR_LITERAL]).chr)
    }
    else if (tokens.peek(classOf[REGEXP_LITERAL])) {
      RegexpLiteral(tokens.consume(classOf[REGEXP_LITERAL]).re)
    }
    else if (tokens.peek(classOf[FSTRING_LITERAL])) {
      FStringLiteral(tokens.consume(classOf[FSTRING_LITERAL]).value)
    }
    else  {
      null
    }
  }

  def parsePartialOper() : Expression = {
    tokens.consume(LPAREN)
    val parOp = tokens.consume(classOf[OPER])
    var listOfArgs: List[Expression] = List.empty[Expression]
    while (!tokens.peek(RPAREN)) {
      val expr = parseLogicalExpr()
      listOfArgs = expr :: listOfArgs
    }
    tokens.consume(RPAREN)
    classifyPartialOper(parOp, listOfArgs)
  }

  def classifyPartialOper(parOp: OPER, args: List[Expression]) : Expression = {
    parOp match {
      case PLUS => PartialAdd(args)
      case MINUS => PartialSub(args)
      case MULT => PartialMul(args)
      case DIV => PartialDiv(args)
      case MOD => PartialMod(args)
      case EQUALS => PartialEQ(args)
      case NOT_EQUALS => PartialNE(args)
      case LT => PartialLT(args)
      case GT => PartialGT(args)
      case LE => PartialLE(args)
      case GE => PartialGE(args)
      case POW => PartialPow(args)
      case CONS => PartialCons(args)
      case PLUS_PLUS => PartialConcat(args)
      case _ => throw PartialOperNotSupported(parOp)
    }
  }

  def parseRangeExpr() : Expression = {
    tokens.consume(LBRACKET)
    if (tokens.peek(RBRACKET)) {
      tokens.consume(RBRACKET)
      return EmptyListExpresion()
    }
    var expr = parseLogicalExpr()
    if (tokens.peek(COMMA)) {
      var list = List.empty[Expression]
      while(tokens.peek(COMMA)) {
        tokens.consume(COMMA)
        list = expr :: list
        expr = parseLogicalExpr()
      }
      list = expr :: list
      expr = ListExpression(list.reverse, None)
    }
    if (tokens.peek(DOTDOT)) {
      tokens.consume(DOTDOT)
      expr match {
        case expression: ListExpression if expression.expressions.size == 2 =>
          val lexpr = expression.expressions
          val rangeInit = lexpr.head
          val rangeIncrement = SubstractExpression(lexpr.tail.head, lexpr.head)
          val rangeEnd = parseLogicalExpr()
          expr = RangeWithIncrementExpression(rangeInit, rangeIncrement, rangeEnd)

        case _ =>
          val rangeInit = expr
          val rangeEnd = parseLogicalExpr()
          expr = RangeExpression(rangeInit, rangeEnd)
      }
    }
    if (tokens.peek(DOTDOTLESS)) {
      tokens.consume(DOTDOTLESS)
      expr match {
        case expression: ListExpression if expression.expressions.size == 2 =>
          val lexpr = expression.expressions
          val rangeInit = lexpr.head
          val rangeIncrement = SubstractExpression(lexpr.tail.head, lexpr.head)
          val rangeEnd = parseLogicalExpr()
          expr = RangeWithIncrementExpressionUntil(rangeInit, rangeIncrement, rangeEnd)

        case _ =>
          val rangeInit = expr
          val rangeEnd = parseLogicalExpr()
          expr = RangeExpressionUntil(rangeInit, rangeEnd)
      }
    }
    if (tokens.peek(DOTDOTDOT)) {
      tokens.consume(DOTDOTDOT)
      expr = InfiniteRangeExpression(expr)
    }
    else if (tokens.peek(GUARD)) {
      tokens.consume(GUARD)
      var listOfGuards = List.empty[ListGuard]
      var guard = parseListGuard()
      listOfGuards = guard :: listOfGuards
      while (tokens.peek(COMMA)) {
        tokens.consume(COMMA)
        guard = parseListGuard()
        listOfGuards = guard :: listOfGuards
      }
      expr = ListExpression(List(expr), Some(listOfGuards.reverse))
    }
    tokens.consume(RBRACKET)
    if (!expr.isInstanceOf[ValidRangeExpression])
      expr = ListExpression(List(expr), None)
    expr
  }

  def parseListGuard() : ListGuard = {
    if (tokens.peek(LPAREN) && tokens.peek(2, classOf[ID]) && tokens.peek(3, COMMA)) {
      tokens.consume(LPAREN)
      var listOfIds = List.empty[String]
      val id = tokens.consume(classOf[ID])
      listOfIds = id.value :: listOfIds
      while (tokens.peek(COMMA)) {
        tokens.consume(COMMA)
        val id = tokens.consume(classOf[ID])
        listOfIds = id.value :: listOfIds
      }
      tokens.consume(RPAREN)
      tokens.consume(BACK_ARROW)
      val expr = parsePipedExpr()
      ListGuardDeclTupled(listOfIds.reverse, expr)
    }
    else if (tokens.peek(classOf[ID]) && tokens.peek(2, BACK_ARROW)) {
      val id = tokens.consume(classOf[ID])
      tokens.consume(BACK_ARROW)
      val expr = parsePipedExpr()
      ListGuardDecl(id.value, expr)
    } else {
      ListGuardExpr(parsePipedExpr())
    }
  }

  def parseSetExpr(): Expression = {
    tokens.consume(HASHLCURLY)
    var listOfValues = List.empty[Expression]
    val value = parseExpr()
    listOfValues = value :: listOfValues
    while (tokens.peek(COMMA)) {
      tokens.consume(COMMA)
      tokens.consumeOptionals(NL)
      val value = parseExpr()
      listOfValues = value :: listOfValues
    }
    tokens.consume(RCURLY)
    SetExpression(listOfValues.reverse)
  }

  def parseDictionaryExpr() : Expression = {
    tokens.consume(LCURLY)
    var listOfPairs = List.empty[(Expression, Expression)]
    val key = parseKeyExpr()
    val value = parseExpr()
    listOfPairs = (key, value) :: listOfPairs
    while (tokens.peek(COMMA)) {
      tokens.consume(COMMA)
      tokens.consumeOptionals(NL)
      val key = parseKeyExpr()
      val value = parseExpr()
      listOfPairs = (key, value) :: listOfPairs
    }
    tokens.consume(RCURLY)
    DictionaryExpression(listOfPairs.reverse)
  }

  def parseKeyExpr() : Expression = {
    if (tokens.peek(classOf[ATOM]))
      parseAtom()
    else
      parseLiteral()
  }

}
