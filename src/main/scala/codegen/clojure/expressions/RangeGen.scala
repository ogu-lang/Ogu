package codegen.clojure.expressions

import codegen.clojure.expressions.ExpressionsGen._
import codegen.{CodeGenerator, Translator}
import parser.ast.expressions.types._

object RangeGen {

  implicit object ListGuardTranslator extends Translator[ListGuard] {
    override def mkString(node: ListGuard): String = {
      node match {
        case ListGuardDecl(id, value) => s"$id ${CodeGenerator.buildString(value)}"
        case ListGuardExpr(expr) => s":when ${CodeGenerator.buildString(expr)}"
        case ListGuardDeclTupled(ids, value) => s"[${ids.mkString(" ")}] ${CodeGenerator.buildString(value)}"
        case _ => ""
      }
    }
  }

  implicit object ValidRangeExpressionTranslator extends Translator[ValidRangeExpression] {
    override def mkString(node: ValidRangeExpression): String = {
      node match {
        case ListExpression(listOfExpr, None) =>
          s"[${listOfExpr.map(e => CodeGenerator.buildString(e)).mkString(" ")}]"

        case ListExpression(listOfExpr, Some(guards)) =>
          val body = listOfExpr match {
            case Nil => ""
            case List(expr) => CodeGenerator.buildString(expr)
            case _ => s"(do ${listOfExpr.map(e => CodeGenerator.buildString(e)).mkString("\n")})"
          }
          s"(for [${guards.map(g => CodeGenerator.buildString(g)).mkString(" ")}] $body)"

        case RangeExpression(ini, end) =>
          s"(range ${CodeGenerator.buildString(ini)} (inc ${CodeGenerator.buildString(end)}))"

        case RangeExpressionUntil(ini, end) =>
          s"(range ${CodeGenerator.buildString(ini)} ${CodeGenerator.buildString(end)})"

        case RangeWithIncrementExpression(ini, inc, end) =>
          s"(range ${CodeGenerator.buildString(ini)} (inc ${CodeGenerator.buildString(end)}) ${CodeGenerator.buildString(inc)})"

        case RangeWithIncrementExpressionUntil(ini, inc, end) =>
          s"(range ${CodeGenerator.buildString(ini)} ${CodeGenerator.buildString(end)} ${CodeGenerator.buildString(inc)})"

        case InfiniteRangeExpression(init) =>
          s"(-range-to-inf ${CodeGenerator.buildString(init)})"

        case InfiniteRangeWithIncrementExpression(init, inc) =>
          s"(-range-to-inf ${CodeGenerator.buildString(init)} ${CodeGenerator.buildString(inc)})"

        case EmptyListExpresion => "[]"

        case _ => ""
      }
    }
  }

}
