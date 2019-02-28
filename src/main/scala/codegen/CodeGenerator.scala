package codegen

import codegen.clojure.module.ModuleGen._
import parser.ast.module.Module

object CodeGenerator {

  def mkString(node: Module): String = buildString(node)


  def buildString[A](node: A)(implicit translator: Translator[A]): String = {
    translator.mkString(node)
  }

}

