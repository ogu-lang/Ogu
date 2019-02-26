package parser.ast.expressions.control

import lexer.{NL, THROW, TokenStream}
import parser.ast.expressions.types.ConstructorExpression
import parser.ast.expressions.{Expression, ExpressionParser}

case class ThrowExpression(ctor: ConstructorExpression) extends Expression

object ThrowExpression extends ExpressionParser {

  override def parse(tokens: TokenStream): Expression = {
    tokens.consume(THROW)
    val ctor = ConstructorExpression.parse(tokens)
    tokens.consumeOptionals(NL)
    ThrowExpression(ctor.asInstanceOf[ConstructorExpression])
  }

}
