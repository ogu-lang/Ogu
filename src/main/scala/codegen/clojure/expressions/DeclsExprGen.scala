package codegen.clojure.expressions

import codegen.clojure.decls.DeclGen._
import codegen.clojure.expressions.ExpressionsGen._
import codegen.{CodeGenerator, Translator}
import parser.ast.decls.BodyGuardsExpresion
import parser.ast.expressions.control.{ForVarDeclIn, ForVarDeclTupledIn, LoopDeclVariable}
import parser.ast.expressions.vars._

object DeclsExprGen {

  implicit object LetIdTranslator extends Translator[LetId] {
    override def mkString(node: LetId): String = {
      node match {
        case LetSimpleId(id) => id
        case LetTupledId(ids) => s"[${ids.map(mkString).mkString(" ")}]"
        case _ => ""
      }
    }
  }

  implicit object UsingExpressionTranslator extends Translator[UsingExpression] {
    override def mkString(node: UsingExpression): String = {
      s"(with-open [${LetIdTranslator.mkString(node.decl.id)} ${CodeGenerator.buildString(node.decl.value)}]" +
        s"\n\t${CodeGenerator.buildString(node.inExpr)})\n"
    }
  }

  implicit object LetDeclExpressionTranslator extends Translator[LetDeclExpression] {
    override def mkString(node: LetDeclExpression): String = {
      node match {
        case LetDeclExpression(decls, Some(expression)) =>
          "(let[" +
            decls.map { d =>
              s"${CodeGenerator.buildString(d.id)} ${CodeGenerator.buildString(d.value)}"
            }.mkString(" ") + "]\n\t" + CodeGenerator.buildString(expression) + ")"
        case LetDeclExpression(decls, None) =>
          decls.map {
            decl => s"(def ${CodeGenerator.buildString(decl.id)} ${CodeGenerator.buildString(decl.value)})"
          }.mkString("\n")
        case _ => ""
      }
    }
  }

  implicit object LoopDeclVariableTranslator extends Translator[LoopDeclVariable] {
    override def mkString(node: LoopDeclVariable): String = {
      node match {
        case ForVarDeclIn(id, initialValue) => s"$id ${CodeGenerator.buildString(initialValue)}"
        case ForVarDeclTupledIn(ids, initialValue) => s"[${ids.mkString(" ")}] ${CodeGenerator.buildString(initialValue)}"
        case _ => ""
      }
    }
  }

  implicit object BindDeclExpressionTranslator extends Translator[BindDeclExpression] {
    override def mkString(node: BindDeclExpression): String = {
        s"(binding [" +
        node.decls.map(d => s"${CodeGenerator.buildString(d.id)} ${CodeGenerator.buildString(d.value)}").mkString(" ") +
        s"]\n\t${CodeGenerator.buildString(node.inExpr)})\n"
    }
  }

  implicit object VarDeclExpressionTranslator extends Translator[VarDeclExpression] {
    override def mkString(node: VarDeclExpression): String = {
      node match {
        case VarDeclExpression(decls, None) =>
          decls.map{d => toClojureOguVariable(d)}.mkString("\n")

        case VarDeclExpression(decls, Some(expression)) =>
          val preamble = "(with-local-vars [" +
          decls.asInstanceOf[List[LetVariable]].map(d => s"${CodeGenerator.buildString(d.id)} ${CodeGenerator.buildString(d.value)}").mkString(" ") +
          "]\n"
          addVariables(decls)
          val body = s"${CodeGenerator.buildString(expression)})\n"
          removeVariables(decls)
          preamble + body
        case _ => ""
      }
    }

    def toClojureOguVariable(variable: LetVariable) : String = {
        s"(-def-ogu-var- ${CodeGenerator.buildString(variable.id)} ${CodeGenerator.buildString(variable.value)})\n"
    }

    private[this] var varDecls : Set[String] = Set.empty[String]

    def isVariable(id: String): Boolean = {
      varDecls.contains(id)
    }

    def addVariables(decls: List[LetVariable]): Unit = {
      for (v <- decls) {
        v match {
          case LetVariable(LetSimpleId(id), _) => varDecls = varDecls + id
          case _=>
        }
      }
    }

    def removeVariables(decls: List[LetVariable]): Unit = {
      for (v <- decls) {
        v match {
          case LetVariable(LetSimpleId(id), _) => varDecls = varDecls - id
          case _ =>
        }
      }
    }

  }

  implicit object BodyGuardsExpresionTranslator extends Translator[BodyGuardsExpresion] {

    override def mkString(node: BodyGuardsExpresion): String = {
          s"(cond\n ${node.guards.map(g => CodeGenerator.buildString(g)).mkString("\n")})"
    }

  }
}

