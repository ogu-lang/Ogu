package codegen.clojure.types

import codegen.clojure.decls.DeclGen._
import codegen.{CodeGenerator, Translator}
import parser.ast.types.{ClassDecl, ExtendsDecl, TraitDecl, TraitDef}

object TypeGen {

  implicit object ExtendsDeclTranslator extends Translator[ExtendsDecl] {

    override def mkString(node: ExtendsDecl): String = {
      val strBuf = new StringBuilder()
        strBuf ++= s"(extend-type ${node.cls} ${node.traitClass}"
        for (method <- node.decls.getOrElse(Nil)) {
          val s = CodeGenerator.buildString(method.definition).replaceFirst("\\(defn\\s+", "\t(")
          strBuf ++= s
        }
        strBuf ++= ")\n\n"
        strBuf.mkString
    }
  }

  implicit object TraitDeclTranslator extends Translator[TraitDecl] {

    override def mkString(node: TraitDecl): String = {
      val strBuf = new StringBuilder()
      strBuf ++= s"(defprotocol ${node.name}\n"
      for (decl <- node.decls) {
        strBuf ++= s"\t(${decl.name} [${decl.args.mkString(" ")}])\n"
      }
      strBuf ++= ")\n\n"
      strBuf.mkString
    }
  }

  implicit object TraitDefTranslator extends Translator[TraitDef] {

    override def mkString(node: TraitDef): String = {
      val strBuf = new StringBuilder()
      strBuf ++= s"${node.traitName}\n"
      for (method <- node.methods) {
        val s = CodeGenerator.buildString(method.definition).replaceFirst("\\(defn\\s+", "\t(")
        strBuf ++= s
      }
      strBuf.mkString
    }
  }

  implicit object ClassDeclTranslator extends Translator[ClassDecl] {

    override def mkString(node: ClassDecl): String = {
      val strBuf = new StringBuilder()
      node match {
        case ClassDecl(_, name, args, traits) =>
          strBuf ++= s"(deftype $name [${args.getOrElse(List.empty[String]).mkString(" ")}]\n"
          if (traits.nonEmpty) {
            strBuf ++= s"\t${traits.map(CodeGenerator.buildString(_)).mkString("\n\t")}"
          }
          strBuf ++= ")\n\n"
          strBuf.mkString

      }
    }
  }
}
