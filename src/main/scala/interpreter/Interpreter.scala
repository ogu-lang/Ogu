package interpreter

import clojure.java.api.Clojure
import codegen.{Translator, _}
import codegen.clojure.module.ModuleGen._
import parser.ast.module.Module

import scala.io.Source


object Interpreter {


  def load(ast:Module): AnyRef = {
    val clojureStr = toClojure(ast)
    println(clojureStr)

    val loadStr = Clojure.`var`("clojure.core", "load-string")
    val require = Clojure.`var`("clojure.core", "require")
    require.invoke(Clojure.read("clojure.set"))
    loadStr.invoke(readOguRuntime())
    val result = loadStr.invoke(clojureStr)
    println(s"@@RESULT = $result")
    result
  }

  def toClojure(node: Module)(implicit translator: Translator[Module]): String = {
    CodeGenerator.mkString(node)
  }

  def readOguRuntime() : String = {
    val classLoader = this.getClass.getClassLoader
    Source.fromInputStream(classLoader.getResourceAsStream("ogu/core.clj")).mkString
  }

  def banner(msg: String) : Unit = {
    println(com.github.lalyos.jfiglet.FigletFont.convertOneLine(msg))
  }
}
