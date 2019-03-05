package parser.ast.expressions.arithmetic

import lexer._
import parser._
import parser.ast.expressions.logical.LogicalExpression
import parser.ast.expressions.{Expression, ExpressionParser}

trait PartialOper extends Expression
case class PartialAdd(args: List[Expression]) extends PartialOper
case class PartialBigAdd(args: List[Expression]) extends PartialOper
case class PartialSub(args: List[Expression]) extends PartialOper
case class PartialBigSub(args: List[Expression]) extends PartialOper
case class PartialMul(args: List[Expression]) extends PartialOper
case class PartialBigMul(args: List[Expression]) extends PartialOper
case class PartialDiv(args: List[Expression]) extends PartialOper
case class PartialMod(args: List[Expression]) extends PartialOper
case class PartialEQ(args: List[Expression]) extends PartialOper
case class PartialNE(args: List[Expression]) extends PartialOper
case class PartialLT(args: List[Expression]) extends PartialOper
case class PartialLE(args: List[Expression]) extends PartialOper
case class PartialGT(args: List[Expression]) extends PartialOper
case class PartialGE(args: List[Expression]) extends PartialOper
case class PartialPow(args: List[Expression]) extends PartialOper
case class PartialCons(args: List[Expression]) extends PartialOper
case class PartialConcat(args: List[Expression]) extends PartialOper

object PartialOperExpression extends ExpressionParser {

  override def parse(tokens: TokenStream): Expression = {
    tokens.consume(LPAREN)
    val parOp = tokens.consume(classOf[OPER])
    val listOfArgs = consumeListOfArgs(tokens, Nil)
    tokens.consume(RPAREN)
    classifyPartialOper(parOp, listOfArgs)
  }

  private[this] def consumeListOfArgs(tokens: TokenStream, args: List[Expression]): List[Expression] = {
    if (tokens.peek(RPAREN)) {
      args.reverse
    }
    else {
      consumeListOfArgs(tokens, LogicalExpression.parse(tokens) :: args)
    }
  }

  private[this] def classifyPartialOper(parOp: OPER, args: List[Expression]) : Expression = {
    parOp match {
      case PLUS => PartialAdd(args)
      case PLUSBIG => PartialBigAdd(args)
      case MINUS => PartialSub(args)
      case MINUSBIG => PartialBigSub(args)
      case MULT => PartialMul(args)
      case MULTBIG => PartialBigMul(args)
      case DIV => PartialDiv(args)
      case MOD => PartialMod(args)
      case EQUALS => PartialEQ(args)
      case NOTEQUALS => PartialNE(args)
      case LT => PartialLT(args)
      case GT => PartialGT(args)
      case LE => PartialLE(args)
      case GE => PartialGE(args)
      case POW => PartialPow(args)
      case CONS => PartialCons(args)
      case PLUSPLUS => PartialConcat(args)
      case _ => throw PartialOperNotSupported(parOp)
    }
  }

}
