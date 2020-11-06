package codegen.clojure.expressions

import codegen.clojure.expressions.ExpressionsGen._
import codegen.{CodeGenerator, Translator}
import parser.ast.expressions.comparisons._

object ComparativeGen {

  implicit object ComparativeExpressionTranslator extends Translator[ComparativeExpression] {

    override def mkString(node: ComparativeExpression): String = {
      node match {
        case EqualsExpression(args) =>
          s"(= ${args.map(a => CodeGenerator.buildString(a)).mkString(" ")})"

        case NotEqualsExpression(args) =>
          s"(not= ${args.map(a => CodeGenerator.buildString(a)).mkString(" ")})"

        case GreaterThanExpression(args) =>
          s"(> ${args.map(a => CodeGenerator.buildString(a)).mkString(" ")})"

        case GreaterOrEqualThanExpression(args) =>
         s"(>= ${args.map(a => CodeGenerator.buildString(a)).mkString(" ")})"

        case LessThanExpression(args) =>
          s"(< ${args.map(a => CodeGenerator.buildString(a)).mkString(" ")})"

        case LessOrEqualThanExpression(args) =>
          s"(<= ${args.map(a => CodeGenerator.buildString(a)).mkString(" ")})"

        case _ => ""
      }
    }
  }

}
