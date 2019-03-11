package parser.ast.expressions.literals

import lexer._
import parser.ast.expressions._
import parser.ast.expressions.types.{DictionaryExpression, ListExpression, SetExpression, TupleExpression}

object AtomicExpression extends ExpressionParser {

  override def parse(tokens: TokenStream): Expression = {
    tokens.nextSymbol() match {
      case LPAREN =>
        TupleExpression.parse(tokens)
      case LBRACKET =>
        ListExpression.parse(tokens)
      case LCURLY =>
        DictionaryExpression.parse(tokens)
      case HASHLCURLY =>
        SetExpression.parse(tokens)
      case _ =>
        LiteralExpression.parse(tokens)
    }
  }


}
