package codegen.clojure.module

import codegen.clojure.decls.DeclGen._
import codegen.clojure.expressions.ExpressionsGen._
import codegen.clojure.types.TypeGen._
import codegen.{CodeGenerator, Translator}
import parser.ast.LangNode
import parser.ast.expressions.TopLevelExpression
import parser.ast.module._
import parser.ast.decls.{DispatchDecl, MultiDefDecl, MultiMethod, SimpleDefDecl}
import parser.ast.types._

import scala.annotation.tailrec

object ModuleGen {

  def toClojureImportClauses(importClauses: List[ImportClause]): String = {
    val strBuf = new StringBuilder()
    val (imp, staticImp) = importClauses.span {
      case FromJvmRequireStatic(_,_) => false
      case FromCljRequireStatic(_, _) => false
      case _ => true
    }

    val (cljImp, jvmImp) = imp.span {
      case CljImport(_) => true
      case FromCljRequire(_, _) => true
      case _ => false
    }
    if (cljImp.nonEmpty) {
      strBuf ++= s"(:require ${cljImp.map(toClojureImportClause).mkString(" ")}) "
    }
    if (jvmImp.nonEmpty) {
      strBuf ++= s"(:import ${jvmImp.map(toClojureJvmImportClause).mkString(" ")}) "
    }
    strBuf.toString
  }


  def toClojureImportStaticClauses(importClauses: List[ImportClause]): String = {
    val strBuf = new StringBuilder()
    val staticImp = importClauses.filter {
      case FromJvmRequireStatic(_,_) => true
      case FromCljRequireStatic(_, _) => true
      case _ => false
    }

    if (staticImp.nonEmpty) {
      staticImp.foreach {
        case FromJvmRequireStatic(from, names) =>
          strBuf ++= s"(import-static $from ${names.map(toClojureImportAlias).mkString(" ")})\n"
        case FromCljRequireStatic(from, names) =>
          strBuf ++= s"(import-static $from ${names.map(toClojureImportAlias).mkString(" ")})\n"
      }
    }

    strBuf.toString
  }

  def toClojureImportClause(importClause: ImportClause) : String = {
    importClause match {
      case CljImport(name) => name.map(toClojureImportAlias).mkString(" ")
      case FromCljRequire(from, names) =>
        val renames = names.filter(p => p.alias.nonEmpty)
        if (renames.isEmpty) {
          s"[$from :refer [${names.map(n => n.name).mkString(" ")}]]"
        }
        else {
          s"[$from :refer [${names.map(n => n.name)} :rename ${renames.map(toClojureImporteRename).mkString(", ")}]]"
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


  implicit object ModuleTranslator extends Translator[Module] {

    override def mkString(node: Module): String = {
      s"(ns ${node.name} (:require [ogu.core :refer :all]) ${mkString(node.imports)})\n\n${staticImports(node.imports)}" + genDecls(node.decls, Nil)
    }

    private[this] def mkString(imports: Option[List[ImportClause]]): String = {
      imports match {
        case None => ""
        case Some(l) => toClojureImportClauses(l)
      }
    }

    private[this] def staticImports(maybeClauses: Option[List[ImportClause]]): String = {
      maybeClauses match {
        case None => ""
        case Some(l) => toClojureImportStaticClauses(l)
      }
    }
  }

  @tailrec
  private[this] def genDecls(nodes: List[LangNode], strs: List[String]): String = {
    if (nodes.isEmpty) {
      strs.reverse.mkString("\n")
    }
    else {
      val s = nodes.head match {
        case ad: AdtDecl => CodeGenerator.buildString(ad)
        case cd: ClassDecl => CodeGenerator.buildString(cd)
        case dd: DispatchDecl => CodeGenerator.buildString(dd)
        case ed: ExtendsDecl => CodeGenerator.buildString(ed)
        case md: MultiDefDecl => CodeGenerator.buildString(md)
        case mm: MultiMethod => CodeGenerator.buildString(mm)
        case rd: RecordDecl => CodeGenerator.buildString(rd)
        case sd: SimpleDefDecl => CodeGenerator.buildString(sd)
        case tl:TopLevelExpression => CodeGenerator.buildString(tl)
        case td: TraitDecl => CodeGenerator.buildString(td)
        case _ => s"**ERROR (${nodes.head.getClass})**"
      }
      genDecls(nodes.tail, s :: strs )
    }
  }

}
