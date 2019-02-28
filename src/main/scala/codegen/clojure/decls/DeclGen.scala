package codegen.clojure.decls

import codegen.clojure.expressions.ExpressionsGen._
import codegen.{CodeGenerator, Translator}
import parser.ast.decls._
import parser.ast.expressions.{Expression, Identifier}
import parser.ast.expressions.list_ops.ConsExpression
import parser.ast.expressions.types._

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

  implicit object MultiDefDeclTranslator extends Translator[MultiDefDecl] {

    override def mkString(node: MultiDefDecl): String = {
      val strBuf = new StringBuilder()
        if (!node.patternMatching()) {
          strBuf ++= s"\n(defn ${node.id}\n"
          for (decl <- node.decls) {
            strBuf ++= "([" + decl.args.map(arg => s"${CodeGenerator.buildString(arg.expression)}").mkString(" ") + "] "
            if (decl.whereBlock.nonEmpty) {
              val whereDefs = decl.whereBlock.get.whereDefs
              strBuf ++= s"${whereDefs.map(CodeGenerator.buildString(_)).mkString("\n")}"
            }
            strBuf ++= s"${CodeGenerator.buildString(decl.body)})\n"
          }
          strBuf ++= ")\n\n"
        }
        else {
          strBuf ++= s"\n(defn ${node.id} [" + node.args.mkString(" ") + "]\n"
          strBuf ++= "\t(cond\n"
          val args: List[String] = node.args
          for (decl <- node.decls) {
            var andList = List.empty[String]
            var letDecls = List.empty[String]
            var argDecls = decl.args
            var namedArgs = args
            if (decl.whereBlock.nonEmpty) {
              val whereDefs = decl.whereBlock.get.whereDefs
              for (wd <- whereDefs) {
                letDecls = s"${CodeGenerator.buildString(wd)}" :: letDecls
              }
              letDecls = letDecls.reverse
            }
            while (argDecls.nonEmpty) {
              val arg = argDecls.head
              arg match {
                case DefArg(Identifier(id)) if args.contains(id) =>
                // nothing
                case DefArg(_:EmptyListExpresion) =>
                  andList = s"\t\t(empty? ${namedArgs.head})" :: andList

                case DefArg(ConsExpression(args)) =>
                  val tail = args.last
                  val head = args.init
                  letDecls = s"[${head.map(CodeGenerator.buildString(_)).mkString(" ")} & ${CodeGenerator.buildString(tail)}] ${namedArgs.head}" :: letDecls

                case DefArg(ListExpression(defArgs, None)) =>
                  letDecls = s"[${defArgs.map(CodeGenerator.buildString(_)).mkString(" ")}] ${namedArgs.head}]" :: letDecls

                case DefArg(ConstructorExpression(cls, ctorArgs)) =>
                  andList = s"(isa-type? $cls ${namedArgs.head})" :: andList
                  var argDecls = List.empty[String]
                  for (arg <- ctorArgs) {
                    arg match {
                      case Identifier(id) => argDecls = s"$id (.$id ${namedArgs.head})" :: argDecls
                    }
                  }
                  letDecls = argDecls.reverse ++ letDecls

                case DefArg(RecordConstructorExpression(cls, ctorArgs)) =>
                  andList = s"(isa-type? $cls ${namedArgs.head})" :: andList
                  var argDecls = List.empty[String]
                  for (arg <- ctorArgs) {
                    arg match {
                      case Identifier(id) => argDecls = s"$id (.$id ${namedArgs.head})" :: argDecls
                    }
                  }
                  letDecls = argDecls.reverse ++ letDecls
                case DefArg(IdIsType(_, cls)) =>
                  andList = s"(isa-type? $cls ${namedArgs.head})" :: andList

                case DefArg(exp: Expression) =>
                  andList = s"\t\t(= ${namedArgs.head} ${CodeGenerator.buildString(exp)})" :: andList

              }
              argDecls = argDecls.tail
              namedArgs = namedArgs.tail
            }
            if (andList.isEmpty) {
              if (letDecls.isEmpty) {
                strBuf ++= s"\t\t:else  ${CodeGenerator.buildString(decl.body)}"
              }
              else {
                strBuf ++= s"\t\t:else (let [${letDecls.mkString("\n\t\t\t")}]\n\t\t${CodeGenerator.buildString(decl.body)})"
              }
            }
            else if (andList.length == 1) {
              if (letDecls.isEmpty) {
                strBuf ++= s"${andList.mkString(" ")} ${CodeGenerator.buildString(decl.body)}\n"
              }
              else {
                strBuf ++= s"${andList.mkString(" ")} (let [${letDecls.mkString(" ")}]\n\t\t${CodeGenerator.buildString(decl.body)})\n"
              }
            }
            else {
              if (letDecls.isEmpty) {
                strBuf ++= s"  (and ${andList.mkString(" ")}) ${CodeGenerator.buildString(decl.body)}\n"
              }
              else {
                strBuf ++= s"  (and ${andList.mkString(" ")}) (let [${letDecls.mkString(" ")}]\n\t\t${CodeGenerator.buildString(decl.body)})\n"

              }
            }
          }
          strBuf ++= "))\n\n"
        }
      strBuf.mkString
    }
  }

}
