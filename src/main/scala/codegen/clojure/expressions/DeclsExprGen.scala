package codegen.clojure.expressions

import codegen.{CodeGenerator, Translator}
import codegen.clojure.decls.DeclGen._
import codegen.clojure.expressions.ExpressionsGen._
import parser.ast.decls.BodyGuardsExpresion
import parser.ast.expressions.vars._

object DeclsExprGen {

  implicit object LetIdTranslator extends Translator[LetId] {

    override def mkString(node: LetId): String = {
      node match {
        case LetSimpleId(id) => id
        case LetTupledId(ids) => s"[${ids.map(mkString).mkString(" ")}]"
      }
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
      }
    }
  }

  implicit object BodyGuardsExpresionTranslator extends Translator[BodyGuardsExpresion] {

    override def mkString(node: BodyGuardsExpresion): String = {
          s"(cond\n ${node.guards.map(g => CodeGenerator.buildString(g)).mkString("\n")})"
    }

  }
}

