package parser.ast.expressions.literals

import lexer._
import parser.InvalidExpression
import parser.ast.expressions.{Expression, ExpressionParser}

trait LiteralExpression extends Expression

object LiteralExpression extends ExpressionParser {

  override def parse(tokens: TokenStream): Expression = {
    tokens.nextToken() match {
      case None => throw InvalidExpression()
      case Some(token) =>
        token match {
          case TRUE =>
            tokens.consume(TRUE)
            BoolLiteral(true)
          case FALSE =>
            tokens.consume(FALSE)
            BoolLiteral(false)
          case i if i.isInstanceOf[INT_LITERAL] =>
            IntLiteral(tokens.consume(classOf[INT_LITERAL]).value)
          case l if l.isInstanceOf[LONG_LITERAL] =>
            LongLiteral(tokens.consume(classOf[LONG_LITERAL]).value)
          case bi if bi.isInstanceOf[BIGINT_LITERAL] =>
            BigIntLiteral(tokens.consume(classOf[BIGINT_LITERAL]).value)
          case dl if dl.isInstanceOf[DOUBLE_LITERAL] =>
            DoubleLiteral(tokens.consume(classOf[DOUBLE_LITERAL]).value)
          case sl if sl.isInstanceOf[STRING_LITERAL] =>
            StringLiteral(tokens.consume(classOf[STRING_LITERAL]).value)
          case idtl if idtl.isInstanceOf[ISODATETIME_LITERAL] =>
            DateTimeLiteral(tokens.consume(classOf[ISODATETIME_LITERAL]).value)
          case ch if ch.isInstanceOf[CHAR_LITERAL] =>
            CharLiteral(tokens.consume(classOf[CHAR_LITERAL]).chr)
          case rel if rel.isInstanceOf[REGEXP_LITERAL] =>
            RegexpLiteral(tokens.consume(classOf[REGEXP_LITERAL]).re)
          case fsl if fsl.isInstanceOf[FSTRING_LITERAL] =>
            FStringLiteral(tokens.consume(classOf[FSTRING_LITERAL]).value)
          case _ =>
            throw InvalidExpression()
        }
    }
  }

}