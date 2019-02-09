package parser

import lexer._

import scala.collection.mutable


class Parser(filename:String, val tokens: TokenStream, defaultSymbolTable: Option[SymbolTable]) {


  def parse() : LangNode = {
    if (tokens.peek(MODULE)) {
      parseModule()
    }
    else {
      Module(filename.substring(filename.lastIndexOf('/')+1, filename.lastIndexOf('.')), parseModuleNodes())
    }
  }

  def parseModule() : Module = {
    tokens.consume(MODULE)
    val moduleName = tokens.consume(classOf[ID])
    tokens.consume(NL)
    Module(moduleName.value, parseModuleNodes())
  }

  def parseModuleNodes() : List[LangNode] = {
    var result = List.empty[LangNode]
    while (tokens.nonEmpty) {
      if (tokens.peek(DEF))
        result = multiDef(parseDef()) :: result
      else if (tokens.peek(LET))
        result = parseLet() :: result
      else if (tokens.peek(VAR))
        result = parseVar() :: result
      else
        result = parsePipedExpr() :: result
      while (tokens.peek(NL)) tokens.consume(NL)
      //println(s"PARSED SO FAR: ${result.reverse}\n\n")
    }
    filter(result.reverse)
  }

  var defs = mutable.HashMap.empty[String, MultiDefDecl]

