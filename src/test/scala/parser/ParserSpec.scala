package parser

import lexer._
import org.scalatest.{FlatSpec, Matchers}
import parser.ast.expressions.arithmetic.AddExpression
import parser.ast.expressions.literals.IntLiteral
import parser.ast.expressions.{Identifier, LambdaSimpleArg, LambdaTupleArg, TopLevelExpression}
import parser.ast.expressions.functions.{ForwardPipeFuncCallExpression, LambdaExpression}
import parser.ast.module.Module

import scala.util.{Failure, Success, Try}

class ParserSpec extends FlatSpec with Matchers {

  case class ParserException(code:String) extends Throwable

  private[this] def parseExpr(code:String) = {
    val lexer = new Lexer()
    val lexResult = lexer.scanString(code)
    lexResult match {
      case Failure(exception) => Failure(exception)
      case Success(tokens) =>
        val parser = new Parser("test.ogu", tokens, None)
        val result = Try(parser.parse())
        result match {
          case Success(Module(_, _, List(TopLevelExpression(expr)))) => Success(expr)
          case _ => Failure(ParserException(code))
        }
    }
  }

  "A parser" should "parse simple arithmetic expressions" in {
    parseExpr("1 + 1") should be(Success(AddExpression(List(IntLiteral(1), IntLiteral(1)))))
    parseExpr("1 adasjldasj 1") should be(Failure(ParserException("1 adasjldasj 1")))
  }

  "A parser" should "parse simple Lambda Expressions" in {
    parseExpr("\\a -> a + 1") should
      be (Success(LambdaExpression(List(LambdaSimpleArg("a")), AddExpression(List(Identifier("a"), IntLiteral(1))))))

    parseExpr("\\(a, b) -> a + b") should
      be (Success(LambdaExpression(List(LambdaTupleArg(List("a", "b"))), AddExpression(List(Identifier("a"), Identifier("b"))))))
  }

  "A parser" should "parse simple forward pipe" in {
    parseExpr("a |> b |> c") should be (Success(
      ForwardPipeFuncCallExpression(List(Identifier("a"), Identifier("b"), Identifier("c")))))
  }
}
