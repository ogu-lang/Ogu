package codegen.clojure.expressions

import codegen.{CodeGenerator, Translator}
import codegen.clojure.expressions.ExpressionsGen._
import parser.ast.expressions.types._

object RangeGen {

  implicit object ListGuardTranslator extends Translator[ListGuard] {

    override def mkString(node: ListGuard): String = {
      node match {
        case ListGuardDecl(id, value) => s"$id ${CodeGenerator.buildString(value)}"
        case ListGuardExpr(expr) => s":when ${CodeGenerator.buildString(expr)}"
        case ListGuardDeclTupled(ids, value) => s"[${ids.mkString(" ")}] ${CodeGenerator.buildString(value)}"
      }
    }
  }

  implicit object ValidRangeExpressionTranslator extends Translator[ValidRangeExpression] {

    override def mkString(node: ValidRangeExpression): String = {
      node match {
        case ListExpression(listOfExpr, None) =>
          s"[${listOfExpr.map(e => CodeGenerator.buildString(e)).mkString(" ")}]"

        case ListExpression(listOfExpr, Some(guards)) =>
          s"(for [${guards.map(g => CodeGenerator.buildString(g)).mkString(" ")}] " + (if (listOfExpr.size == 1)
            CodeGenerator.buildString(listOfExpr.head)
          else {
            s"(do ${listOfExpr.map(e => CodeGenerator.buildString(e)).mkString("\n")})"
          }) +
            ")"

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
      }

    }
  }

}
