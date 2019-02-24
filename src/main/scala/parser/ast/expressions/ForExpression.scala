package parser.ast.expressions

import lexer._
import parser._
import parser.ast._
import parser.ast.module.Module

case class ForExpression(variables: List[LoopDeclVariable], body: Expression) extends ControlExpression

object ForExpression extends ExpressionParser {
  override def parse(tokens: TokenStream): Expression = {
    tokens.consume(FOR)
    val forDecls = parseForDecls(tokens)
    val forBody = parseForBody(tokens)
    ForExpression(forDecls, forBody)
  }

  def parseForDecls(tokens:TokenStream) : List[LoopDeclVariable] = parseForDecls(tokens, List(parseForVarDecl(tokens)))

  def parseForDecls(tokens: TokenStream, decls: List[LoopDeclVariable]) : List[LoopDeclVariable] = {
    if (!tokens.peek(COMMA)) {
      decls.reverse
    }
    else {
      tokens.consume(COMMA)
      parseForDecls(tokens, parseForVarDecl(tokens) :: decls)
    }
  }

  def parseForVarDecl(tokens:TokenStream) : LoopDeclVariable = {
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

  def parseForBody(tokens:TokenStream) : Expression = {
    tokens.consume(DO)
    Module.parsePipedOrBodyExpression(tokens)
  }
}
