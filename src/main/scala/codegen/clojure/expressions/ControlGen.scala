package codegen.clojure.expressions

import codegen.clojure.expressions.DeclsExprGen._
import codegen.{CodeGenerator, Translator}
import codegen.clojure.expressions.ExpressionsGen._
import parser.ast.expressions.{ArrayAccessExpression, Identifier}
import parser.ast.expressions.control._

object ControlGen {

  implicit object LoopGuardTranslator extends Translator[LoopGuard] {

    override def mkString(node: LoopGuard): String = {
      node match {
        case WhileGuardExpr(comp) =>
          s"when ${CodeGenerator.buildString(comp)}"

        case UntilGuardExpr(comp) =>
          s"when-not ${CodeGenerator.buildString(comp)}"
      }
    }
  }

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


        case LoopExpression(variables, None, body) =>
          s"(loop [${variables.map(toClojureLoopVar).mkString(" ")}]\n ${CodeGenerator.buildString(body)})"

        case LoopExpression(variables, Some(guard), body) =>
          s"(loop [${variables.map(toClojureLoopVar).mkString(" ")}]\n" +
            s"   (${CodeGenerator.buildString(guard)} ${CodeGenerator.buildString(body)}))"

        case RepeatExpresion(Some(newValues)) =>
          s"(let [${newValues.map(toClojureNewVarValue).mkString(" ")}]" +
          s"(recur ${newValues.map(nv => nv.variable).mkString(" ")}))"


        case WhenExpression(comp, body) =>
          s"(when ${CodeGenerator.buildString(comp)}\n\t${CodeGenerator.buildString(body)})"


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


    private[this] def toClojureLoopVar(variable: LoopDeclVariable): String = {
      variable match {
        case LoopVarDecl(id, initialValue) => s"$id ${CodeGenerator.buildString(initialValue)}"
      }
    }


    private[this] def toClojureNewVarValue(variable: RepeatNewVarValue): String = {
      s"${variable.variable} ${CodeGenerator.buildString(variable.value)}"
    }


  }
}
