package parser.ast.decls

import lexer._
import parser.ast.LangNode

case class WhereBlock(whereDefs: List[WhereDef]) extends LangNode

object WhereBlock {

  def parse(tokens:TokenStream): Option[WhereBlock] = {
    tokens.nextToken() match {
      case None => None
      case Some(token) =>
        token match {
          case WHERE => Some(parseUnindented(tokens))

          case NL if tokens.peek(2, INDENT) =>
            tokens.consume(NL)
            Some(parseWhereBlock(tokens))

          case _ =>
            None
        }
    }
  }

  def parseUnindented(tokens:TokenStream): WhereBlock = {
    tokens.consume(WHERE)
    val iniList =
      if (!tokens.peek(NL)) {
        val wdef = WhereDef.parse(tokens)
        tokens.consumeOptionals(NL)
        List(wdef)
      } else {
        tokens.consume(NL)
        Nil
      }
    WhereBlock(if (!tokens.peek(INDENT)) iniList
      else {
        tokens.consume(INDENT)
        val l = consumeWhereDefs(tokens, iniList)
        tokens.consume(DEDENT)
        l
      })
  }

  private[this] def consumeWhereDefs(tokens: TokenStream, whereDefs: List[WhereDef]) : List[WhereDef] = {
    if (tokens.peek(DEDENT)) {
      whereDefs.reverse
    }
    else {
      val whereDef = WhereDef.parse(tokens)
      tokens.consumeOptionals(NL)
      consumeWhereDefs(tokens, whereDef :: whereDefs)
    }
  }

  private[this] def parseWhereBlock(tokens:TokenStream): WhereBlock = {
    tokens.consume(INDENT)
    val whereBlock = parseUnindented(tokens)
    tokens.consume(DEDENT)
    whereBlock
  }

}
