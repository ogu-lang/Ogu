package codegen.clojure.expressions

import codegen.{CodeGenerator, Translator}
import codegen.clojure.expressions.ExpressionsGen._
import parser.ast.expressions.vars._

object DeclsGen {

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

}

