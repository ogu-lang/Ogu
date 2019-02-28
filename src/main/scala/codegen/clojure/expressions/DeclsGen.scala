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
           }.mkString(" ")
       case LetDeclExpression(decls, None) =>
         decls.map{
           decl => s"(def ${CodeGenerator.buildString(decl.id)} ${CodeGenerator.buildString(decl.value)})"
         }.mkString("\n")
     }
    }

  }
}

/**
  *
  * def toClojureLetId(id: LetId) : String = {
  * id match {
  * case LetSimpleId(name) => name
  * case LetTupledId(ids) => s"[${ids.map(toClojureLetId).mkString(" ")}]"
  * }
  * }
  *
  * case
  * strBuf ++= "(let ["
  * strBuf ++=
  * strBuf ++= " ]\n"
  * strBuf ++= s"\t${toClojure(expression)})\n"
  **
  *case LetDeclExpression(decls: List[_], None) =>
  *for (decl <- decls.asInstanceOf[List[LetVariable]]) {
  *strBuf ++= s"(def ${toClojureLetId(decl.id)} ${toClojure(decl.value)})\n"
  * }
  *
  */