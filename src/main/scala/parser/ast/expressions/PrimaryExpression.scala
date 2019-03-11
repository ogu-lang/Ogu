package parser.ast.expressions
import lexer._
import parser.ast.expressions.arithmetic.PartialOperExpression
import parser.ast.expressions.control.LazyExpression
import parser.ast.expressions.functions.FunctionCallExpression
import parser.ast.expressions.literals.AtomicExpression
import parser.ast.expressions.types.{ConstructorExpression, NewCallExpression}

object PrimaryExpression extends ExpressionParser {

  override def parse(tokens: TokenStream): Expression = {
    tokens.nextSymbol() match {
      case LPAREN if tokens.peek(2, classOf[OPER]) => PartialOperExpression.parse(tokens)
      case LPAREN | LBRACKET | LCURLY | HASHLCURLY => AtomicExpression.parse(tokens)
      case LAZY => LazyExpression.parse(tokens)
      case NEW => NewCallExpression.parse(tokens)
      case lit if lit.isInstanceOf[LITERAL] => AtomicExpression.parse(tokens)
      case tid if tid.isInstanceOf[TID] => ConstructorExpression.parse(tokens)
      case _ => FunctionCallExpression.parse(tokens)
    }
  }

}
