package codegen.clojure.expressions

import codegen.{CodeGenerator, Translator}
import codegen.clojure.expressions.ExpressionsGen._
import parser.ast.expressions.control.{ControlExpression, IfExpression}

object ControlGen {


  implicit object ControlExpressionTranslator extends Translator[ControlExpression] {

    override def mkString(node: ControlExpression): String = {
      node match {
        case IfExpression(comp, thenPart, Nil, elsePart) =>
          s"(if ${CodeGenerator.buildString(comp)}\n\t${CodeGenerator.buildString(thenPart)}\n\t${CodeGenerator.buildString(elsePart)})"

        case IfExpression(comp, thenPart, ep :: tail, elsePart) =>
          s"(if ${CodeGenerator.buildString(comp)}\n ${CodeGenerator.buildString(thenPart)}\n " +
            s"(${mkString(IfExpression(ep.comp, ep.body, tail, elsePart))}))"

      }
    }
  }
}
