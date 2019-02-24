package parser.ast.functions

import lexer.{DEF, NL, TokenStream}
import parser.DefDecl
import parser.ast.module.Module

import scala.annotation.tailrec

case class ClassMethodDecl(definition: DefDecl)

object ClassMethodDecl {

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
