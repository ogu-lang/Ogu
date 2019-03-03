package interpreter

import clojure.java.api.Clojure
import codegen.{Translator, _}
import codegen.clojure.module.ModuleGen._
import parser.ast.module.Module
import scala.io.Source


object Interpreter {

  val clojureLoader = Clojure.`var`("clojure.core", "load-string")

  clojureLoader.invoke(readOguRuntime())
  clojureLoader.invoke(readOguTurtle())

  def load(ast:Module): AnyRef = {
    val clojureStr = toClojure(ast)
    debug(clojureStr)
    val result = clojureLoader.invoke(clojureStr)
    result
  }

  def toClojure(node: Module)(implicit translator: Translator[Module]): String = {
    CodeGenerator.mkString(node)
  }

  def readOguRuntime() : String = {
    val classLoader = this.getClass.getClassLoader
    Source.fromInputStream(classLoader.getResourceAsStream("ogu/core.clj")).mkString
  }

  def readOguTurtle() : String = {
    val classLoader = this.getClass.getClassLoader
    Source.fromInputStream(classLoader.getResourceAsStream("ogu/turtle.clj")).mkString
  }

  def banner(msg: String) : Unit = {
    println(com.github.lalyos.jfiglet.FigletFont.convertOneLine(msg))
  }

  def debug(str: String): Unit = {
    println(str)
  }
}
