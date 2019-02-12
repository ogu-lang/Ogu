package codegen

import interpreter.Interpreter.toClojure
import parser._


class ClojureCodeGenerator(node: LangNode) extends CodeGenerator {

  def mkString(): String = genCode(node)

  def genCode(node: LangNode): String = {
    val strBuf = new StringBuilder()
    node match {
      case Module(name, decls) =>
        strBuf ++= s"(ns $name )\n\n"
        for (node <- decls) {
          strBuf ++= toClojure(node)
        }


      case LetDeclExpr(decls: List[LetVariable], Some(expression)) =>
        strBuf ++= "(let ["
        strBuf ++= decls.map(d => s"${d.id} ${toClojure(d.value)}").mkString(" ")
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

      case ModExpression(left, right) =>
        strBuf ++= s"(mod ${toClojure(left)} ${toClojure(right)})"

      case Identifier(id) =>
        val pos = id.lastIndexOf('.')
        if (pos <= 1) {
          strBuf ++= id
        } else {
          val sb = new StringBuilder(id)
          sb.replace(pos, pos + 1, "/")
          strBuf ++= sb.toString()
        }

      case BoolLiteral(value) =>
        strBuf ++= value.toString

      case IntLiteral(i) =>
        strBuf ++= i.toString

      case LongLiteral(i) =>
        strBuf ++= i.toString

      case StringLiteral(str) =>
        strBuf ++= str

      case ArrayAccessExpression(array, index) =>
        strBuf ++= s"(aget ${toClojure(array)} ${toClojure(index)})"

      case RangeExpression(ini, end) =>
        strBuf ++= s"(range ${toClojure(ini)} (inc ${toClojure(end)}))"

      case RangeExpressionUntil(ini, end) =>
        strBuf ++= s"(range ${toClojure(ini)} ${toClojure(end)})"

      case RangeWithIncrementExpression(ini,inc, end) =>
        strBuf ++= s"(range ${toClojure(ini)} (inc ${toClojure(end)}) ${toClojure(inc)})"

      case RangeWithIncrementExpressionUntil(ini,inc, end) =>
        strBuf ++= s"(range ${toClojure(ini)} ${toClojure(end)} ${toClojure(inc)})"

      case EmptyListExpresion() =>
        strBuf ++= "[]"

      case ListExpression(listOfExpr, Some(guards)) =>
        strBuf ++= s"(for [${guards.map(toClojureListGuard).mkString(" ")}]"
        if (listOfExpr.size == 1) {
          strBuf ++= toClojure(listOfExpr.head)
        } else {
          ???
        }
        strBuf ++= ")\n"

      case BlockExpression(expressions) =>
        if (expressions.size == 1) {
          strBuf ++= toClojure(expressions.head)
        } else {
          strBuf ++= s"(do ${expressions.map(toClojure).mkString("\n")})"
        }

      case ForExpression(variables, body) =>
        strBuf ++= s"(for [${variables.map(toClojureForVarDeclIn).mkString("\n")}] \n${toClojure(body)})"

      case LoopExpression(variables, None, body) =>
        strBuf ++= s"(loop [${variables.map(toClojureLoopVar).mkString(" ")}]\n ${toClojure(body)})"

      case LoopExpression(variables, Some(guard), body) =>
        strBuf ++= s"(loop [${variables.map(toClojureLoopVar).mkString(" ")}]\n" +
          s"   (${toClojure(guard)} ${toClojure(body)}))"

      case WhileGuardExpr(comp) =>
        strBuf ++= s"when ${toClojure(comp)}"

      case UntilGuardExpr(comp) =>
        strBuf ++= s"when-not ${toClojure(comp)}"

      case RepeatExpr(Some(newValues)) =>
        strBuf ++= s"(recur ${newValues.map(toClojureNewVarValue).mkString(" ")})"

      case WhenExpression(comp, body) =>
        strBuf ++= s"(when ${toClojure(comp)}\n ${toClojure(body)})"

      case IfExpression(comp, thenPart, elifPart, elsePart) =>
        if (elifPart.nonEmpty) {
          ???
        } else {
          strBuf ++= s"(if ${toClojure(comp)}\n   ${toClojure(thenPart)}\n    ${toClojure(elsePart)})"
        }

      case RecurExpr(args) =>
        strBuf ++= s"(recur ${args.map(toClojure).mkString(" ")})"

      case FunctionCallExpression(func, args) =>
        strBuf ++= s"(${toClojure(func)} ${args.map(toClojure).mkString(" ")})"

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

      case ConsExpression(left, right) =>
        strBuf ++= s"(cons ${toClojure(left)} ${toClojure(right)})"

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
        strBuf ++= s"($id ${args.map(toClojure).mkString(" ")})\n"

      case ForwardPipeFuncCallExpression(args) =>
        strBuf ++= s"(->> ${args.map(toClojure).mkString(" ")})"

      case ForwardPipeFirstArgFuncCallExpression(args) =>
        strBuf ++= s"(-> ${args.map(toClojure).mkString(" ")})"

      case BackwardPipeFuncCallExpression(args) =>
        strBuf ++= s"(->> ${args.map(toClojure).mkString(" ")})"

      case SimpleAssignExpr(ArrayAccessExpression(array, index), value) =>
        strBuf ++= s"(aset ${toClojure(array)} ${toClojure(index)} ${toClojure(value)})"

      case SimpleDefDecl(id, args, BodyGuardsExpresion(guards), None) =>
        strBuf ++= s"(defn $id [${args.map(toClojureDefArg).mkString(" ")}]\n"
        strBuf ++= s"  (cond\n${guards.map(toClojureDefBodyGuardExpr).mkString("\n")}"
        strBuf ++= "))\n\n"
        if (args.isEmpty) {
          strBuf ++= s"(def $id ($id))\n"
        }

      case SimpleDefDecl(id, args, body, None) =>
        strBuf ++= s"(defn $id [${args.map(toClojureDefArg).mkString(" ")}]\n ${toClojure(body)})\n\n"
        if (args.isEmpty) {
          strBuf ++= s"(def $id ($id))\n"
        }

      case SimpleDefDecl(id, args, body, Some(whereBlock)) =>
        strBuf ++= s"(defn $id [${args.map(toClojureDefArg).mkString(" ")}]\n" +
                   s"  ${toClojure(whereBlock)}\n    ${toClojure(body)})\n\n"
        if (args.isEmpty) {
          strBuf ++= s"(def $id ($id))\n"
        }

      case WhereBlock(defs) =>
        strBuf ++= defs.map(toCloureWhereDef).mkString("\n")

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
          val args: List[String] = md.args
          for (decl <- md.decls) {
            var andList = List.empty[String]
            var argDecls = decl.args
            var namedArgs = args
            while (argDecls.nonEmpty) {
              val arg = argDecls.head
              arg match {
                case DefArg(Identifier(id)) if args.contains(id) =>
                // nothing
                case DefArg(exp: Expression) =>
                  val nameArg = namedArgs.head
                  andList = s"  (= $nameArg ${toClojure(exp)}) " :: andList

              }
              argDecls = argDecls.tail
              namedArgs = namedArgs.tail
            }
            if (andList.isEmpty) {
              strBuf ++= s"  :else  ${toClojure(decl.body)}"
            }
            else if (andList.length == 1) {
              strBuf ++= s"${andList.mkString(" ")} ${toClojure(decl.body)}\n"
            }
            else {
              strBuf ++= s"  (and ${andList.mkString(" ")}) ${toClojure(decl.body)}\n"
            }
          }
          strBuf ++= "))\n\n"
        }

      case LazyExpression(expr) =>
        strBuf ++= s"(lazy-seq ${toClojure(expr)})"

      case InfiniteRangeExpression(init) =>
        strBuf ++= s"(-range-to-inf ${toClojure(init)})"

      case _ =>
        strBuf ++= node.toString
    }
    strBuf.toString()
  }

  def toClojureLoopVar(variable: LoopDeclVariable): String = {
    variable match {
      case LoopVarDecl(id, initialValue) => s"$id ${toClojure(initialValue)}"
      case _ => ???
    }
  }

  def toClojureForVarDeclIn(variable: ForVarDeclIn): String = {
   variable match {
     case ForVarDeclIn(id, initialValue) => s"$id ${toClojure(initialValue)}"
     case _ => ???
   }
  }

  def toClojureNewVarValue(variable: RepeatNewVarValue): String = {
    toClojure(variable.value)
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

  def toCloureWhereDef(whereDef: WhereDef): String = {
    whereDef match {
      case WhereDefSimple(id, None, body) => s"(def $id ${toClojure(body)})"
      case WhereDefSimple(id, Some(args), body) =>
        s"(def $id (fn [${args.map(toClojure).mkString(" ")}] ${toClojure(body)}))"
      case _ => ???
    }
  }


}
