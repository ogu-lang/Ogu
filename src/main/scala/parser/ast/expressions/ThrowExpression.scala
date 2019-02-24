package parser.ast.expressions

import lexer.{NL, THROW, TokenStream}
import parser.ast.module.Module.parseConstructorExpr
import parser.{ConstructorExpression, Expression}

case class ThrowExpression(ctor: ConstructorExpression) extends Expression

object ThrowExpression extends ExpressionParser {

  override def parse(tokens: TokenStream): Expression = {
    tokens.consume(THROW)
    val ctor = parseConstructorExpr(tokens)
    tokens.consumeOptionals(NL)
    ThrowExpression(ctor)
  }

}
