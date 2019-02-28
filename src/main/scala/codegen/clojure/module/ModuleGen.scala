package codegen.clojure.module

import codegen.{CodeGenerator, Translator}
import parser.ast.LangNode
import parser.ast.expressions.TopLevelExpression
import parser.ast.module.Module
import codegen.clojure.expressions.ExpressionsGen._

import scala.annotation.tailrec

object ModuleGen {

  implicit object ModuleTranslator extends Translator[Module] {

    override def mkString(node: Module): String = {
      s"(ns ${node.name} )\n\n" + genDecls(node.decls, Nil)
    }
  }

  @tailrec
  private[this] def genDecls(nodes: List[LangNode], strs: List[String]): String = {
    if (nodes.isEmpty) {
      strs.reverse.mkString("\n")
    }
    else {
      val s = nodes.head match {
        case e:TopLevelExpression => CodeGenerator.buildString(e)
        case _ => s"**ERROR (${nodes.head.getClass})**"
      }
      genDecls(nodes.tail, s :: strs )
    }
  }

}
