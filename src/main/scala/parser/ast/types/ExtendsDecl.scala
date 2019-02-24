package parser.ast.types

import lexer._
import parser.ast.module.Module
import parser.LangNode
import parser.ast.functions.ClassMethodDecl

case class ExtendsDecl(cls: String, traitClass: String, decls: Option[List[ClassMethodDecl]]) extends LangNode

object ExtendsDecl {

  def parse(inner: Boolean, tokens:TokenStream): ExtendsDecl = {
    tokens.consume(EXTENDS)
    val cls = tokens.consume(classOf[TID]).value
    tokens.consume(WITH)
    val trt = tokens.consume(classOf[TID]).value
    tokens.consumeOptionals(NL)
    val defs = if (!tokens.peek(INDENT)) None else {
      tokens.consume(INDENT)
      val defs = ClassMethodDecl.consumeClassMethodDecls(tokens, List.empty)
      tokens.consume(DEDENT)
      Some(defs)
    }
    ExtendsDecl(cls, trt, defs)
  }

}
