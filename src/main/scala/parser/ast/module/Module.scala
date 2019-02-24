package parser.ast.module

import lexer._
import parser._
import parser.ast.expressions._
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
      var inner = false
      if (tokens.peek(PRIVATE)) {
        tokens.consume(PRIVATE)
        inner = true
      }
      if (tokens.peek(CLASS)) {
        result = ClassDecl.parse(inner, tokens) :: result
      }
      else if (tokens.peek(DATA)) {
        result = AdtDecl.parse(inner, tokens) :: result
      }
      else if (tokens.peek(DEF)) {
        result = multiDef(parseDef(inner, tokens)) :: result
      }
      else if (tokens.peek(DISPATCH)) {
        result = DispatchDecl.parse(inner, tokens) :: result
      }
      else if (tokens.peek(EXTENDS)) {
        result = ExtendsDecl.parse(inner, tokens) :: result
      }
      else if (tokens.peek(RECORD)) {
        result = RecordDecl.parse(inner, tokens) :: result
      }
      else if (tokens.peek(TRAIT)) {
        result = TraitDecl.parse(inner, tokens) :: result
      }
      else {
        result = TopLevelExpression.parse(tokens) :: result
      }
      tokens.consumeOptionals(NL)
    }
    filter(result.reverse)
  }



  def parseListOfIds(tokens:TokenStream): List[String] = {
    val arg = tokens.consume(classOf[ID]).value
    var args = List(arg)
    while (tokens.peek(COMMA)) {
      tokens.consume(COMMA)
      val arg = tokens.consume(classOf[ID]).value
      args = arg :: args
    }
    args.reverse
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
      val l = parseListOfIds(tokens)
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


  def parseWhileExpr(tokens:TokenStream) : Expression = {
    tokens.consume(WHILE)
    val comp = LogicalExpression.parse(tokens)
    tokens.consume(DO)
    WhileExpression(comp, parsePipedOrBodyExpression(tokens))
  }


  def parseUntilExpr(tokens:TokenStream) : Expression = {
    tokens.consume(UNTIL)
    val comp = LogicalExpression.parse(tokens)
    tokens.consume(DO)
    UntilExpression(comp, parsePipedOrBodyExpression(tokens))
  }


  def parseWhenExpr(tokens:TokenStream) : Expression = {
    tokens.consume(WHEN)
    val comp = LogicalExpression.parse(tokens)
    tokens.consume(THEN)
    if (!tokens.peek(NL)) {
      WhenExpression(comp, ForwardPipeFuncCallExpression.parse(tokens))
    }
    else {
      tokens.consume(NL)
      WhenExpression(comp, BlockExpression.parse(tokens))
    }
  }


  def parseLambdaExpr(tokens:TokenStream) : Expression = {
    if (!tokens.peek(LAMBDA)) {
      LogicalExpression.parse(tokens)
    }
    else {
      tokens.consume(LAMBDA)
      var args = List.empty[LambdaArg]
      val arg = parseLambdaArg(tokens)
      args = arg :: args
      while (tokens.peek(COMMA)) {
        tokens.consume(COMMA)
        val arg = parseLambdaArg(tokens)
        args = arg :: args
      }
      if (!tokens.peek(ARROW)) {
        throw InvalidLambdaExpression(tokens.nextToken())
      }
      tokens.consume(ARROW)
      val expr = LambdaExpression(args.reverse, ParseExpr.parse(tokens))
      expr
    }
  }

  def parseLambdaArg(tokens:TokenStream) : LambdaArg = {
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

  def parseComparativeExpr(tokens:TokenStream) : Expression = {
    var expr = parseConsExpr(tokens)
    while (tokens.peek(classOf[COMPARATIVE_BIN_OPER])) {
      val oper = tokens.consume(classOf[COMPARATIVE_BIN_OPER])
      while (tokens.peek(NL)) tokens.consume(NL)
      expr = classifyComparativeExpr(oper, expr, parseConsExpr(tokens))
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

  def parseConsExpr(tokens:TokenStream) : Expression = {
    var expr = parseSumExpr(tokens)
    while (tokens.peek(CONS)) {
      tokens.consume(CONS)
      while (tokens.peek(NL)) tokens.consume(NL)
      expr = ConsExpression(expr, parseConsExpr(tokens))
    }
    expr
  }

  def parseSumExpr(tokens:TokenStream) : Expression = {
    var expr = parseMulExpr(tokens)
    while (tokens.peek(classOf[SUM_OPER])) {
      val oper : SUM_OPER = tokens.consume().get
      while (tokens.peek(NL)) tokens.consume(NL)
      expr = classifySumExpr(oper, expr, parseMulExpr(tokens))
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

  def parseMulExpr(tokens:TokenStream) : Expression = {
    var expr = parsePowExpr(tokens)
    while (tokens.peek(classOf[MUL_OPER])) {
      val oper = tokens.consume(classOf[MUL_OPER])
      while (tokens.peek(NL)) tokens.consume(NL)
      expr = classifyMulExpr(oper, expr, parsePowExpr(tokens))
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


  def parsePowExpr(tokens:TokenStream) : Expression = {
    var expr = parseComposeExpr(tokens)
    while (tokens.peek(POW)) {
      tokens.consume(POW)
      expr = PowerExpression(expr, parsePowExpr(tokens))
    }
    expr
  }

  def parseComposeExpr(tokens:TokenStream) : Expression = {
    var expr = parsePostfixExpr(tokens)
    while (tokens.peek(classOf[COMPOSE_OPER])) {
      val op = tokens.consume(classOf[COMPOSE_OPER])
      expr = op match {
        case COMPOSE_FORWARD => ComposeExpressionForward(expr, parseComposeExpr(tokens))
        case COMPOSE_BACKWARD => ComposeExpressionBackward(expr, parseComposeExpr(tokens))
      }
    }
    expr
  }

  def parsePostfixExpr(tokens:TokenStream) : Expression = {
    var expr = parsePrimExpr(tokens)
    if (tokens.peek(ARROBA)) {
      val array = expr
      tokens.consume(ARROBA)
      val arg = LogicalExpression.parse(tokens)
      expr = ArrayAccessExpression(array, arg)
    }
    expr
  }

  def parsePrimExpr(tokens:TokenStream) : Expression = {
    if (tokens.peek(LPAREN) && tokens.peek(2, classOf[OPER])) {
      parsePartialOper(tokens)
    }
    else if (tokens.peek(LPAREN) || tokens.peek(LBRACKET) || tokens.peek(LCURLY) || tokens.peek(HASHLCURLY)) {
      parseAtomicExpr(tokens)
    }
    else if (tokens.peek(classOf[LITERAL])) {
      parseAtomicExpr(tokens)
    }
    else if (tokens.peek(LAZY)) {
      tokens.consume(LAZY)
      LazyExpression(ForwardPipeFuncCallExpression.parse(tokens))
    }
    else if (tokens.peek(NEW)) {
      parseNewCtorExpression(tokens)
    }
    else if (tokens.peek(classOf[TID])) {
      ConstructorExpression.parse(tokens)
    }
    else {
      parseFuncCallExpr(tokens)
    }
  }


  def parseNewCtorExpression(tokens:TokenStream) : Expression = {
    tokens.consume(NEW)
    val cls = tokens.consume(classOf[TID]).value
    tokens.consume(LPAREN)
    val args = if (tokens.peek(RPAREN)) List.empty[Expression] else parseListOfExpressions(tokens)
    tokens.consume(RPAREN)
    NewCallExpression(cls, args)
  }



  def parseFuncCallExpr(tokens:TokenStream) : Expression = {
    var expr : Expression = null
    //println(s"@@parseFunCallExpr (tokens=${tokens})")
    if (tokens.peek(classOf[ID])) {
      val id = tokens.consume(classOf[ID])
      expr = Identifier(id.value)
    }
    else if (tokens.peek(classOf[ATOM])) {
      expr = parseAtom(tokens)
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

  def funcCallEndToken(tokens:TokenStream) : Boolean = {
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

  def parseAtomicExpr(tokens:TokenStream) : Expression = {
    var expr: Expression = null
    if (tokens.peek(LPAREN)) {
      tokens.consume(LPAREN)
      expr = ForwardPipeFuncCallExpression.parse(tokens)
      if (tokens.peek(COMMA)) {
        var tupleElem = expr
        var tupleElements = List.empty[Expression]
        tupleElements = tupleElem :: tupleElements
        while (tokens.peek(COMMA)) {
          tokens.consume(COMMA)
          tupleElem = ForwardPipeFuncCallExpression.parse(tokens)
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
      expr = parseRangeExpr(tokens)
    }
    else if (tokens.peek(LCURLY)) {
      expr = parseDictionaryExpr(tokens)
    }
    else if (tokens.peek(HASHLCURLY)) {
      expr = parseSetExpr(tokens)
    }
    else {
      expr = parseLiteral(tokens)
    }


    if (expr == null) {
      println(s"!!! expr == null, tokens = $tokens")
      throw UnexpectedTokenClassException()
    }
    expr
  }

  def parseAtom(tokens:TokenStream) : Expression = {
    val atom = tokens.consume(classOf[ATOM])
    Atom(atom.value)
  }

  def parseLiteral(tokens:TokenStream) : Expression = {
    if (tokens.peek(TRUE)) {
      tokens.consume(TRUE)
      BoolLiteral(true)
    }
    else if (tokens.peek(FALSE)) {
      tokens.consume(FALSE)
      BoolLiteral(false)
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

  def parsePartialOper(tokens:TokenStream) : Expression = {
    tokens.consume(LPAREN)
    val parOp = tokens.consume(classOf[OPER])
    var listOfArgs: List[Expression] = List.empty[Expression]
    while (!tokens.peek(RPAREN)) {
      val expr = LogicalExpression.parse(tokens)
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

  def parseRangeExpr(tokens:TokenStream) : Expression = {
    tokens.consume(LBRACKET)
    if (tokens.peek(RBRACKET)) {
      tokens.consume(RBRACKET)
      return EmptyListExpresion()
    }
    var expr = LogicalExpression.parse(tokens)
    if (tokens.peek(COMMA)) {
      var list = List.empty[Expression]
      while(tokens.peek(COMMA)) {
        tokens.consume(COMMA)
        list = expr :: list
        expr = LogicalExpression.parse(tokens)
      }
      list = expr :: list
      expr = ListExpression(list.reverse, None)
    }
    if (tokens.peek(DOTDOT)) {
      tokens.consume(DOTDOT)
      expr = parseEndRange(tokens, expr, include=true)
    }
    if (tokens.peek(DOTDOTLESS)) {
      tokens.consume(DOTDOTLESS)
      expr = parseEndRange(tokens, expr, include = false)
    }
    if (tokens.peek(DOTDOTDOT)) {
      tokens.consume(DOTDOTDOT)
      expr = InfiniteRangeExpression(expr)
    }
    else if (tokens.peek(GUARD)) {
      tokens.consume(GUARD)
      var listOfGuards = List.empty[ListGuard]
      var guard = parseListGuard(tokens)
      listOfGuards = guard :: listOfGuards
      while (tokens.peek(COMMA)) {
        tokens.consume(COMMA)
        guard = parseListGuard(tokens)
        listOfGuards = guard :: listOfGuards
      }
      expr = ListExpression(List(expr), Some(listOfGuards.reverse))
    }
    tokens.consume(RBRACKET)
    if (!expr.isInstanceOf[ValidRangeExpression])
      expr = ListExpression(List(expr), None)
    expr
  }

  private[this] def parseEndRange(tokens: TokenStream, expr: Expression, include: Boolean) : Expression = {
    expr match {
      case expression: ListExpression if expression.expressions.size == 2 =>
        val lexpr = expression.expressions
        val rangeInit = lexpr.head
        val rangeIncrement = SubstractExpression(lexpr.tail.head, lexpr.head)
        if (include)
          RangeWithIncrementExpression(rangeInit, rangeIncrement, LogicalExpression.parse(tokens))
        else
          RangeWithIncrementExpressionUntil(rangeInit, rangeIncrement, LogicalExpression.parse(tokens))
      case rangeInit =>
        if (include)
          RangeExpression(rangeInit, LogicalExpression.parse(tokens))
        else
          RangeExpressionUntil(rangeInit, LogicalExpression.parse(tokens))
    }
  }

  def parseListGuard(tokens:TokenStream) : ListGuard = {
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
      val expr = ForwardPipeFuncCallExpression.parse(tokens)
      ListGuardDeclTupled(listOfIds.reverse, expr)
    }
    else if (tokens.peek(classOf[ID]) && tokens.peek(2, BACK_ARROW)) {
      val id = tokens.consume(classOf[ID])
      tokens.consume(BACK_ARROW)
      val expr = ForwardPipeFuncCallExpression.parse(tokens)
      ListGuardDecl(id.value, expr)
    } else {
      ListGuardExpr(ForwardPipeFuncCallExpression.parse(tokens))
    }
  }

  def parseSetExpr(tokens:TokenStream): Expression = {
    tokens.consume(HASHLCURLY)
    var listOfValues = List.empty[Expression]
    val value = ParseExpr.parse(tokens)
    listOfValues = value :: listOfValues
    while (tokens.peek(COMMA)) {
      tokens.consume(COMMA)
      tokens.consumeOptionals(NL)
      val value = ParseExpr.parse(tokens)
      listOfValues = value :: listOfValues
    }
    tokens.consume(RCURLY)
    SetExpression(listOfValues.reverse)
  }

  def parseDictionaryExpr(tokens:TokenStream) : Expression = {
    tokens.consume(LCURLY)
    var listOfPairs = List.empty[(Expression, Expression)]
    val key = parseKeyExpr(tokens)
    val value = ParseExpr.parse(tokens)
    listOfPairs = (key, value) :: listOfPairs
    while (tokens.peek(COMMA)) {
      tokens.consume(COMMA)
      tokens.consumeOptionals(NL)
      val key = parseKeyExpr(tokens)
      val value = ParseExpr.parse(tokens)
      listOfPairs = (key, value) :: listOfPairs
    }
    tokens.consume(RCURLY)
    DictionaryExpression(listOfPairs.reverse)
  }

  def parseKeyExpr(tokens:TokenStream) : Expression = {
    if (tokens.peek(classOf[ATOM]))
      parseAtom(tokens)
    else
      parseLiteral(tokens)
  }
}
