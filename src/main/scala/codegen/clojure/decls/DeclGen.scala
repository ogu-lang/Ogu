package codegen.clojure.decls

import codegen.{CodeGenerator, Translator}
import codegen.clojure.expressions.ExpressionsGen._
import parser.ast.decls._
import parser.ast.expressions.{Identifier, InfiniteTupleExpr, TupleExpression}

object DeclGen {

  implicit object DefArgTranslator extends Translator[DefArg] {

    override def mkString(node: DefArg): String = {
      node match {
        case DefOtherwiseArg => ":default"

        case DefArg(Identifier(id)) => id

        case DefArg(TupleExpression(exprs)) => s"[${exprs.map(e => CodeGenerator.buildString(e)).mkString(" ")}]"

        case DefArg(InfiniteTupleExpr(exprs)) =>
          val rest = exprs.last
          val args = exprs.dropRight(1)
          s"[${args.map(a => CodeGenerator.buildString(a)).mkString(" ")} & ${CodeGenerator.buildString(rest)}]"

        case DefArg(expression) => s"${CodeGenerator.buildString(expression)}"

      }
    }

  }

  implicit object DefBodyGuardExprTranslator extends Translator[DefBodyGuardExpr] {

    override def mkString(node: DefBodyGuardExpr): String = {
      node match {
        case DefBodyGuardExpression(comp, expr) => s"\t${CodeGenerator.buildString(comp)} ${CodeGenerator.buildString(expr)}"
        case DefBodyGuardOtherwiseExpression(expr) => s"\t:else ${CodeGenerator.buildString(expr)}"
      }
    }
  }

  implicit object WhereGuardTranslator extends Translator[WhereGuard] {

    override def mkString(node: WhereGuard): String = {
        node match  {
          case WhereGuard(Some(comp), expr) => s"${CodeGenerator.buildString(comp)} ${CodeGenerator.buildString(expr)}"
          case WhereGuard(None, expr) => s":else ${CodeGenerator.buildString(expr)}"
        }
    }

  }

  implicit object WhereDefTranslator extends Translator[WhereDef] {

    override def mkString(node: WhereDef): String = {
      node match {
        case WhereDefSimple(id, None, body) => s"(def $id ${CodeGenerator.buildString(body)})"
        case WhereDefSimple(id, Some(args), body) =>
          s"(def $id (fn [${args.map(a => CodeGenerator.buildString(a)).mkString(" ")}] ${CodeGenerator.buildString(body)}))"
        case WhereDefWithGuards(id, Some(args), guards) =>
          s"(def $id (fn [${args.map(a => CodeGenerator.buildString(a)).mkString(" ")}] \n" +
            s"(cond ${guards.map(g => CodeGenerator.buildString(g)).mkString("\n")})))"
        case WhereDefTupled(idList, None, body) =>
          var strBuf = new StringBuilder()
          strBuf ++= s"(def _*temp*_ ${CodeGenerator.buildString(body)})\n"
          var i = 0
          for (id <- idList) {
            strBuf ++= s"(def ${id} (nth _*temp*_ $i))\n"
            i += 1
          }
          strBuf.toString()
      }
    }
  }

  implicit object WhereBlockTranslator extends Translator[WhereBlock] {

    override def mkString(node: WhereBlock): String = {
       node.whereDefs.map(wd => CodeGenerator.buildString(wd)).mkString("\n")
    }

  }

  implicit object SimpleDefDeclTranslator extends Translator[SimpleDefDecl] {

    override def mkString(node: SimpleDefDecl): String = {
      node match {
        case SimpleDefDecl(inner, id, args, BodyGuardsExpresion(guards), None) =>
          val conds = s"\t(cond\n${guards.map(g => CodeGenerator.buildString(g)).mkString("\n")})"
          if (inner) {
            s"(defn- $id [${args.map(a => CodeGenerator.buildString(a)).mkString(" ")}]\n\t$conds)"
          } else {
            s"(defn $id [${args.map(a => CodeGenerator.buildString(a)).mkString(" ")}]\n\t$conds)"
          }

        case SimpleDefDecl(inner, id, args, body, None) =>
          if (inner) {
            s"(defn- $id [${args.map(a => CodeGenerator.buildString(a)).mkString(" ")}]\n\t\t${CodeGenerator.buildString(body)})\n\n"
          } else {
            s"(defn $id [${args.map(a => CodeGenerator.buildString(a)).mkString(" ")}]\n\t\t${CodeGenerator.buildString(body)})\n\n"
          }

        case SimpleDefDecl(inner, id, args, body, Some(whereBlock)) =>
          if (inner) {
            s"(defn- $id [${args.map(a => CodeGenerator.buildString(a)).mkString(" ")}]\n" +
              s"\t\t${CodeGenerator.buildString(whereBlock)}\n    ${CodeGenerator.buildString(body)})\n\n"
          } else {
            s"(defn $id [${args.map(a => CodeGenerator.buildString(a)).mkString(" ")}]\n" +
              s"\t\t${CodeGenerator.buildString(whereBlock)}\n    ${CodeGenerator.buildString(body)})\n\n"
          }
      }
    }

  }
}
