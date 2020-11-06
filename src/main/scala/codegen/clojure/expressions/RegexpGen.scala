package codegen.clojure.expressions

import codegen.clojure.expressions.ExpressionsGen._
import codegen.{CodeGenerator, Translator}
import parser.ast.expressions.regexp.{MatchesExpression, NoMatchExpr, ReMatchExpr, RegexExpression}

object RegexpGen {

  implicit object RegexExpressionTranslator extends Translator[RegexExpression] {

    override def mkString(node: RegexExpression): String = {
      node match {
        case MatchesExpression(expr, re) =>
          s"(some? (re-matches ${CodeGenerator.buildString(re)} ${CodeGenerator.buildString(expr)}))"

        case ReMatchExpr(expr, re) =>
          s"(re-matches ${CodeGenerator.buildString(re)} ${CodeGenerator.buildString(expr)})"

        case NoMatchExpr(expr, re) =>
          s"(nil? (re-matches ${CodeGenerator.buildString(re)} ${CodeGenerator.buildString(expr)}))"

        case _ => ""
      }
    }
  }


}
