package parser.ast.expressions
import lexer.{ARROBA, TokenStream}
import parser.{AssignableExpression, Expression}
import parser.ast.module.Module.parsePrimExpr

case class ArrayAccessExpression(array: Expression, index: Expression) extends Expression with AssignableExpression

object PostfixExpression extends ExpressionParser {

  override def parse(tokens: TokenStream): Expression = {
    var expr = parsePrimExpr(tokens)
    if (!tokens.peek(ARROBA)) {
      expr
    } else {
      tokens.consume(ARROBA)
      ArrayAccessExpression(expr, LogicalExpression.parse(tokens))
    }
  }

}
