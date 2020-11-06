package parser.ast.decls

import lexer.{DEF, NL, TokenStream}
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
      val defDecl = DefDecl.parse(false, tokens)
      tokens.consumeOptionals(NL)
      consumeClassMethodDecls(tokens, ClassMethodDecl(defDecl) :: methods)
    }
  }
}