  private[this] def multiDef(node: LangNode) : LangNode = {
    val decl = node.asInstanceOf[SimpleDefDecl]
    println(s"@multiDef(node = ${node}) ")
    if (defs.contains(decl.id)) {
      defs.get(decl.id).map { defDecl =>
        val decls = decl :: defDecl.decls
        val mDecl = MultiDefDecl(defDecl.id, decls)
        defs.update(mDecl.id,  mDecl)
        println(s"@defs=>${defs}")
        mDecl
      }.get
    }
    else {
      val mDecl = MultiDefDecl(decl.id, List(decl))
      defs.put(mDecl.id, mDecl)
      println(s"@defs=>${defs}")
      mDecl
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

  def parseDef() : LangNode = {
    tokens.consume(DEF)
    createContext()
    val defId = tokens.consume(classOf[ID])
    val args = parseDefArgs()
    if (tokens.peek(NL)) {
      tokens.consume(NL)
      val body = parseDefBodyGuards()
      body match {
        case bd: BodyGuardsExpresionAndWhere =>
          destroyContext()
          SimpleDefDecl(defId.value, args, BodyGuardsExpresion(bd.guards), Some(bd.whereBlock))
        case _ =>
          val where = tryParseWhereBlock()
          destroyContext()
          SimpleDefDecl(defId.value, args, body, where)
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
      destroyContext()
      SimpleDefDecl(defId.value, args, body, where)
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
    var result = if (tokens.peek(WHERE)) {
      BodyGuardsExpresionAndWhere(listOfGuards, parseUnindentedWhereBlock())
    } else {
      BodyGuardsExpresion(listOfGuards)
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
        tokens.consume(NL)
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
    var whereBlock = parseUnindentedWhereBlock()
    tokens.consume(DEDENT)
    whereBlock
  }

  def parseUnindentedWhereBlock() : WhereBlock = {
    tokens.consume(WHERE)
    var listOfWhereDefs = List.empty[WhereDef]
    if (!tokens.peek(NL)) {
      val whereDef = parseWhereDef()
      listOfWhereDefs = whereDef :: listOfWhereDefs
      tokens.consume(NL)
    } else {
      tokens.consume(NL)
    }
    if (tokens.peek(INDENT)) {
      tokens.consume(INDENT)
      while (!tokens.peek(DEDENT)) {
        val whereDef = parseWhereDef()
        listOfWhereDefs = whereDef :: listOfWhereDefs
        if (tokens.peek(NL)) while (tokens.peek(NL)) tokens.consume(NL)
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
    while (!tokens.peek(ASSIGN) && !tokens.peek(GUARD)) {
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
        WhereDefSimple(listOfIds.head, if (listOfArgs.isEmpty) None else Some(listOfArgs), body)
      else
        WhereDefTupled(listOfIds, if (listOfArgs.isEmpty) None else Some(listOfArgs), body)
    } else if (tokens.peek(GUARD)) {
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

  def parseDefArgs() : List[DefArg] = {
    var result = List.empty[DefArg]
    while (!tokens.peek(ASSIGN) && !tokens.peek(NL)) {
      val expr = parseDefArg()
      result = DefArg(expr) :: result
    }
    result.reverse
  }

  def parseDefArg() : Expression = {
    if (tokens.peek(classOf[ID])) {
      val id = tokens.consume(classOf[ID])
      Identifier(id.value)
    } else {
      parseLogicalExpr()
    }
  }

  def parseVar() : LangNode = {
    tokens.consume(VAR)
    val listOfIds = parseLetVarList()
    tokens.consume(ASSIGN)
    val listOfValues = parseLetValueList()
    if (tokens.peek(NL))
      tokens.consume(NL)
    if (listOfIds.length != listOfValues.length) {
      throw InvalidLetDeclaration("ids and values doesn't matches")
    }
    VarDecl(listOfIds.zip(listOfValues))
  }

  def parseVarExpr() : Expression = {
    tokens.consume(VAR)
    val listOfIds = parseLetVarList()
    tokens.consume(ASSIGN)
    val listOfValues = parseLetValueList()
    if (tokens.peek(NL))
      tokens.consume(NL)
    if (listOfIds.length != listOfValues.length) {
      throw InvalidLetDeclaration("ids and values doesn't matches")
    }
    addSimpleVarList(listOfIds)

    VarDeclExpr(listOfIds.zip(listOfValues))
  }


  def parseLet() : LangNode = {
    tokens.consume(LET)
    var listOfIds = parseLetVarList()
    tokens.consume(ASSIGN)
    var listOfValues = parseLetValueList()
    if (tokens.peek(NL))
      tokens.consume(NL)
    if (listOfIds.length != listOfValues.length) {
      throw InvalidLetDeclaration("ids and values doesn't matches")
    }
    addSimpleVarList(listOfIds)
    LetDecl(listOfIds.zip(listOfValues))
  }

  def parseLetExpr() : Expression = {
    tokens.consume(LET)
    val listOfIds = parseLetVarList()
    tokens.consume(ASSIGN)
    val listOfValues = parseLetValueList()
    if (listOfIds.length != listOfValues.length) {
      throw InvalidLetDeclaration("ids and values doesn't matches")
    }
    LetDeclExpr(listOfIds.zip(listOfValues))
  }

  def parseLetVarList() : List[DeclVariable] = {
    var listOfVar = List.empty[DeclVariable]
    var letVar = parseLetVar()
    while (tokens.peek(COMMA)) {
      tokens.consume(COMMA)
      listOfVar = letVar :: listOfVar
      letVar = parseLetVar()
    }
    listOfVar = letVar :: listOfVar
    listOfVar.reverse
  }

  def parseLetVar() : DeclVariable = {
    if (tokens.peek(LPAREN))
      parseLetTupledVars()
    else {
      val idToken = tokens.consume(classOf[ID])
      DeclIdVar(idToken.value)
    }
  }

  def parseLetTupledVars() : DeclTupledIdVars = {
    tokens.consume(LPAREN)
    var listOfIds = List.empty[String]
    var id = tokens.consume(classOf[ID])
    while (tokens.peek(COMMA)) {
      tokens.consume(COMMA)
      listOfIds = id.value :: listOfIds
      id = tokens.consume(classOf[ID])
    }
    listOfIds = id.value :: listOfIds
    tokens.consume(RPAREN)
    DeclTupledIdVars(listOfIds.reverse)
  }

  def parseLetValueList() : List[Expression] = {
    var listOfValues = List.empty[Expression]
    var expr = parsePipedExpr()
    while (tokens.peek(COMMA)) {
      tokens.consume(COMMA)
      while (tokens.peek(NL)) tokens.consume(NL)
      listOfValues = expr :: listOfValues
      expr = parsePipedExpr()
    }
    listOfValues = expr :: listOfValues
    listOfValues.reverse
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
    else
      parseAssignExpr()
  }

  def parseControlExpr() : Expression = {
    if (tokens.peek(FOR)) {
      return parseForExpr()
    }
    else if (tokens.peek(IF)) {
      return parseIfExpr()
    }
    else if (tokens.peek(WHEN)) {
      return parseWhenExpr()
    }
    else if (tokens.peek(LOOP)) {
      return parseLoopExpr()
    }
    else if (tokens.peek(UNTIL)) {
      return parseWhileExpr()
    }
    else if (tokens.peek(WHILE)) {
      return parseWhileExpr()
    }
    else if (tokens.peek(REPEAT)) {
      return parseRepeatExpr()
    }
    else if (tokens.peek(RECUR)) {
      return parseRecurExpr()
    }
    println(s"ERROR PARSE CONTROL tokens= $tokens")
    throw InvalidNodeException(tokens.nextToken())
  }

  def parseRecurExpr() : Expression = {
    tokens.consume(RECUR)
    RecurExpr(parsePipedExpr())
  }

  def parseRepeatExpr() : Expression = {
    tokens.consume(REPEAT)
    if (!tokens.peek(WITH))
      RepeatExpr(None)
    else {
      tokens.consume(WITH)
      var repeatVariables = List.empty[RepeatNewVarValue]
      var repVar = parseRepeatAssign()
      repeatVariables = repVar :: repeatVariables
      while (tokens.peek(COMMA)) {
        tokens.consume(COMMA)
        repVar = parseRepeatAssign()
        repeatVariables = repVar :: repeatVariables
      }
      if (tokens.peek(NL)) while (tokens.peek(NL)) tokens.consume(NL)
      RepeatExpr(Some(repeatVariables.reverse))
    }
  }

  def parseRepeatAssign() : RepeatNewVarValue = {
    val id = tokens.consume(classOf[ID])
    tokens.consume(ASSIGN)
    val expr = parsePipedExpr()
    RepeatNewVarValue(id.value, expr)
  }

  def parseForExpr() : Expression = {
    tokens.consume(FOR)
    val forDecls = parseForDecls()
    val forBody = parseForBody()
    ForExpression(forDecls, forBody)
  }

  def parseForDecls() : List[ForVarDeclIn] = {
    var listOfDecls = List.empty[ForVarDeclIn]
    var forVarDecl = parseForVarDecl()
    while (tokens.peek(COMMA)) {
      tokens.consume(COMMA)
      listOfDecls = forVarDecl :: listOfDecls
    }
    listOfDecls = forVarDecl :: listOfDecls
    listOfDecls.reverse
  }

  def parseForVarDecl() : ForVarDeclIn = {
    var id = tokens.consume(classOf[ID])
    if (tokens.peek(IN)) {
      tokens.consume(IN)
      return ForVarDeclIn(id.value, parsePipedExpr())
    }
    throw UnexpectedTokenClassException()
  }

  def parseWhileExpr() : Expression = {
    tokens.consume(WHILE)
    val comp = parseLogicalExpr()
    tokens.consume(DO)
    val body = if (!tokens.peek(NL)) {
      parsePipedExpr()
    } else {
      tokens.consume(NL)
      parseBlockExpr()
    }
    WhileExpression(comp, body)
  }


  def parseUntilExpr() : Expression = {
    tokens.consume(UNTIL)
    val comp = parseLogicalExpr()
    tokens.consume(DO)
    val body = if (!tokens.peek(NL)) {
      parsePipedExpr()
    } else {
      tokens.consume(NL)
      parseBlockExpr()
    }
    UntilExpression(comp, body)
  }

  def parseLoopExpr() : Expression = {
    tokens.consume(LOOP)

    val loopDecls = parseLoopDecls()

    var guardExpr : Option[LoopGuard] = None
    if (tokens.peek(NL)) while(tokens.peek(NL)) tokens.consume(NL)

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
    listOfDecls = loopVarDecl :: listOfDecls
    listOfDecls.reverse
  }

  def parseLoopVarDecl() : LoopVarDecl = {
    var id = tokens.consume(classOf[ID])
    if (tokens.peek(ASSIGN)) {
      tokens.consume(ASSIGN)
      return LoopVarDecl(id.value, parsePipedExpr())
    }
    throw UnexpectedTokenClassException()
  }

  def parseForBody() : Expression = {
    tokens.consume(DO)
    if (!tokens.peek(NL))
      parsePipedExpr()
    else
    {
      tokens.consume(NL)
      parseBlockExpr()
    }
  }

  def parseBlockExpr() : Expression = {
    tokens.consume(INDENT)
    createContext()
    var listOfExpressions = List.empty[Expression]
    var loop = 0
    while (!tokens.peek(DEDENT)) {
      loop += 1
      var expr = parsePipedExpr()
      if (tokens.peek(NL)) tokens.consume(NL)
      listOfExpressions = expr :: listOfExpressions
    }
    tokens.consume(DEDENT)
    destroyContext()
    BlockExpression(listOfExpressions.reverse)
  }

  def parseIfExpr() : Expression = {
    tokens.consume(IF)
    var comp = parseLogicalExpr()
    tokens.consume(THEN)
    var thenPart = if (!tokens.peek(NL)) {
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
    var comp = parseLogicalExpr()
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
    var expr = parseLambdaExpr()
    if (tokens.peek(classOf[ASSIGN_OPER])) {
      if (!expr.isInstanceOf[AssignableExpression])
        throw CantAssignToExpression()
      val oper = tokens.consume(classOf[ASSIGN_OPER])
      val right = parsePipedExpr()
      expr = oper match {
        case ASSIGN => SimpleAssignExpr(expr, right)
        case PLUS_ASSIGN => PlusAssignExpr(expr, right)
        case MINUS_ASSIGN => MinusAssignExpr(expr, right)
        case MULT_ASSIGN => MultAssignExpr(expr, right)
        case DIV_ASSIGN => DivAssignExpr(expr, right)
        case MOD_ASSIGN => ModAssignExpr(expr, right)
      }
    }
    expr
  }

  def parseLambdaExpr() : Expression = {
    var expr : Expression = null
    if (!tokens.peek(LAMBDA)) {
      expr = parseLogicalExpr()
    }
    else {
      tokens.consume(LAMBDA)
      var args = List.empty[LambdaArg]
      val arg = parseLambdaArg()
      args = arg :: args
      while (tokens.peek(COMMA)) {
        tokens.consume(COMMA)
        val id = tokens.consume(classOf[ID])
        args = LambdaSimpleArg(id.value) :: args
      }
      if (!tokens.peek(ARROW)) {
        throw InvalidLambdaExpression(tokens.nextToken())
      }
      tokens.consume(ARROW)
      expr = LambdaExpression(args.reverse, parseExpr())
    }
    expr
  }

  def parseLambdaArg() : LambdaArg = {
    if (tokens.peek(classOf[ID])) {
      val id = tokens.consume(classOf[ID])
      LambdaSimpleArg(id.value)
    }
    else if (tokens.peek(LPAREN)) {
      tokens.consume(LPAREN)
      var ids = List.empty[String]
      var id = tokens.consume(classOf[ID]).value
      ids = id :: ids
      while (tokens.peek(COMMA)) {
        tokens.consume(COMMA)
        var id = tokens.consume(classOf[ID]).value
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
      case MATCHES => MatchExpr(left, right)
      case NOT_MATCHES => NoMatchExpr(left, right)
      case IN => InExpr(left, right)
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
    }
  }


  def parsePowExpr() : Expression = {
    var expr = parsePostfixExpr()
    while (tokens.peek(POW)) {
      tokens.consume(POW)
      expr = PowerExpression(expr, parsePowExpr())
    }
    expr
  }

  def parsePostfixExpr() : Expression = {
    var expr = parsePrimExpr()
    if (tokens.peek(ARROBA)) {
      var array = expr
      tokens.consume(ARROBA)
      val arg = parseLogicalExpr()
      expr = ArrayAccessExpression(array, arg)
    }
    expr
  }

  def parsePrimExpr() : Expression = {
    if (tokens.peek(LPAREN) && tokens.peek(2, classOf[OPER])) {
      return parsePartialOper()
    }
    else if (tokens.peek(LPAREN) || tokens.peek(LBRACKET)) {
      return parseAtomExpr()
    }
    else if (tokens.peek(LCURLY)) {
      return parseAtomExpr()
    }
    else if (tokens.peek(classOf[LITERAL])) {
      return parseAtomExpr()
    }
    parseFuncCallExpr()
  }

  def parseFuncCallExpr() : Expression = {
    var expr : Expression = null
    println(s"@@parseFunCallExpr (tokens=${tokens})")
    if (tokens.peek(classOf[ID])) {
      val id = tokens.consume(classOf[ID])
      expr = Identifier(id.value)
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
      expr = FunctionCallExpression(func, args.reverse)
    }
    expr
  }


  var contextList = List.empty[mutable.HashMap[String, DeclVariable]]

  def createContext() : Unit = {
    contextList = mutable.HashMap.empty[String, DeclVariable] :: contextList
  }

  def destroyContext() : Unit = {
    if (contextList.isEmpty)
      throw CantFindContext()
    contextList = contextList.tail
  }

  def addSimpleVar(variable: DeclVariable): Unit = {
    if (contextList.isEmpty)
      createContext()
    val context = contextList.head
    variable match {
      case DeclIdVar(id) =>
        context.put(id, variable)
      case DeclTupledIdVars(tuple) =>
        for (id <- tuple) {
          context.put(id, variable)
        }
      case LoopVarDecl(id, _) =>
        context.put(id, variable)
      case ForVarDeclIn(id, _) =>
        context.put(id, variable)
      case _ =>
        ???
    }
  }

  def addSimpleVarList(variables: List[DeclVariable]): Unit = {
    for (i <- variables) {
      addSimpleVar(i)
    }
  }

  def isSimpleVar(expression: Expression): Boolean = {
    expression match {
      case Identifier(id) =>
        contextList.exists(context => context.contains(id))
      case _ => ???
    }
  }


  def funcCallEndToken() : Boolean = {
    tokens.nextToken().exists { next =>
      next == NL || next.isInstanceOf[PIPE_OPER] || next.isInstanceOf[OPER] || next.isInstanceOf[DECL] || next == INDENT || next == DEDENT ||
        next == DOLLAR || next == COMMA || next == LET || next == VAR || next == DO || next == THEN || next == ELSE ||
      next == RPAREN
    }
  }

  def parseAtomExpr() : Expression = {
    var expr : Expression = null
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
        expr = TupleExpr(tupleElements)
      }
      tokens.consume(RPAREN)
    }
    else if (tokens.peek(LBRACKET)) {
      expr = parseRangeExpr()

    }
    else if (tokens.peek(LCURLY)) {
      // TODO implements sets
      tokens.consume(LCURLY)
      expr = parsePipedExpr()
      tokens.consume(RCURLY)
    }

    else if (tokens.peek(classOf[INT_LITERAL])) {
      val num = tokens.consume(classOf[INT_LITERAL])
      expr = IntLiteral(num.value)
    }
    else if (tokens.peek(classOf[DOUBLE_LITERAL])) {
      val num = tokens.consume(classOf[DOUBLE_LITERAL])
      expr = DoubleLiteral(num.value)
    }
    else if (tokens.peek(classOf[STRING_LITERAL])) {
      val str = tokens.consume(classOf[STRING_LITERAL])
      expr = StringLiteral(str.value)
    }
    else if (tokens.peek(classOf[ISODATETIME_LITERAL])) {
      val date = tokens.consume(classOf[ISODATETIME_LITERAL])
      expr = DateTimeLiteral(date.value)
    }
    else if (tokens.peek(classOf[CHAR_LITERAL])) {
      val chr = tokens.consume(classOf[CHAR_LITERAL])
      expr = CharLiteral(chr.chr)
    }
    else if (tokens.peek(classOf[REGEXP_LITERAL])) {
      val re = tokens.consume(classOf[REGEXP_LITERAL])
      expr = RegexpLiteral(re.re)
    }
    else if (tokens.peek(classOf[FSTRING_LITERAL])) {
      val fs = tokens.consume(classOf[FSTRING_LITERAL])
      expr = FStringLiteral(fs.value)
    }
    if (expr == null) {
      println(s"!!! expr == null, tokens = ${tokens}")
      throw UnexpectedTokenClassException()
    }
    expr
  }

  def parsePartialOper() : Expression = {
    tokens.consume(LPAREN)
    val parOp = tokens.consume(classOf[OPER])
    var listOfArgs = List.empty[Expression]
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
      expr = ListExpression(List(expr), if (listOfGuards.isEmpty) None else Some(listOfGuards.reverse))
    }
    tokens.consume(RBRACKET)
    expr
  }

  def parseListGuard() : ListGuard = {
    if (tokens.peek(classOf[ID]) && tokens.peek(2, BACK_ARROW)) {
      val id = tokens.consume(classOf[ID])
      tokens.consume(BACK_ARROW)
      val expr = parsePipedExpr()
      return ListGuardDecl(id.value, expr)
    } else {
      return ListGuardExpr(parsePipedExpr())
    }
  }

}
