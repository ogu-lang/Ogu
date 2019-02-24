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
          case FOR => ForExpression.parse(tokens)
          case IF => IfExpression.parse(tokens)
          case LOOP => LoopExpression.parse(tokens)
          case RECUR => RecurExpression.parse(tokens)
          case REIFY => ReifyExpression.parse(tokens)
          case REPEAT => RepeatExpresion.parse(tokens)
          case SET => SimpleAssignExpression.parse(tokens)
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
