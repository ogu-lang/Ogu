package interpreter

import backend.Options
import clojure.java.api.Clojure
import codegen._
import codegen.clojure.module.ModuleGen._
import parser.ast.module.Module

import scala.io.Source

object Interpreter {

  private[this] val clojureLoader = Clojure.`var`("clojure.core", "load-string")

  clojureLoader.invoke(readOguRuntime())
  clojureLoader.invoke(readOguTurtle())

  def load(ast:Module, options: Options): AnyRef = {
    val clojureStr = toClojure(ast)
    if (options.print) {
      println(clojureStr)
    }
    val preamble = options.args match {
      case Nil => ""
      case _ => "(alter-var-root  (var ogu.core/**args**) (constantly '(" + options.args.map{a => "\"" + a + "\""}.mkString(" ") + ")))"
    }
    clojureLoader.invoke(preamble)
    val result = clojureLoader.invoke(clojureStr)
    result
  }

  private[this] def toClojure(node: Module)(implicit translator: Translator[Module]): String = {
    CodeGenerator.mkString(node)
  }

  private[this] def readOguRuntime() : String = {
    val classLoader = this.getClass.getClassLoader
    Source.fromInputStream(classLoader.getResourceAsStream("ogu/core.clj")).mkString
  }

  private[this] def readOguTurtle() : String = {
    val classLoader = this.getClass.getClassLoader
    Source.fromInputStream(classLoader.getResourceAsStream("ogu/turtle.clj")).mkString
  }

}
