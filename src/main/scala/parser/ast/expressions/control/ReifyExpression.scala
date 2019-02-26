package parser.ast.expressions.control

import lexer._
import parser.ast.expressions.{Expression, ExpressionParser}
import parser.ast.functions.ClassMethodDecl

case class ReifyExpression(traitName: String, methods: List[ClassMethodDecl]) extends Expression

object ReifyExpression extends ExpressionParser {

  override def parse(tokens: TokenStream): Expression = {
    tokens.consume(REIFY)
    val name = tokens.consume(classOf[TID]).value
    tokens.consume(NL)
    tokens.consume(INDENT)
    val methods = ClassMethodDecl.parseMethodDecls(tokens)
    tokens.consume(DEDENT)
    ReifyExpression(name, methods)
  }

}
