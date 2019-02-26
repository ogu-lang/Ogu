package parser.ast.expressions.control

import lexer._
import parser.ast.expressions._
import parser.{InvalidExpression, InvalidNodeException}

class ControlExpression extends Expression

object ControlExpression extends ExpressionParser {

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
          case THROW => ThrowExpression.parse(tokens)
          case TRY => TryExpression.parse(tokens)
          case UNTIL => UntilExpression.parse(tokens)
          case WHEN => WhenExpression.parse(tokens)
          case WHILE => WhileExpression.parse(tokens)
          case _ => throw InvalidNodeException(tokens.nextToken())
        }
    }
  }

}
