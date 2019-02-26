package parser.ast.decls

import lexer.{DEF, ID, NL, TokenStream}
import parser.ast._

import scala.annotation.tailrec

case class TraitMethodDecl(name: String, args: List[String])

object TraitMethodDecl {

  def parse(tokens:TokenStream): TraitMethodDecl = {
    tokens.consume(DEF)
    val id = tokens.consume(classOf[ID]).value
    var args = consumeListOfIdsSepByBlank(tokens, List.empty)
    tokens.consume(NL)
    TraitMethodDecl(id, args)
  }

  def parseMethods(tokens: TokenStream): List[TraitMethodDecl] = {
    parseMethods(tokens, List.empty)
  }

  @tailrec
  def parseMethods(tokens:TokenStream, methods: List[TraitMethodDecl]): List[TraitMethodDecl] = {
    if (!tokens.peek(DEF)) {
      methods.reverse
    }
    else {
      parseMethods(tokens, parse(tokens) :: methods)
    }
  }

}
