package codegen.clojure.decls

import codegen.clojure.expressions.ExpressionsGen._
import codegen.{CodeGenerator, Translator}
import parser.ast.decls._
import parser.ast.expressions.list_ops.ConsExpression
import parser.ast.expressions.literals.Atom
import parser.ast.expressions.types._
import parser.ast.expressions.{Expression, Identifier}
import scala.annotation.tailrec

object DeclGen {

  type StrList = List[String]

  implicit object DispatchDeclTranslator extends Translator[DispatchDecl] {

    override def mkString(node: DispatchDecl): String = {
      s"(defmulti ${node.id} " + (node.dispatcher match {
        case ClassDispatcher => "class)\n"
        case ExpressionDispatcher(expr) => s"${CodeGenerator.buildString(expr)})\n"
      })
    }
  }

  implicit object DefArgTranslator extends Translator[DefArg] {

    override def mkString(node: DefArg): String = {
      node match {
        case DefOtherwiseArg => ":default"

        case DefArg(Identifier(id)) => id

        case DefArg(VariadicArg(id)) => s"& $id"

        case DefArg(TupleExpression(exprs)) => s"[${exprs.map(e => CodeGenerator.buildString(e)).mkString(" ")}]"

        case DefArg(InfiniteTupleExpr(exprs)) =>
          exprs.reverse match {
            case body :: args =>
              s"[${args.map(CodeGenerator.buildString(_)).mkString(" ")} & ${CodeGenerator.buildString(body)}]"
            case Nil => ""
            case _ => s"[${exprs.map(CodeGenerator.buildString(_)).mkString(" ")}]"
          }


        case DefArg(DictionaryExpression(List((ConsExpression(args), Atom(a))))) =>
          s"{[${args.init.map(CodeGenerator.buildString(_)).mkString(" ")} & ${CodeGenerator.buildString(args.last)}] $a}"

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

  implicit object MultiMethodTranslator extends Translator[MultiMethod] {

    override def mkString(node: MultiMethod): String = {
      node match {
        case MultiMethod(_, id, matches, args, BodyGuardsExpresion(guards), None) =>
          s"(defmethod $id ${matches.map(toClojureDefMatchArg).mkString(" ")} " +
          s"[${args.map(CodeGenerator.buildString(_)).mkString(" ")}]\n" +
          s"  (cond\n${guards.map(CodeGenerator.buildString(_)).mkString("\n")}" +
          "))\n\n"

        case MultiMethod(_, id, matches, args, body, None) =>
          s"(defmethod $id ${matches.map(toClojureDefMatchArg).mkString(" ")} " +
          s"[${args.map(CodeGenerator.buildString(_)).mkString(" ")}]\n\t${CodeGenerator.buildString(body)})\n\n"

        case MultiMethod(_, id, matches, args, body, Some(WhereBlock(whereDefs))) =>
          s"(defmethod $id ${matches.map(toClojureDefMatchArg).mkString(" ")} " +
          s"[${args.map(CodeGenerator.buildString(_)).mkString(" ")}]\n" +
          s"\t(let [${whereDefs.map(mkStringAsLet).mkString("\n")}]" +
          s"\n\t\t${CodeGenerator.buildString(body)}))"

        case _ => ""
      }
    }


    private[this] def toClojureDefMatchArg(defArg: DefArg): String = {
      defArg match {
        case DefArg(ConstructorExpression(cls, _)) => s"$cls"
        case DefArg(RecordConstructorExpression(cls, _)) => s"$cls"
        case _ => CodeGenerator.buildString(defArg)
      }
    }

  }

  implicit object WhereGuardTranslator extends Translator[WhereGuard] {

    override def mkString(node: WhereGuard): String = {
        node match  {
          case WhereGuard(Some(comp), expr) => s"${CodeGenerator.buildString(comp)} ${CodeGenerator.buildString(expr)}"
          case WhereGuard(None, expr) => s":else ${CodeGenerator.buildString(expr)}"
          case _ => ""
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
        case WhereDefWithGuards(id, None, guards) =>
          s"(def $id  \n" +
            s"(cond ${guards.map(g => CodeGenerator.buildString(g)).mkString("\n")}))"
        case WhereDefTupled(idList, None, body) =>
          s"(def _*temp*_ ${CodeGenerator.buildString(body)})\n" +
            idList.zipWithIndex.map { case (id, i) => s"(def $id (nth _*temp*_ $i))" }.mkString("\n")
        case _ => ""
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
      if (node.isMulti()) {
        MultiDefDeclTranslator.mkString(MultiDefDecl(node.id, List(node)))
      }
      else {
        def prefix(inner: Boolean): String = if (inner) "(defn- " else "(defn "

        def sArgs(args: List[DefArg]) = args.map(a => CodeGenerator.buildString(a)).mkString(" ")

        node match {
          case SimpleDefDecl(inner, id, args, BodyGuardsExpresion(guards), None) =>
            val conds = s"\t(cond\n${guards.map(g => CodeGenerator.buildString(g)).mkString("\n")})"
            s"${prefix(inner)} $id [${sArgs(args)}]\n\t$conds)"

          case SimpleDefDecl(inner, id, args, body, None) =>
            s"${prefix(inner)} $id [${sArgs(args)}]\n\t\t${CodeGenerator.buildString(body)})\n\n"

          case SimpleDefDecl(inner, id, args, body, Some(whereBlock)) =>
            s"${prefix(inner)} $id [${sArgs(args)}]\n" +
              s"\t\t${CodeGenerator.buildString(whereBlock)}\n\t${CodeGenerator.buildString(body)})\n\n"

          case _ => ""
        }
      }
    }

  }

  implicit object MultiDefDeclTranslator extends Translator[MultiDefDecl] {

    override def mkString(node: MultiDefDecl): String = {
      if (!node.patternMatching()) {
        s"\n(defn ${node.id}\n" +
          node.decls.map { decl =>
            "([" + decl.args.map(arg => s"${CodeGenerator.buildString(arg.expression)}").mkString(" ") + "] " +
              (decl.whereBlock match {
                case None => s"${CodeGenerator.buildString(decl.body)})\n"
                case Some(WhereBlock(whereDefs)) =>
                  s"(let [${whereDefs.map(mkStringAsLet).mkString("\n")}]" +
                    s"\n\t${CodeGenerator.buildString(decl.body)})"
              })
          }.mkString("") + ")\n\n"
      }
      else {
        val namedArgs: List[String] = 0.until(node.count).map(i => s"_*_arg_$i").toList
        val args2 = namedArgs ++ namedArgs
        s"\n(defn ${node.id} [" + namedArgs.mkString(" ") + "]\n" +
          "(let [" + node.args.zip(args2).map { case (k, v) => s"$k $v" }.mkString(" ") + "]\n\t(cond\n" +
          node.decls.map { decl =>
            val letDecls = decl.whereBlock match {
              case None => Nil
              case Some(WhereBlock(whereDefs)) => whereDefs.map(mkStringAsLet)
            }
            buildCond(decl.body, decl.args, namedArgs, Nil, letDecls)
          }.mkString("") + ")))\n\n"
      }
    }

    @tailrec
    private[this]
    def buildCond(body:Expression, argDecls: List[DefArg], args: StrList, ands: StrList, lets: StrList): String = {
      argDecls.headOption match {
        case None => mergeAndLets(ands, lets, body)
        case Some(arg) =>
          val headArg = args.headOption.getOrElse("")
          arg match {
            case DefArg(EmptyListExpresion) =>
              buildCond(body, argDecls.tail, args.tail, s"\t\t(empty? $headArg)" :: ands, lets)
            case DefArg(ConsExpression(cArgs)) =>
              val tail = CodeGenerator.buildString(cArgs.last)
              val head = cArgs.init.map(CodeGenerator.buildString(_)).mkString(" ")
              val letDecl = s"[$head & $tail] ${args.headOption.getOrElse("")}"
              buildCond(body, argDecls.tail, args.tail, ands, letDecl :: lets)
            case DefArg(ListExpression(defArgs, None)) =>
              val letDecl = s"[${defArgs.map(CodeGenerator.buildString(_)).mkString(" ")}] $headArg]"
              buildCond(body, argDecls.tail, args.tail, ands, letDecl :: lets)
            case DefArg(ConstructorExpression(cls, ctorArgs)) =>
              ctor(cls, ctorArgs, body, argDecls, args, ands, lets)
            case DefArg(RecordConstructorExpression(cls, ctorArgs)) =>
              ctor(cls, ctorArgs, body, argDecls, args, ands, lets)
            case DefArg(IdIsType(_, cls)) =>
              buildCond(body, argDecls.tail, args.tail, s"(isa-type? $cls $headArg)" :: ands, lets)
            case DefArg(VariadicArg(expr)) =>
              buildCond(body, argDecls.tail, args.tail, s"\t\t(= $headArg $expr)" :: ands, lets)
            case DefArg(TupleExpression(List(lit: Expression, Identifier("_")))) =>
              lit match {
                case Identifier(n) =>
                  val newAndList = s":else " :: ands
                  val newLetDecls = s"[$n & _] $headArg" :: lets
                  buildCond(body, argDecls.tail, args.tail, newAndList, newLetDecls)
                case _ =>
                  val newAndList = s"\t\t(= (head $headArg) ${CodeGenerator.buildString(lit)})" :: ands
                  buildCond(body, argDecls.tail, args.tail, newAndList, lets)
              }
            case DefArg(exp: Expression) =>
              val newAndList = s"\t\t(= $headArg ${CodeGenerator.buildString(exp)})" :: ands
              buildCond(body, argDecls.tail, args.tail, newAndList, lets)

            case _ => buildCond(body, argDecls.tail, args.tail, ands, lets)
          }
      }
    }

    private[this]
    def ctor(cls: String, ctorArgs: List[Expression], body: Expression, argDecls: List[DefArg], args: StrList, andList: StrList, lets: StrList) : String = {
      val head= args.headOption.getOrElse("")
      val and = s"(isa-type? $cls $head)"
      val ctorDecls = ctorArgs.flatMap {
        case Identifier(id) => Some(s"$id (.$id $head)")
        case _ => None
      }
      buildCond(body, argDecls.tail, args.tail, and :: andList, ctorDecls ++ lets)
    }


    private[this] def mergeAndLets(andList: StrList, letDecls: StrList, body: Expression) : String = {
      val sBody = CodeGenerator.buildString(body)
      val sLet = letDecls match {
        case Nil => s"$sBody"
        case _ => s"(let [${letDecls.mkString("\n\t\t\t")}]\n\t\t$sBody)"
      }
      andList match {
        case Nil => s"\t\t:else $sLet"
        case List(comp) => s"\t\t$comp $sLet"
        case _ => s"\t\t(and ${andList.mkString(" ")}) $sLet\n"
      }
    }

  }

  implicit object DefDeclTranslator extends Translator[DefDecl] {

    override def mkString(node: DefDecl): String = {
      node match {
        case sd: SimpleDefDecl => CodeGenerator.buildString(sd)
        case md: MultiDefDecl => CodeGenerator.buildString(md)
        case mm: MultiMethod => CodeGenerator.buildString(mm)
        case _ => ""
      }
    }
  }

  private[this] def mkStringAsLet(node: WhereDef): String = {
    node match {
      case WhereDefSimple(id, None, body) => s"$id ${CodeGenerator.buildString(body)}"
      case WhereDefSimple(id, Some(args), body) =>
        s"$id (fn [${args.map(a => CodeGenerator.buildString(a)).mkString(" ")}] ${CodeGenerator.buildString(body)})"
      case WhereDefWithGuards(id, Some(args), guards) =>
        s"$id (fn [${args.map(a => CodeGenerator.buildString(a)).mkString(" ")}] \n" +
          s"(cond ${guards.map(g => CodeGenerator.buildString(g)).mkString("\n")}))"
      case WhereDefWithGuards(id, None, guards) =>
        s"$id  \n" +
          s"(cond ${guards.map(g => CodeGenerator.buildString(g)).mkString("\n")})"
      case WhereDefTupled(idList, None, body) =>
        s"[${idList.mkString(" ")}] ${CodeGenerator.buildString(body)}"
      case _ => ""
    }
  }

}
