package parser.ast.types

import lexer._
import parser.ast.decls.TraitMethodDecl
import parser.ast.LangNode

case class TraitDecl(inner: Boolean, name: String, decls: List[TraitMethodDecl]) extends LangNode

object TraitDecl {

  def parse(inner: Boolean, tokens:TokenStream): TraitDecl = {
    tokens.consume(TRAIT)
    val id = tokens.consume(classOf[TID]).value
    tokens.consumeOptionals(NL)
    val decls =
      if (!tokens.peek(INDENT)) List.empty
      else {
        tokens.consume(INDENT)
        val methods = TraitMethodDecl.parseMethods(tokens)
        tokens.consume(DEDENT)
        methods
      }
    TraitDecl(inner, id, decls)
  }

}
