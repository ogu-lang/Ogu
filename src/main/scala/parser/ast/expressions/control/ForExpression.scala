package parser.ast.expressions.control

import lexer._
import parser.ast._
import parser.ast.expressions.functions.ForwardPipeFuncCallExpression
import parser.ast.expressions.{Expression, ExpressionParser, parsePipedOrBodyExpression}

import scala.annotation.tailrec

case class ForExpression(variables: List[LoopDeclVariable], body: Expression) extends ControlExpression

object ForExpression extends ExpressionParser {
  override def parse(tokens: TokenStream): Expression = {
    tokens.consume(FOR)
    val forDecls = parseForDecls(tokens)
    val forBody = parseForBody(tokens)
    ForExpression(forDecls, forBody)
  }

  private[this] def parseForDecls(tokens:TokenStream) : List[LoopDeclVariable] = {
    parseForDecls(tokens, List(parseForVarDecl(tokens)))
  }

  @tailrec
  private[this] def parseForDecls(tokens: TokenStream, decls: List[LoopDeclVariable]) : List[LoopDeclVariable] = {
    if (!tokens.peek(COMMA)) {
      decls.reverse
    }
    else {
      tokens.consume(COMMA)
      parseForDecls(tokens, parseForVarDecl(tokens) :: decls)
    }
  }

  private[this] def parseForVarDecl(tokens:TokenStream) : LoopDeclVariable = {
    if (!tokens.peek(LPAREN)) {
      val id = tokens.consume(classOf[ID])
      tokens.consume(IN)
      ForVarDeclIn(id.value, ForwardPipeFuncCallExpression.parse(tokens))
    }
    else {
      tokens.consume(LPAREN)
      val ids = consumeListOfIdsSepByComma(tokens)
      tokens.consume(RPAREN)
      tokens.consume(IN)
      ForVarDeclTupledIn(ids, ForwardPipeFuncCallExpression.parse(tokens))
    }
  }

  private[this] def parseForBody(tokens:TokenStream) : Expression = {
    tokens.consume(DO)
    parsePipedOrBodyExpression(tokens)
  }
}
