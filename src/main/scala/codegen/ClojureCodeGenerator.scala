package codegen

import interpreter.Interpreter.toClojure
import parser._

class ClojureCodeGenerator(node: LangNode) extends CodeGenerator {

    def mkString() : String = genCode(node)

    def genCode(node: LangNode) : String = {
      val strBuf = new StringBuilder()
      node match {
        case Module(name, decls) =>
          strBuf ++= s"(ns ${name} )\n\n"
          for (node <- decls) {
            strBuf ++= toClojure(node)
          }
        case AddExpression(left, right) =>
          strBuf ++= s"(+ ${toClojure(left)} ${toClojure(right)})"
        case Identifier(id) =>
          strBuf ++= id
        case IntLiteral(i) =>
          strBuf ++= i.toString
        case StringLiteral(str) =>
          strBuf ++= str
        case FunctionCallExpression(func, args) =>
          strBuf ++=
            strBuf ++= s"(${toClojure(func)}"
          for (arg <- args) {
            strBuf ++= s" ${toClojure(arg)}"
          }
          strBuf ++= ")\n\n"
        case _ =>
          strBuf ++= node.toString
      }
      strBuf.toString()
    }

}
