package codegen

import interpreter.Interpreter.toClojure
import parser._
import parser.LiteralExpression

class ClojureCodeGenerator(node: LangNode) extends CodeGenerator {

  def mkString(): String = genCode(node)

  def genCode(node: LangNode): String = {
    val strBuf = new StringBuilder()
    node match {
      case Module(name, decls) =>
        strBuf ++= s"(ns ${name} )\n\n"
        for (node <- decls) {
          strBuf ++= toClojure(node)
        }
      case AddExpression(left, right) =>
        strBuf ++= s"(+ ${toClojure(left)} ${toClojure(right)})"

      case MultiplyExpression(left, right) =>
        strBuf ++= s"(* ${toClojure(left)} ${toClojure(right)})"

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
        strBuf ++= ")"

      case DeclIdVar(id) =>
        strBuf ++= id

      case LetDecl(decls) =>
        strBuf ++= decls.map(decl => s"(def ${toClojure(decl._1)} ${toClojure(decl._2)})").mkString("\n")

      case VarDecl(decls) =>
        for (decl <- decls) {
          strBuf ++= s"(-def-ogu-var- ${toClojure(decl._1)} ${toClojure(decl._2)})\n"
        }

      case FunctionCallWithDollarExpression(Identifier(id), args) =>
        strBuf ++= s"(${id} ${args.map(toClojure).mkString(" ")})\n"


      case FunctionCallExpression(Identifier(id), args) =>
        strBuf ++= s"(${id} ${args.map(toClojure).mkString} )"

      case md: MultiDefDecl =>
        if (!md.patternMatching()) {
          strBuf ++= s"\n(defn ${md.id}\n"
          for (decl <- md.decls) {
            strBuf ++= "([" + decl.args.map(arg => s"${toClojure(arg.expression)}").mkString(" ") + "] "
            // TODO WHERE
            if (decl.whereBlock.isEmpty)
              strBuf ++= s"${toClojure(decl.body)})\n"
            else {
              ???
            }
          }
          strBuf ++= ")\n\n"
        }
        else {
          strBuf ++= s"\n(defn ${md.id} [" + md.args.mkString(" ") + "]\n"
          strBuf ++= " (cond\n"
          val args = md.args
          for (decl <- md.decls) {
            var andList = List.empty[String]
            var argDecls = decl.args
            var namedArgs = args
            var pos = 0
            while (!argDecls.isEmpty) {
              val arg = argDecls.head
              arg match {
                case DefArg(Identifier(id)) if (args.contains(id)) =>
                // nothing
                case DefArg(exp: Expression) =>
                  val nameArg = namedArgs.head
                  andList = s"  (= ${nameArg} ${toClojure(exp)}) " :: andList

              }
              argDecls = argDecls.tail
              namedArgs = namedArgs.tail
            }
            if (andList.isEmpty) {
              strBuf ++= s"  :else  ${toClojure(decl.body)}\n"
            }
            else if (andList.length == 1) {
              strBuf ++= s"${andList.mkString(" ")} ${toClojure(decl.body)}\n"
            }
            else {
              strBuf ++= s"  (and ${andList.mkString(" ")}) ${toClojure(decl.body)}\n"
            }
          }
          strBuf ++= "\n)\n)\n"
        }

      case _ =>
        strBuf ++= node.toString
    }
    strBuf.toString()
  }

}
