package parser.ast.types

import lexer._
import parser.ast.module.Module
import parser.{ClassMethodDecl, LangNode}
import parser.ast.module.Module.{parseDef, parseListOfIds}

case class ClassDecl(inner: Boolean, name: String, args: Option[List[String]], traits: List[TraitDef]) extends LangNode

object ClassDecl {

  def parse(inner: Boolean, tokens:TokenStream): LangNode = {
    tokens.consume(CLASS)
    val name = tokens.consume(classOf[TID]).value
    val argsOpt = if (!tokens.peek(LPAREN)) None else {
      tokens.consume(LPAREN)
      val args = parseListOfIds(tokens)
      tokens.consume(RPAREN)
      Some(args)
    }
    tokens.consume(NL)
    var traits = TraitDef.parse(tokens)

    ClassDecl(inner, name, argsOpt, traits)
  }
}
