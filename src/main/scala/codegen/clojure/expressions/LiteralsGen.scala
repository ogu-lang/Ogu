package codegen.clojure.expressions

import codegen.{CodeGenerator, Translator}
import codegen.clojure.expressions.ExpressionsGen._
import parser.ast.expressions.TupleExpression
import parser.ast.expressions.literals.{DoubleLiteral, IntLiteral, LiteralExpression}

object LiteralsGen {

  implicit object LiteralExpressionTranslator extends Translator[LiteralExpression] {

    override def mkString(node: LiteralExpression): String = {
      node match {
        case DoubleLiteral(d) => d.toString
        case IntLiteral(i) => i.toString
      }
    }
  }

  implicit object TupleExpressionTranslator extends Translator[TupleExpression] {

    override def mkString(node: TupleExpression): String = {
      node match {
        case TupleExpression(exprs) =>
          s"[${exprs.map(e => CodeGenerator.buildString(e)).mkString(" ")}]"
      }

    }

  }

}
