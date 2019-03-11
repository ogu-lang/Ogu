package parser.ast.decls

import exceptions.InvalidDef
import lexer._
import parser.ast._
import parser.ast.expressions._

class DefDecl(id: String) extends LangNode

object DefDecl {

  def parse(inner: Boolean, tokens: TokenStream): DefDecl = {
    val line = tokens.currentLine()
    tokens.consume(DEF)
    val defId = tokens.consume(classOf[ID]).value
    val (args, dispatchers) = DefArg.parseDefArgs(tokens)
    tokens.nextSymbol() match {
      case ASSIGN =>
        tokens.consume(ASSIGN)
        buildResult(inner, defId, args, dispatchers, parsePipedOrBodyExpression(tokens), WhereBlock.parse(tokens))

      case NL =>
        tokens.consume(NL)
        val body = DefBodyGuardExpr.parse(tokens)
        body match {
          case BodyGuardsExpresionAndWhere(guards, whereBlock) =>
            buildResult(inner, defId, args, dispatchers, BodyGuardsExpresion(guards), Some(whereBlock))
          case _ =>
            buildResult(inner, defId, args, dispatchers, body, WhereBlock.parse(tokens))
        }

      case _ => throw InvalidDef(line)
    }
  }

  private[this]
  def buildResult(inn: Boolean, id: String, args: List[DefArg], dispatches: List[DefArg],
             body: Expression, where: Option[WhereBlock]): DefDecl = {
    dispatches match {
      case Nil => SimpleDefDecl(inn, id, args, body, where)
      case _ => MultiMethod(inn, id, dispatches, args, body, where)
    }
  }


}
