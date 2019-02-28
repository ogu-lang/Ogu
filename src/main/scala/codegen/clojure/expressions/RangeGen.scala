package codegen.clojure.expressions

import codegen.{CodeGenerator, Translator}
import codegen.clojure.expressions.ExpressionsGen._
import parser.ast.expressions.types._

object RangeGen {

  implicit object ValidRangeExpressionTranslator extends Translator[ValidRangeExpression] {

    override def mkString(node: ValidRangeExpression): String = {
      node match {
        case RangeExpression(ini, end) =>
          s"(range ${CodeGenerator.buildString(ini)} (inc ${CodeGenerator.buildString(end)}))"

        case RangeExpressionUntil(ini, end) =>
          s"(range ${CodeGenerator.buildString(ini)} ${CodeGenerator.buildString(end)})"

        case RangeWithIncrementExpression(ini,inc, end) =>
          s"(range ${CodeGenerator.buildString(ini)} (inc ${CodeGenerator.buildString(end)}) ${CodeGenerator.buildString(inc)})"

        case RangeWithIncrementExpressionUntil(ini,inc, end) =>
          s"(range ${CodeGenerator.buildString(ini)} ${CodeGenerator.buildString(end)} ${CodeGenerator.buildString(inc)})"

        case InfiniteRangeExpression(init) =>
          s"(-range-to-inf ${CodeGenerator.buildString(init)})"

        case InfiniteRangeWithIncrementExpression (init, inc) =>
          s"(-range-to-inf ${CodeGenerator.buildString(init)} ${CodeGenerator.buildString(inc)})"

        case EmptyListExpresion() =>
          "[]"
      }

    }
  }
}
