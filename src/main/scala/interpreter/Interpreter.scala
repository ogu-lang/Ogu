package interpreter

import java.awt._
import javax.swing._

import clojure.java.api.Clojure
import codegen.{Translator, _}
import codegen.clojure.module.ModuleGen._
import parser.ast.module.Module
import scala.io.Source


object Interpreter {

  val color = Color.BLACK
  val panel = new JDesktopPane()


  def load(ast:Module): AnyRef = {
    val clojureStr = toClojure(ast)
    println(clojureStr)
    val loadStr = Clojure.`var`("clojure.core", "load-string")
    val require = Clojure.`var`("clojure.core", "require")
    val imports = Clojure.`var`("clojure.core", "import")
    //val r = require.invoke(Clojure.read("clojure.set"))
    //println(s"@@@r = ${r}")
    //val r0 = require.invoke(Clojure.read("java.awt"))
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
