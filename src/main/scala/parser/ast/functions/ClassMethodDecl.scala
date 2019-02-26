package parser.ast.functions

import lexer.{DEF, NL, TokenStream}
import parser.ast.DefDecl
import parser.ast.module.Module

import scala.annotation.tailrec

case class ClassMethodDecl(definition: DefDecl)

object ClassMethodDecl {

  def parseMethodDecls(tokens: TokenStream): List[ClassMethodDecl] = {
    consumeClassMethodDecls(tokens, Nil)
  }

  @tailrec
  def consumeClassMethodDecls(tokens: TokenStream, methods: List[ClassMethodDecl]) : List[ClassMethodDecl] = {
    if (!tokens.peek(DEF)) {
      methods.reverse
    } else {
      val defDecl = Module.parseDef(false, tokens)
      tokens.consumeOptionals(NL)
      consumeClassMethodDecls(tokens, ClassMethodDecl(defDecl) :: methods)
    }
  }
}
