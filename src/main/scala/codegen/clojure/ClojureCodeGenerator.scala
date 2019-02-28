package codegen.clojure

import parser.ast.module._



        /**
        strBuf ++= ModuleGenCode
        strBuf ++= s"(ns $name )\n\n"
        for (node <- decls) {
          strBuf ++= toClojure(node)
        }

      case Module(name, Some(imports), decls) =>
        strBuf ++= s"(ns $name ${toClojureImportClauses(imports)})\n\n"
        for (node <- decls) {
          strBuf ++= toClojure(node)
        }


      case RecordDecl(name, args) =>
        strBuf ++= s"(defrecord $name [${args.mkString(" ")}])\n"



      case ReifyExpression(name, methods) =>
        strBuf ++= s"(reify $name\n"
        for (method <- methods) {
          val s = toClojure(method.definition).replaceFirst("\\(defn\\s+", "\t(")
          strBuf ++= s
        }
        strBuf ++= ")\n"

      case AdtDecl(name, adts) =>
        strBuf ++= s"(defprotocol $name)\n"
        for (adt <- adts) {
          strBuf ++=  s"(deftype ${adt.name} [${adt.args.mkString(" ")}] $name)\n"
        }

      case DispatchDecl(id, dispatcher) =>
        strBuf ++= s"(defmulti $id "
        dispatcher match {
          case ClassDispatcher => strBuf ++= "class)\n"
          case ExpressionDispatcher(expr) => strBuf ++= s"${toClojure(expr)})\n"
        }

      case BindDeclExpression(decls, expression) =>
        strBuf ++= s"(binding ["
        strBuf ++= decls.asInstanceOf[List[LetVariable]].map(d => s"${toClojureLetId(d.id)} ${toClojure(d.value)}").mkString(" ")
        strBuf ++= "]\n"
        strBuf ++= s"\t${toClojure(expression)})\n"








      case ComposeExpressionForward(args) =>
        strBuf ++= s"(comp ${args.reverse.map(toClojure).mkString(" ")})"

      case ComposeExpressionBackward(args) =>
        strBuf ++= s"(comp ${args.map(toClojure).mkString(" ")})"



      case BoolLiteral(value) =>
        strBuf ++= value.toString

        strBuf ++= d.toString

      case StringLiteral(str) =>
        strBuf ++= str

      case FStringLiteral(str) =>
        strBuf ++= s"(fmt $str)"

      case DateTimeLiteral(date) =>
        strBuf ++= "#inst  \"" + s"$date" + "\""

      case Atom(value) =>
        strBuf ++= value

      case RegexpLiteral(re) =>
        strBuf ++= "#\"" + re + "\""

      case MatchesExpression(expr, re) =>
        strBuf ++= s"(some? (re-matches ${toClojure(re)} ${toClojure(expr)}))"

      case ReMatchExpr(expr, re) =>
        strBuf ++= s"(re-matches ${toClojure(re)} ${toClojure(expr)})"

      case NoMatchExpr(expr, re) =>
        strBuf ++= s"(nil? (re-matches ${toClojure(re)} ${toClojure(expr)}))"

      case ArrayAccessExpression(array, index) =>
        strBuf ++= s"(aget ${toClojure(array)} ${toClojure(index)})"



      case SetExpression(elements) =>
        strBuf ++= s"#{${elements.map(toClojure).mkString(" ")}}"










      case RecurExpression(args) =>
        strBuf ++= s"(recur ${args.map(toClojure).mkString(" ")})"


      case NewCallExpression(cls, args) if args.isEmpty =>
        strBuf ++= s"($cls.)"








      case MultiMethod(_, id, matches, args, BodyGuardsExpresion(guards), None) =>
        strBuf ++= s"(defmethod $id ${matches.map(toClojureDefMatchArg).mkString(" ")} "
        strBuf ++= s"[${args.map(toClojureDefArg).mkString(" ")}]\n"
        strBuf ++= s"  (cond\n${guards.map(toClojureDefBodyGuardExpr).mkString("\n")}"
        strBuf ++= "))\n\n"

      case MultiMethod(_, id, matches, args, body, None) =>
        strBuf ++= s"(defmethod $id ${matches.map(toClojureDefMatchArg).mkString(" ")} "
        strBuf ++= s"[${args.map(toClojureDefArg).mkString(" ")}]\n\t${toClojure(body)})\n\n"








      case LazyExpression(expr) =>
        strBuf ++= s"(lazy-seq ${toClojure(expr)})"



      case TryExpression(body, catches, finExpr) =>
        strBuf ++= s"(try ${toClojure(body)}\n"
        strBuf ++= s"\t${catches.map(toClojure).mkString("\n\t")}"
        if (finExpr.isDefined) {
          strBuf ++= s"\t(finally ${toClojure(finExpr.get)})\n"
        }
        strBuf ++= ")\n"

      case CatchExpression(id, ex, body) =>
        strBuf ++= s"(catch $ex ${id.getOrElse("_")} ${toClojure(body)})"

      case ThrowExpression(ctor) =>
        strBuf ++= s"(throw ${toClojure(ctor)})"



      case _ =>
        strBuf ++= node.toString
    }
    strBuf.toString()
  }







  def toClojureDefMatchArg(defArg: DefArg): String = {
    defArg match {
      case DefArg(ConstructorExpression(cls, _)) => s"$cls"
      case DefArg(RecordConstructorExpression(cls, _)) => s"$cls"
      case d => toClojureDefArg(d)
    }
  }









  def toClojureImportClauses(importClauses: List[ImportClause]): String = {
    val strBuf = new StringBuilder()
    val (cljImp, jvmImp) = importClauses.span {
      case CljImport(_) => true
      case FromCljRequire(_, _) => true
      case _ => false
    }
    if (cljImp.nonEmpty) {
      strBuf ++= s"(:require [${cljImp.map(toClojureImportClause).mkString(" ")}]) "
    }
    if (jvmImp.nonEmpty) {
      strBuf ++= s"(:import ${jvmImp.map(toClojureJvmImportClause).mkString(" ")}) "
    }
    strBuf.toString
  }

  def toClojureImportClause(importClause: ImportClause) : String = {
    importClause match {
      case CljImport(name) => name.map(toClojureImportAlias).mkString(" ")
      case FromCljRequire(from, names) =>
        val renames = names.filter(p => p.alias.nonEmpty)
        if (renames.isEmpty) {
          s"$from :refer [${names.map(n => n.name).mkString(" ")}] "
        }
        else {
          s"$from :refer [${names.map(n => n.name)} :rename ${renames.map(toClojureImporteRename).mkString(", ")}] "
        }
      case _ => ""
    }
  }

  def toClojureJvmImportClause(importClause: ImportClause) : String = {
    importClause match {
      case JvmImport(name) => name.map(toClojureImportAlias).mkString(" ")
      case FromJvmRequire(from, names) =>
        s"($from ${names.map(n => n.name).mkString(" ")}) "
      case _ => ""
    }
  }

  def toClojureImportAlias(importAlias: ImportAlias) : String = {
    importAlias match {
      case ImportAlias(name, None) => name
      case ImportAlias(name, Some(alias)) => s"$name :as $alias"
    }
  }

  def toClojureImporteRename(importAlias: ImportAlias) : String = {
    importAlias match {
      case ImportAlias(_, None) => ""
      case ImportAlias(name, Some(alias)) => s"$name $alias"
    }
  }


}
*/