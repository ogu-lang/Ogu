package parser.ast.expressions.literals

import lexer._
import parser.ast.expressions.{Expression, ExpressionParser}
import parser.InvalidExpression

trait LiteralExpression extends Expression

object LiteralExpression extends ExpressionParser {

  override def parse(tokens: TokenStream): Expression = {
    tokens.nextToken() match {
      case TRUE =>
        tokens.consume(TRUE)
        BoolLiteral(true)
      case FALSE =>
        tokens.consume(FALSE)
        BoolLiteral(false)
      case i if i.isInstanceOf[INT] =>
        IntLiteral(tokens.consume(classOf[INT]).value)
      case l if l.isInstanceOf[LONG] =>
        LongLiteral(tokens.consume(classOf[LONG]).value)
      case bi if bi.isInstanceOf[BIGINT] =>
        BigIntLiteral(tokens.consume(classOf[BIGINT]).value)
      case dl if dl.isInstanceOf[DOUBLE] =>
        DoubleLiteral(tokens.consume(classOf[DOUBLE]).value)
      case sl if sl.isInstanceOf[STRING] =>
        StringLiteral(tokens.consume(classOf[STRING]).value)
      case idtl if idtl.isInstanceOf[ISODATETIME] =>
        DateTimeLiteral(tokens.consume(classOf[ISODATETIME]).value)
      case ch if ch.isInstanceOf[CHAR] =>
        CharLiteral(tokens.consume(classOf[CHAR]).chr)
      case rel if rel.isInstanceOf[REGEXP] =>
        RegexpLiteral(tokens.consume(classOf[REGEXP]).re)
      case fsl if fsl.isInstanceOf[FSTRING] =>
        FStringLiteral(tokens.consume(classOf[FSTRING]).value)
      case _ =>
        println(s"@@ tokens${tokens}")
        throw InvalidExpression(tokens.nextToken())
    }
  }

}