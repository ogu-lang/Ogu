package codegen.clojure.expressions

import codegen.clojure.expressions.DeclsExprGen._
import codegen.{CodeGenerator, Translator}
import codegen.clojure.expressions.ExpressionsGen._
import parser.ast.expressions.{ArrayAccessExpression, Identifier}
import parser.ast.expressions.control._

object ControlGen {


  implicit object ControlExpressionTranslator extends Translator[ControlExpression] {

    override def mkString(node: ControlExpression): String = {
      node match {

        case ForExpression(variables, body) =>
          s"(doall (for [${variables.map(CodeGenerator.buildString(_)).mkString("\n")}] \n${CodeGenerator.buildString(body)}))"

        case IfExpression(comp, thenPart, Nil, elsePart) =>
          s"(if ${CodeGenerator.buildString(comp)}\n\t${CodeGenerator.buildString(thenPart)}\n\t${CodeGenerator.buildString(elsePart)})"

        case IfExpression(comp, thenPart, ep :: tail, elsePart) =>
          s"(if ${CodeGenerator.buildString(comp)}\n ${CodeGenerator.buildString(thenPart)}\n " +
            s"(${mkString(IfExpression(ep.comp, ep.body, tail, elsePart))}))"


        case WhileExpression(comp, body) =>
          s"(while ${CodeGenerator.buildString(comp)} ${CodeGenerator.buildString(body)})"


        case SimpleAssignExpression(ArrayAccessExpression(array, index), value) =>
          s"(aset ${CodeGenerator.buildString(array)} ${CodeGenerator.buildString(index)} ${CodeGenerator.buildString(value)})"

        case SimpleAssignExpression(Identifier(variable), value) =>
          if (VarDeclExpressionTranslator.isVariable(variable)) {
            s"(var-set $variable ${CodeGenerator.buildString(value)})"
          }
          else {
            s"(alter-var-root (var $variable) (constantly ${CodeGenerator.buildString(value)}))"
          }

      }
    }
  }
}
