package parser.ast.expressions.control

import lexer._
import parser.ast.decls.ClassMethodDecl
import parser.ast.expressions.{Expression, ExpressionParser}

case class ProxyExpression(traitName: String, interfaces: List[String], methods: List[ClassMethodDecl]) extends ControlExpression

object ProxyExpression extends ExpressionParser {

  override def parse(tokens: TokenStream): Expression = {
    tokens.consume(PROXY)
    val name = tokens.consume(classOf[TID]).value
    val interfaces = if (!tokens.peek(WITH)) Nil else {
      tokens.consume(WITH)
      parseInterfaces(tokens, List(tokens.consume(classOf[TID]).value))
    }
    tokens.consume(NL)
    tokens.consume(INDENT)
    val methods = ClassMethodDecl.parseMethodDecls(tokens)
    tokens.consume(DEDENT)
    ProxyExpression(name, interfaces, methods)
  }

  private[this] def parseInterfaces(tokens: TokenStream, interfaces: List[String]): List[String] = {
    if (!tokens.peek(COMMA)) {
      interfaces.reverse
    }
    else {
      tokens.consume(COMMA)
      parseInterfaces(tokens, tokens.consume(classOf[TID]).value :: interfaces)
    }
  }
}

