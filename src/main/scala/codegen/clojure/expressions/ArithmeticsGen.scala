package codegen.clojure.expressions

import codegen.{CodeGenerator, Translator}
import codegen.clojure.expressions.ExpressionsGen._
import parser.ast.expressions.ArithmeticExpression
import parser.ast.expressions.arithmetic._

object ArithmeticsGen {

  implicit object ArithmeticExpressionTranslator extends Translator[ArithmeticExpression] {

    override def mkString(node: ArithmeticExpression): String = {
      node match {

        case AddBigExpression(args) =>
          s"(+' ${args.map(arg => CodeGenerator.buildString(arg)).mkString(" ")})"

        case AddExpression(args) =>
          s"(+ ${args.map(arg => CodeGenerator.buildString(arg)).mkString(" ")})"

        case DivideExpression(args) =>
          s"(/ ${args.map(a => CodeGenerator.buildString(a)).mkString(" ")})"

        case ModExpression(left, right) =>
          s"(mod ${CodeGenerator.buildString(left)} ${CodeGenerator.buildString(right)})"

        case MultiplyBigExpression(args) =>
          s"(*' ${args.map(arg => CodeGenerator.buildString(arg)).mkString(" ")})"

        case MultiplyExpression(args) =>
          s"(* ${args.map(arg => CodeGenerator.buildString(arg)).mkString(" ")})"

        case PowerExpression(left, right) =>
          s"(pow ${CodeGenerator.buildString(left)} ${CodeGenerator.buildString(right)})"

        case SubstractBigExpression(args) =>
          s"(-' ${args.map(a => CodeGenerator.buildString(a)).mkString(" ")})"

        case SubstractExpression(args) =>
          s"(- ${args.map(a => CodeGenerator.buildString(a)).mkString(" ")})"

        case _ => s"AE(${node.getClass})"
      }
    }

  }

}
