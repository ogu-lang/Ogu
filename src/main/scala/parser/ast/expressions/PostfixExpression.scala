package parser.ast.expressions
import lexer.{ARROBA, TokenStream}
import parser.{ArrayAccessExpression, Expression}
import parser.ast.module.Module.parsePrimExpr

object PostfixExpression extends ExpressionParser {

  override def parse(tokens: TokenStream): Expression = {
    var expr = parsePrimExpr(tokens)
    if (tokens.peek(ARROBA)) {
      val array = expr
      tokens.consume(ARROBA)
      val arg = LogicalExpression.parse(tokens)
      expr = ArrayAccessExpression(array, arg)
    }
    expr
  }
}
