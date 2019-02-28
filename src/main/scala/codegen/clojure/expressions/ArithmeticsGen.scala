package codegen.clojure.expressions

import codegen.{CodeGenerator, Translator}
import codegen.clojure.expressions.ExpressionsGen._
import parser.ast.expressions.ArithmeticExpression
import parser.ast.expressions.arithmetic.{AddExpression, MultiplyExpression, PartialAdd}

object ArithmeticsGen {

  implicit object ArithmeticExpressionTranslator extends Translator[ArithmeticExpression] {

    override def mkString(node: ArithmeticExpression): String = {
      node match {
        case AddExpression(args) =>
          s"(+ ${args.map(arg => CodeGenerator.buildString(arg)).mkString(" ")})"
        case MultiplyExpression(args) =>
          s"(* ${args.map(arg => CodeGenerator.buildString(arg)).mkString(" ")})"

        case _ => s"AE(${node.getClass})"
      }
    }

  }

}
