package codegen.clojure.types

import codegen.clojure.decls.DeclGen._
import codegen.{CodeGenerator, Translator}
import parser.ast.types._

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
      mkTypeString(typeName="deftype", node.name, node.args.getOrElse(Nil), node.traits)
    }
  }

  implicit object RecordDeclTranslator extends Translator[RecordDecl] {
    override def mkString(node: RecordDecl): String = {
     mkTypeString(typeName="defrecord", node.name, node.args, node.traits)
    }
  }

  private[this] def mkTypeString(typeName: String, name: String, args:List[String], traits: List[TraitDef]) : String = {
    s"($typeName $name [${args.mkString(" ")}]\n" +
      (if (traits.isEmpty) "" else s"\t${traits.map(CodeGenerator.buildString(_)).mkString("\n\t")}") + ")\n\n"
  }

  implicit object AdtDeclTranslator extends Translator[AdtDecl] {
    override def mkString(node: AdtDecl): String = {
      s"(defprotocol ${node.name})\n" +
      node.defs.map { adt =>
         s"(deftype ${adt.name} [${adt.args.mkString(" ")}] ${node.name})"
      }.mkString("\n")
    }
  }




}
