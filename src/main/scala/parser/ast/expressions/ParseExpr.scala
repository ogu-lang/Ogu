package parser.ast.expressions

import lexer._
import parser.InvalidExpression
import parser.ast.expressions.control.ControlExpression
import parser.ast.module.Module._

object ParseExpr extends ExpressionParser {
  def parse(tokens: TokenStream): Expression = {
    tokens.nextToken() match {
      case None => throw InvalidExpression()
      case Some(token) =>
        token match {
          case LET => parseLetExpr(tokens)
          case VAR => parseVarExpr(tokens)
          case BIND => parseBindExpr(tokens)
          case ctl if ctl.isInstanceOf[CONTROL] => ControlExpression.parse(tokens)
          case _ => parseLambdaExpr(tokens)
        }
    }
  }

}
