package parser.ast.expressions
import lexer._
import parser.ast.module.Module.parseFuncCallExpr
import parser.{Expression, InvalidExpression}

object PrimaryExpression extends ExpressionParser {

  override def parse(tokens: TokenStream): Expression = {
    tokens.nextToken() match {
      case None => throw InvalidExpression()
      case Some(token) =>
        token match {
          case LPAREN if tokens.peek(2, classOf[OPER]) => PartialOperExpression.parse(tokens)
          case LPAREN | LBRACKET | LCURLY | HASHLCURLY => AtomicExpression.parse(tokens)
          case LAZY => LazyExpression.parse(tokens)
          case NEW => NewCallExpression.parse(tokens)
          case lit if lit.isInstanceOf[LITERAL] => AtomicExpression.parse(tokens)
          case tid if tid.isInstanceOf[TID] => ConstructorExpression.parse(tokens)
          case _ => parseFuncCallExpr(tokens)
        }
    }
  }

}
