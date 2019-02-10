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


      case LetDeclExpr(decls: List[LetVariable], Some(expression)) =>
        strBuf ++= "(let ["
        strBuf ++= decls.map(d => s"${d.id} ${toClojure(d.value)}").mkString(",\n")
        strBuf ++= " ]\n"
        strBuf ++= s" ${toClojure(expression)})\n"

      case LetDeclExpr(decls: List[LetVariable], None) =>
        for (decl <- decls) {
          strBuf ++= s"(def ${decl.id} ${toClojure(decl.value)})\n"
        }

      case AddExpression(left, right) =>
        strBuf ++= s"(+ ${toClojure(left)} ${toClojure(right)})"

      case SubstractExpression(left, right) =>
        strBuf ++= s"(- ${toClojure(left)} ${toClojure(right)})"

      case MultiplyExpression(left, right) =>
        strBuf ++= s"(* ${toClojure(left)} ${toClojure(right)})"

      case MultiplyBigExpression(left, right) =>
        strBuf ++= s"(*' ${toClojure(left)} ${toClojure(right)})"

      case DivideExpression(left, right) =>
        strBuf ++= s"(/ ${toClojure(left)} ${toClojure(right)})"

      case Identifier(id) =>
        strBuf ++= id

      case IntLiteral(i) =>
        strBuf ++= i.toString

      case StringLiteral(str) =>
        strBuf ++= str

      case RangeExpression(ini, end) =>
        strBuf ++= s"(range ${toClojure(ini)} ${toClojure(end)})"

      case ListExpression(listOfExpr, Some(guards)) =>
        strBuf ++= s"(for [${guards.map(toClojureListGuard).mkString(" ")}]"
        if (listOfExpr.size == 1) {
          strBuf ++= toClojure(listOfExpr.head)
        }
        strBuf ++= ")\n"

      case FunctionCallExpression(func, args) =>
        strBuf ++=
          strBuf ++= s"(${toClojure(func)}"
        for (arg <- args) {
          strBuf ++= s" ${toClojure(arg)}"
        }
        strBuf ++= ")"

      case EqualsExpr(left, right) =>
        strBuf ++= s"(= ${toClojure(left)} ${toClojure(right)})"

      case GreaterThanExpr(left, right) =>
        strBuf ++= s"(> ${toClojure(left)} ${toClojure(right)})"

      case GreaterOrEqualThanExpr(left, right) =>
        strBuf ++= s"(> ${toClojure(left)} ${toClojure(right)})"

      case LessThanExpr(left, right) =>
        strBuf ++= s"(< ${toClojure(left)} ${toClojure(right)})"

      case LessOrEqualThanExpr(left, right) =>
        strBuf ++= s"(<= ${toClojure(left)} ${toClojure(right)})"

      case LambdaExpression(args, expr) =>
        strBuf ++= s"(fn [${args.map(toClojureLambdaArg).mkString(" ")}] ${toClojure(expr)})"

      case PartialAdd(args) =>
        if (args.isEmpty) strBuf ++= "+" else strBuf ++= s"(+ ${args.map(toClojure).mkString(" ")})"

      case PartialSub(args) =>
        if (args.isEmpty) strBuf ++= "-'" else strBuf ++= s"(- ${args.map(toClojure).mkString(" ")})"

      case PartialMul(args) =>
        if (args.isEmpty) strBuf ++= "*'" else strBuf ++= s"(* ${args.map(toClojure).mkString(" ")})"

      case PartialDiv(args) =>
        if (args.isEmpty) strBuf ++= "/'" else strBuf ++= s"(/ ${args.map(toClojure).mkString(" ")})"

      case PartialMod(args) =>
        if (args.isEmpty) strBuf ++= "%'" else strBuf ++= s"(% ${args.map(toClojure).mkString(" ")})"

      case PartialEQ(args) =>
        if (args.isEmpty) strBuf ++= "=" else strBuf ++= s"(= ${args.map(toClojure).mkString(" ")})"

      case PartialNE(args) =>
        if (args.isEmpty) strBuf ++= "not=" else strBuf ++= s"(not= ${args.map(toClojure).mkString(" ")})"

      case PartialLT(args) =>
        if (args.isEmpty) strBuf ++= "<" else strBuf ++= s"(< ${args.map(toClojure).mkString(" ")})"

      case PartialLE(args) =>
        if (args.isEmpty) strBuf ++= "<=" else strBuf ++= s"(<= ${args.map(toClojure).mkString(" ")})"

      case PartialGT(args) =>
        if (args.isEmpty) strBuf ++= ">" else strBuf ++= s"(> ${args.map(toClojure).mkString(" ")})"

      case PartialGE(args) =>
        if (args.isEmpty) strBuf ++= ">=" else strBuf ++= s"(>= ${args.map(toClojure).mkString(" ")})"

      case PartialCons(args) =>
        if (args.isEmpty) strBuf ++= "cons" else strBuf ++= s"(cons ${args.map(toClojure).mkString(" ")})"

      case PartialConcat(args) =>
        if (args.isEmpty) strBuf ++= "concat" else strBuf ++= s"(concat ${args.map(toClojure).mkString(" ")})"

      case FunctionCallWithDollarExpression(Identifier(id), args) =>
        strBuf ++= s"(${id} ${args.map(toClojure).mkString(" ")})\n"


      case ForwardPipeFuncCallExpression(args) =>
        strBuf ++= s"(->> ${args.map(toClojure).mkString(" ")})\n"

      case ForwardPipeFirstArgFuncCallExpression(args) =>
        strBuf ++= s"(-> ${args.map(toClojure).mkString(" ")})\n"

      case SimpleDefDecl(id, args, BodyGuardsExpresion(guards), None) =>
        strBuf ++= s"(defn $id [${args.map(toClojureDefArg).mkString(" ")}]\n"
        strBuf ++= s"  (cond\n${guards.map(toClojureDefBodyGuardExpr).mkString("\n")}"
        strBuf ++= "))\n\n"


      case SimpleDefDecl(id, args, body, None) =>
        strBuf ++= s"(defn $id [${args.map(toClojureDefArg).mkString(" ")}] ${toClojure(body)})\n"

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

      case InfiniteRangeExpression(init) =>
        strBuf ++= s"(-range-to-inf ${toClojure(init)})"

      case _ =>
        strBuf ++= node.toString
    }
    strBuf.toString()
  }

  def toClojureListGuard(guard: ListGuard): String = {
    guard match {
      case ListGuardDecl(id, value) => s"$id ${toClojure(value)}"
      case _ => ???
    }
  }

  def toClojureDefBodyGuardExpr(guard: DefBodyGuardExpr): String = {
    guard match {
      case DefBodyGuardExpression(comp, expr) => s"\t${toClojure(comp)} ${toClojure(expr)}"
      case DefBodyGuardOtherwiseExpression(expr) => s"\t:else ${toClojure(expr)}"
      case _ => ???
    }
  }

  def toClojureLambdaArg(lambdaArg: LambdaArg): String = {
    lambdaArg match {
      case LambdaSimpleArg(id) => id
      case _ => ???
    }
  }

  def toClojureDefArg(defArg: DefArg): String = {
    defArg match {
      case DefArg(Identifier(id)) => id
      case _ => ???
    }
  }


}
