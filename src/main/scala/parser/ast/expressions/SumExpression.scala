package parser.ast.expressions

import lexer.TokenStream
import parser.ast.module.Module
import parser.{BinaryExpression, Expression}

class SumExpression(override val left: Expression, override val right: Expression) extends BinaryExpression(left, right)


object SumExpression extends ExpressionParser {

  override def parse(tokens: TokenStream): Expression = {
    Module.parseSumExpr(tokens)
  }
}