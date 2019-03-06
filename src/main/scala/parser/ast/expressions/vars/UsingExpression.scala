package parser.ast.expressions.vars

import exceptions.InvalidExpression
import lexer.{TokenStream, USING}
import parser.ast.expressions.{Expression, ExpressionParser}

case class UsingExpression(decl: LetVariable, inExpr: Expression) extends Expression

object UsingExpression extends ExpressionParser {

  override def parse(tokens: TokenStream): Expression = {
    tokens.consume(USING)
    val letVar = VariableParser.parseLetVar(tokens)
    VariableParser.parseInBodyOptExpr(tokens) match {
      case None => throw InvalidExpression(tokens.nextToken())
      case Some(body) => UsingExpression(letVar, body)
    }
  }

}
