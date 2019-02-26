package interpreter

import clojure.java.api.Clojure
import codegen.ClojureCodeGenerator
import parser.ast.LangNode

import scala.io.Source

object Interpreter {

  def load(ast:LangNode): AnyRef = {
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

  def toClojure(node: LangNode): String = {
    val codeGenerator = new ClojureCodeGenerator(node)
    codeGenerator.mkString()
  }

  def readOguRuntime() : String = {
    val classLoader = this.getClass.getClassLoader
    Source.fromInputStream(classLoader.getResourceAsStream("ogu/core.clj")).mkString
  }

  def banner(msg: String) : Unit = {
    println(com.github.lalyos.jfiglet.FigletFont.convertOneLine(msg))
  }
}
