package parser.ast.expressions
import lexer._
import parser.ast.module.Module._
import parser.{Expression, InvalidExpression, InvalidNodeException}

object ControlExpressionParser extends ExpressionParser {

  override def parse(tokens: TokenStream): Expression = {
    tokens.nextToken() match {
      case None => throw InvalidExpression()
      case Some(token) =>
        token match {
          case COND => CondExpression.parse(tokens)
          case FOR => parseForExpr(tokens)
          case IF => parseIfExpr(tokens)
          case LOOP => parseLoopExpr(tokens)
          case RECUR => parseRecurExpr(tokens)
          case REIFY => parseReifyExpr(tokens)
          case REPEAT => parseRepeatExpr(tokens)
          case SET => parseAssignExpr(tokens)
          case THROW => parseThrowExpr(tokens)
          case TRY => parseTryExpr(tokens)
          case UNTIL => parseWhileExpr(tokens)
          case WHEN => parseWhenExpr(tokens)
          case WHILE => parseWhileExpr(tokens)
          case _ => throw InvalidNodeException(tokens.nextToken())
        }
    }
  }

}
