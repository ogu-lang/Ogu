package interpreter

import clojure.java.api.Clojure
import codegen.{Translator, _}
import codegen.clojure.module.ModuleGen._
import parser.ast.module.Module
import scala.io.Source


object Interpreter {

  private[this] val clojureLoader = Clojure.`var`("clojure.core", "load-string")

  clojureLoader.invoke(readOguRuntime())
  clojureLoader.invoke(readOguTurtle())

  def load(ast:Module, args: List[String]): AnyRef = {
    setArgs(args)
    val clojureStr = toClojure(ast)
    debug(clojureStr)
    val result = clojureLoader.invoke(clojureStr)
    result
  }

  private[this] def setArgs(args: List[String]): Unit = {
    Runtime.setArgs(args)
    clojureLoader.invoke(
      """(def ^:dynamic  **args**
        |      (let [args (interpreter.Runtime/getArgs)]
        |    (println "args received are:" args)
        |    args))""".stripMargin)

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


  private[this] def debug(str: String): Unit = {
    //println(str)
  }


}
