package codegen.clojure.expressions

import codegen.clojure.decls.DeclGen._
import codegen.clojure.expressions.DeclsExprGen._
import codegen.clojure.expressions.ExpressionsGen._
import codegen.clojure.expressions.FunctionsGen._
import codegen.{CodeGenerator, Translator}
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

  implicit object CatchExpressionTranslatro extends Translator[CatchExpression] {

    override def mkString(node: CatchExpression): String = {
      s"(catch ${node.ex} ${node.id.getOrElse("_")} ${CodeGenerator.buildString(node.body)})"
    }
  }

  implicit object ThrowExpressionTranslator extends Translator[ThrowExpression] {

    override def mkString(node: ThrowExpression): String = {
      s"(throw ${CallExpressionExpressionTranslator.mkString(node.ctor)})"
    }

  }

  implicit object ControlExpressionTranslator extends Translator[ControlExpression] {

    override def mkString(node: ControlExpression): String = {
      node match {
        case CondExpression(guards) =>
          s"(cond\n\t${guards.map(toClojureCondGuard).mkString("\n\t")})"

        case ForExpression(variables, body) =>
          s"(doseq [${variables.map(CodeGenerator.buildString(_)).mkString("\n")}] \n${CodeGenerator.buildString(body)})"

        case IfExpression(comp, thenPart, Nil, elsePart) =>
          s"(if ${CodeGenerator.buildString(comp)}\n\t${CodeGenerator.buildString(thenPart)}\n\t${CodeGenerator.buildString(elsePart)})"

        case IfExpression(comp, thenPart, ep :: tail, elsePart) =>
          s"(if ${CodeGenerator.buildString(comp)}\n ${CodeGenerator.buildString(thenPart)}\n " +
            s"${mkString(IfExpression(ep.comp, ep.body, tail, elsePart))})"

        case LazyExpression(expr) =>
          s"(lazy-seq ${CodeGenerator.buildString(expr)})"

        case LoopExpression(variables, None, body) =>
          s"(loop [${variables.map(toClojureLoopVar).mkString(" ")}]\n ${CodeGenerator.buildString(body)})"

        case LoopExpression(variables, Some(guard), body) =>
          s"(loop [${variables.map(toClojureLoopVar).mkString(" ")}]\n" +
            s"   (${CodeGenerator.buildString(guard)} ${CodeGenerator.buildString(body)}))"

        case ProxyExpression(name, interfaces, methods) =>
          val strBuf = new StringBuilder()
          strBuf ++= s"(proxy [$name ${interfaces.mkString(" ")}] []\n"
          for (method <- methods) {
            val s = CodeGenerator.buildString(method.definition).replaceFirst("\\(defn\\s+", "\t(")
            strBuf ++= s
          }
          strBuf ++= ")\n"
          strBuf.mkString

        case RecurExpression(args) =>
           s"(recur ${args.map(CodeGenerator.buildString(_)).mkString(" ")})"

        case ReifyExpression(name, methods) =>
          val strBuf = new StringBuilder()
          strBuf ++= s"(reify $name\n"
          for (method <- methods) {
            val s = CodeGenerator.buildString(method.definition).replaceFirst("\\(defn\\s+", "\t(")
            strBuf ++= s
          }
          strBuf ++= ")\n"
          strBuf.mkString

        case RepeatExpresion(Some(newValues)) =>
          s"(let [${newValues.map(toClojureNewVarValue).mkString(" ")}]" +
            s"(recur ${newValues.map(nv => nv.variable).mkString(" ")}))"

        case SimpleAssignExpression(ArrayAccessExpression(array, index), value) =>
          s"(aset ${CodeGenerator.buildString(array)} ${CodeGenerator.buildString(index)} ${CodeGenerator.buildString(value)})"

        case SimpleAssignExpression(Identifier(variable), value) =>
          if (VarDeclExpressionTranslator.isVariable(variable)) {
            s"(var-set $variable ${CodeGenerator.buildString(value)})"
          }
          else {
            s"(alter-var-root (var $variable) (constantly ${CodeGenerator.buildString(value)}))"
          }

        case SyncExpression(body) =>
          s"(dosync ${CodeGenerator.buildString(body)})"

        case TryExpression(body, catches, finExpr) =>
          val strBuf = new StringBuilder()
          strBuf ++= s"(try ${CodeGenerator.buildString(body)}\n"
          strBuf ++= s"\t${catches.map(CodeGenerator.buildString(_)).mkString("\n\t")}"
          if (finExpr.isDefined) {
            strBuf ++= s"\t(finally ${CodeGenerator.buildString(finExpr.get)})\n"
          }
          strBuf ++= ")\n"
          strBuf.mkString

        case WhenExpression(comp, body) =>
          s"(when ${CodeGenerator.buildString(comp)}\n\t${CodeGenerator.buildString(body)})"

        case WhileExpression(comp, body) =>
          s"(while ${CodeGenerator.buildString(comp)} ${CodeGenerator.buildString(body)})"


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

    def toClojureCondGuard(condGuard: CondGuard) : String = {
      condGuard match {
        case CondGuard(Some(comp), value) =>
          s"${CodeGenerator.buildString(comp)} ${CodeGenerator.buildString(value)}"
        case CondGuard(None, value) =>
          s":else ${CodeGenerator.buildString(condGuard.value)}"

      }
    }


  }
}
