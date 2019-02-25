package codegen

import parser._
import parser.ast.expressions._
import parser.ast.functions._
import parser.ast.types._
import parser.ast.module._


class ClojureCodeGenerator(node: LangNode) extends CodeGenerator {

  private[this] var varDecls : Set[String] = Set.empty[String]

  def mkString(): String = toClojure(node)

  def toClojure(node: LangNode): String = {
    val strBuf = new StringBuilder()
    node match {
      case Module(name, None, decls) =>
        strBuf ++= s"(ns $name )\n\n"
        for (node <- decls) {
          strBuf ++= toClojure(node)
        }

      case Module(name, Some(imports), decls) =>
        strBuf ++= s"(ns $name ${toClojureImportClauses(imports)})\n\n"
        for (node <- decls) {
          strBuf ++= toClojure(node)
        }

      case TopLevelExpression(expression) =>
        strBuf ++= toClojure(expression) + "\n"

      case RecordDecl(name, args) =>
        strBuf ++= s"(defrecord $name [${args.mkString(" ")}])\n"

      case ClassDecl(_, name, args, traits) =>
        strBuf ++= s"(deftype $name [${args.getOrElse(List.empty[String]).mkString(" ")}]\n"
        if (traits.nonEmpty) {
          strBuf ++= s"\t${traits.map(toClojure).mkString("\n\t")}"
        }
        strBuf ++= ")\n\n"

      case TraitDef(traitName, methods) =>
        strBuf ++= s"$traitName\n"
        for (method <- methods) {
          val s = toClojure(method.definition).replaceFirst("\\(defn\\s+", "\t(")
          strBuf ++= s
        }

      case ExtendsDecl(cls, traitClass, methods) =>
        strBuf ++= s"(extend-type $cls $traitClass"
        for (method <- methods.getOrElse(List.empty)) {
          val s = toClojure(method.definition).replaceFirst("\\(defn\\s+", "\t(")
          strBuf ++= s
        }
        strBuf ++= ")\n\n"

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

      case BindDeclExpr(decls, expression) =>
        strBuf ++= s"(binding ["
        strBuf ++= decls.asInstanceOf[List[LetVariable]].map(d => s"${toClojureLetId(d.id)} ${toClojure(d.value)}").mkString(" ")
        strBuf ++= "]\n"
        strBuf ++= s"\t${toClojure(expression)})\n"

      case LetDeclExpr(decls, Some(expression)) =>
        strBuf ++= "(let ["
        strBuf ++= decls.asInstanceOf[List[LetVariable]].map(d => s"${toClojureLetId(d.id)} ${toClojure(d.value)}").mkString(" ")
        strBuf ++= " ]\n"
        strBuf ++= s"\t${toClojure(expression)})\n"

      case LetDeclExpr(decls: List[_], None) =>
        for (decl <- decls.asInstanceOf[List[LetVariable]]) {
          strBuf ++= s"(def ${toClojureLetId(decl.id)} ${toClojure(decl.value)})\n"
        }

      case VarDeclExpr(decls, None) =>
        for (d <- decls) {
          strBuf ++= toClojureOguVariable(d)
        }

      case VarDeclExpr(decls, Some(expression)) =>
        strBuf ++= "(with-local-vars ["
        strBuf ++= decls.asInstanceOf[List[LetVariable]].map(d => s"${toClojureLetId(d.id)} ${toClojure(d.value)}").mkString(" ")
        strBuf ++= "]\n"
        addVariables(decls)
        strBuf ++= s"${toClojure(expression)})\n"
        removeVariables(decls)

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

      case PowerExpression(left, right) =>
        strBuf ++= s"(pow ${toClojure(left)} ${toClojure(right)})"

      case ComposeExpressionForward(left, right) =>
        strBuf ++= s"(comp ${toClojure(right)} ${toClojure(left)})"

      case ComposeExpressionBackward(left, right) =>
        strBuf ++= s"(comp ${toClojure(left)} ${toClojure(right)})"

      case Identifier(id) =>
        if (isVariable(id)) {
          strBuf ++= "@"
        }
        val pos = id.lastIndexOf('.')
        if (pos <= 1) {
          strBuf ++= id
        } else {
          var containsClass = false
          val parts = id.split('.')
          for (p <- parts) {
            if (p.head.isUpper) {
              containsClass = true
            }
          }
          if (containsClass) {
            val sb = new StringBuilder(id)
            sb.replace(pos, pos + 1, "/")
            strBuf ++= sb.toString()
          }
          else if (parts.length == 2) {
            strBuf ++= s"(.${parts.last} ${parts.head})"
          }
          else {
            strBuf ++= id
          }
        }

      case BoolLiteral(value) =>
        strBuf ++= value.toString

      case CharLiteral(value) =>
        strBuf ++= s"\\${value.stripPrefix("\'").stripSuffix("\'")}"

      case IntLiteral(i) =>
        strBuf ++= i.toString

      case LongLiteral(i) =>
        strBuf ++= i.toString

      case DoubleLiteral(d) =>
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

      case RangeExpression(ini, end) =>
        strBuf ++= s"(range ${toClojure(ini)} (inc ${toClojure(end)}))"

      case RangeExpressionUntil(ini, end) =>
        strBuf ++= s"(range ${toClojure(ini)} ${toClojure(end)})"

      case RangeWithIncrementExpression(ini,inc, end) =>
        strBuf ++= s"(range ${toClojure(ini)} (inc ${toClojure(end)}) ${toClojure(inc)})"

      case RangeWithIncrementExpressionUntil(ini,inc, end) =>
        strBuf ++= s"(range ${toClojure(ini)} ${toClojure(end)} ${toClojure(inc)})"

      case SetExpression(elements) =>
        strBuf ++= s"#{${elements.map(toClojure).mkString(" ")}}"

      case EmptyListExpresion() =>
        strBuf ++= "[]"

      case ListExpression(listOfExpr, None) =>
        strBuf ++= s"[${listOfExpr.map(toClojure).mkString(" ")}]"

      case ListExpression(listOfExpr, Some(guards)) =>
        strBuf ++= s"(for [${guards.map(toClojureListGuard).mkString(" ")}] "
        if (listOfExpr.size == 1) {
          strBuf ++= toClojure(listOfExpr.head)
        } else {
          strBuf ++= s"(do ${listOfExpr.map(toClojure).mkString("\n")})"
        }
        strBuf ++= ")"

      case DictionaryExpression(pairs) =>
        strBuf ++= s"{${pairs.map(toClojureDictPair).mkString(" ")}}"

      case BlockExpression(expressions) =>
        if (expressions.size == 1) {
          strBuf ++= toClojure(expressions.head)
        } else {
          strBuf ++= s"(do ${expressions.map(toClojure).mkString("\n")})"
        }

      case ForExpression(variables, body) =>
        strBuf ++= s"(doall (for [${variables.map(toClojureForVarDeclIn).mkString("\n")}] \n${toClojure(body)}))"

      case LoopExpression(variables, None, body) =>
        strBuf ++= s"(loop [${variables.map(toClojureLoopVar).mkString(" ")}]\n ${toClojure(body)})"

      case LoopExpression(variables, Some(guard), body) =>
        strBuf ++= s"(loop [${variables.map(toClojureLoopVar).mkString(" ")}]\n" +
          s"   (${toClojure(guard)} ${toClojure(body)}))"

      case WhileGuardExpr(comp) =>
        strBuf ++= s"when ${toClojure(comp)}"

      case UntilGuardExpr(comp) =>
        strBuf ++= s"when-not ${toClojure(comp)}"

      case RepeatExpresion(Some(newValues)) =>
        strBuf ++= s"(let [${newValues.map(toClojureNewVarValue).mkString(" ")}]"
        strBuf ++= s"(recur ${newValues.map(nv => nv.variable).mkString(" ")}))"

      case WhileExpression(comp, body) =>
        strBuf ++= s"(while ${toClojure(comp)} ${toClojure(body)})"

      case CondExpression(guards) =>
        strBuf ++= s"(cond\n\t${guards.map(toClojureCondGuard).mkString("\n\t")})"

      case WhenExpression(comp, body) =>
        strBuf ++= s"(when ${toClojure(comp)}\n ${toClojure(body)})"

      case IfExpression(comp, thenPart, elifPart, elsePart) =>
        if (elifPart.nonEmpty) {
          ???
        } else {
          strBuf ++= s"(if ${toClojure(comp)}\n   ${toClojure(thenPart)}\n    ${toClojure(elsePart)})"
        }

      case RecurExpression(args) =>
        strBuf ++= s"(recur ${args.map(toClojure).mkString(" ")})"

      case ConstructorExpression(cls, args) =>
        strBuf ++= s"($cls. ${args.map(toClojure).mkString(" ")})"

      case RecordConstructorExpression(cls, args) =>
        strBuf ++= s"(->$cls ${args.map(toClojure).mkString(" ")})"

      case NewCallExpression(cls, args) if args.isEmpty =>
        strBuf ++= s"($cls.)"

      case FunctionCallExpression(func, args) =>
        strBuf ++= s"(${toClojure(func)} ${args.map(toClojure).mkString(" ")})"

      case FunctionCallWithDollarExpression(func, args) =>
        strBuf ++= s"(${toClojure(func)} ${args.map(toClojure).mkString(" ")})"

      case EqualsExpression(args) =>
        strBuf ++= s"(= ${args.map(toClojure).mkString(" ")})"

      case GreaterThanExpression(args) =>
        strBuf ++= s"(> ${args.map(toClojure).mkString(" ")})"

      case GreaterOrEqualThanExpression(args) =>
        strBuf ++= s"(>= ${args.map(toClojure).mkString(" ")})"

      case LessThanExpression(args) =>
        strBuf ++= s"(< ${args.map(toClojure).mkString(" ")})"

      case LessOrEqualThanExpression(args) =>
        strBuf ++= s"(<= ${args.map(toClojure).mkString(" ")})"

      case ConcatExpression(left, right) =>
        strBuf ++= s"(concat ${toClojure(left)} ${toClojure(right)})"

      case ConsExpression(args) =>
        strBuf ++= s"(cons ${args.map(toClojure).mkString(" ")})"

      case LambdaExpression(args, expr) =>
        strBuf ++= s"(fn [${args.map(toClojureLambdaArg).mkString(" ")}] ${toClojure(expr)})"

      case LogicalAndExpression(args) =>
        strBuf ++= s"(and ${args.map(toClojure).mkString(" ")})"

      case LogicalOrExpression(args) =>
        strBuf ++= s"(or ${args.map(toClojure).mkString(" ")})"

      case PartialAdd(args) =>
        if (args.isEmpty) strBuf ++= "+" else strBuf ++= s"(+ ${args.map(toClojure).mkString(" ")})"

      case PartialSub(args) =>
        if (args.isEmpty) strBuf ++= "-" else strBuf ++= s"(- ${args.map(toClojure).mkString(" ")})"

      case PartialMul(args) =>
        if (args.isEmpty) strBuf ++= "*'" else strBuf ++= s"(* ${args.map(toClojure).mkString(" ")})"

      case PartialDiv(args) =>
        if (args.isEmpty) strBuf ++= "/" else strBuf ++= s"(/ ${args.map(toClojure).mkString(" ")})"

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

      case ForwardPipeFuncCallExpression(args) =>
        strBuf ++= s"(->> ${args.map(toClojure).mkString(" ")})"

      case ForwardPipeFirstArgFuncCallExpression(args) =>
        strBuf ++= s"(-> ${args.map(toClojure).mkString(" ")})"

      case BackwardPipeFuncCallExpression(args) =>
        strBuf ++= s"(->> ${args.map(toClojure).mkString(" ")})"

      case SimpleAssignExpression(ArrayAccessExpression(array, index), value) =>
        strBuf ++= s"(aset ${toClojure(array)} ${toClojure(index)} ${toClojure(value)})"

      case SimpleAssignExpression(Identifier(variable), value) =>
        if (isVariable(variable)) {
          strBuf ++= s"(var-set $variable ${toClojure(value)})"
        }
        else {
          strBuf ++= s"(alter-var-root (var $variable) (constantly ${toClojure(value)}))"
        }

      case MultiMethod(_, id, matches, args, BodyGuardsExpresion(guards), None) =>
        strBuf ++= s"(defmethod $id ${matches.map(toClojureDefMatchArg).mkString(" ")} "
        strBuf ++= s"[${args.map(toClojureDefArg).mkString(" ")}]\n"
        strBuf ++= s"  (cond\n${guards.map(toClojureDefBodyGuardExpr).mkString("\n")}"
        strBuf ++= "))\n\n"

      case MultiMethod(_, id, matches, args, body, None) =>
        strBuf ++= s"(defmethod $id ${matches.map(toClojureDefMatchArg).mkString(" ")} "
        strBuf ++= s"[${args.map(toClojureDefArg).mkString(" ")}]\n\t${toClojure(body)})\n\n"

      case SimpleDefDecl(inner, id, args, BodyGuardsExpresion(guards), None) =>
        if (inner) {
          strBuf ++= s"(defn- $id [${args.map(toClojureDefArg).mkString(" ")}]\n"
        } else {
          strBuf ++= s"(defn $id [${args.map(toClojureDefArg).mkString(" ")}]\n"
        }
        strBuf ++= s"  (cond\n${guards.map(toClojureDefBodyGuardExpr).mkString("\n")}"
        strBuf ++= "))\n\n"

      case SimpleDefDecl(inner, id, args, body, None) =>
        if (inner) {
          strBuf ++= s"(defn- $id [${args.map(toClojureDefArg).mkString(" ")}]\n\t\t${toClojure(body)})\n\n"
        } else {
          strBuf ++= s"(defn $id [${args.map(toClojureDefArg).mkString(" ")}]\n\t\t${toClojure(body)})\n\n"
        }

      case SimpleDefDecl(inner, id, args, body, Some(whereBlock)) =>
        if (inner) {
          strBuf ++= s"(defn- $id [${args.map(toClojureDefArg).mkString(" ")}]\n" +
            s"\t\t${toClojure(whereBlock)}\n    ${toClojure(body)})\n\n"
        } else {
          strBuf ++= s"(defn $id [${args.map(toClojureDefArg).mkString(" ")}]\n" +
            s"\t\t${toClojure(whereBlock)}\n    ${toClojure(body)})\n\n"
        }

      case BodyGuardsExpresion(guards) =>
        strBuf ++= s"(cond\n ${guards.map(toClojureDefBodyGuardExpr).mkString("\n")})"

      case TupleExpr(exprs) =>
        strBuf ++= s"[${exprs.map(toClojure).mkString(" ")}]"

      case WhereBlock(defs) =>
        strBuf ++= defs.map(toClojureWhereDef).mkString("\n")

      case md: MultiDefDecl =>
        if (!md.patternMatching()) {
          strBuf ++= s"\n(defn ${md.id}\n"
          for (decl <- md.decls) {
            strBuf ++= "([" + decl.args.map(arg => s"${toClojure(arg.expression)}").mkString(" ") + "] "
            if (decl.whereBlock.nonEmpty) {
              val whereDefs = decl.whereBlock.get.whereDefs
              strBuf ++= s"${whereDefs.map(toClojureWhereDef).mkString("\n")}"
            }
            strBuf ++= s"${toClojure(decl.body)})\n"
          }
          strBuf ++= ")\n\n"
        }
        else {
          strBuf ++= s"\n(defn ${md.id} [" + md.args.mkString(" ") + "]\n"
          strBuf ++= "\t(cond\n"
          val args: List[String] = md.args
          for (decl <- md.decls) {
            var andList = List.empty[String]
            var letDecls = List.empty[String]
            var argDecls = decl.args
            var namedArgs = args
            if (decl.whereBlock.nonEmpty) {
              val whereDefs = decl.whereBlock.get.whereDefs
              for (wd <- whereDefs) {
                letDecls = s"${toClojureWhereDefAsLet(wd)}" :: letDecls
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
                  letDecls = s"[${head.map(toClojure).mkString(" ")} & ${toClojure(tail)}] ${namedArgs.head}" :: letDecls

                case DefArg(ListExpression(defArgs, None)) =>
                  letDecls = s"[${defArgs.map(toClojure).mkString(" ")}] ${namedArgs.head}]" :: letDecls

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
                  andList = s"\t\t(= ${namedArgs.head} ${toClojure(exp)})" :: andList

              }
              argDecls = argDecls.tail
              namedArgs = namedArgs.tail
            }
            if (andList.isEmpty) {
              if (letDecls.isEmpty) {
                strBuf ++= s"\t\t:else  ${toClojure(decl.body)}"
              }
              else {
                strBuf ++= s"\t\t:else (let [${letDecls.mkString("\n\t\t\t")}]\n\t\t${toClojure(decl.body)})"
              }
            }
            else if (andList.length == 1) {
              if (letDecls.isEmpty) {
                strBuf ++= s"${andList.mkString(" ")} ${toClojure(decl.body)}\n"
              }
              else {
                strBuf ++= s"${andList.mkString(" ")} (let [${letDecls.mkString(" ")}]\n\t\t${toClojure(decl.body)})\n"
              }
            }
            else {
              if (letDecls.isEmpty) {
                strBuf ++= s"  (and ${andList.mkString(" ")}) ${toClojure(decl.body)}\n"
              }
              else {
                strBuf ++= s"  (and ${andList.mkString(" ")}) (let [${letDecls.mkString(" ")}]\n\t\t${toClojure(decl.body)})\n"

              }
            }
          }
          strBuf ++= "))\n\n"
        }

      case LazyExpression(expr) =>
        strBuf ++= s"(lazy-seq ${toClojure(expr)})"

      case InfiniteRangeExpression(init) =>
        strBuf ++= s"(-range-to-inf ${toClojure(init)})"

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

      case TraitDecl(_, name, decls) =>
        strBuf ++= s"(defprotocol $name\n"
        for (decl <- decls) {
          strBuf ++= s"\t(${decl.name} [${decl.args.mkString(" ")}])\n"
        }
        strBuf ++= ")\n\n"

      case _ =>
        strBuf ++= node.toString
    }
    strBuf.toString()
  }



  def toClojureLetId(id: LetId) : String = {
    id match {
      case LetSimpleId(name) => name
      case LetTupledId(ids) => s"[${ids.map(toClojureLetId).mkString(" ")}]"
    }
  }

  def toClojureLoopVar(variable: LoopDeclVariable): String = {
    variable match {
      case LoopVarDecl(id, initialValue) => s"$id ${toClojure(initialValue)}"
      case _ => ???
    }
  }

  def toClojureDictPair(pair: (Expression, Expression)): String = {
    s"${toClojure(pair._1)} ${toClojure(pair._2)}"
  }

  def toClojureForVarDeclIn(variable: LoopDeclVariable): String = {
   variable match {
     case ForVarDeclIn(id, initialValue) => s"$id ${toClojure(initialValue)}"
     case ForVarDeclTupledIn(ids, initialValue) => s"[${ids.mkString(" ")}] ${toClojure(initialValue)}"
   }
  }

  def toClojureNewVarValue(variable: RepeatNewVarValue): String = {
    s"${variable.variable} ${toClojure(variable.value)}"
  }

  def toClojureListGuard(guard: ListGuard): String = {
    guard match {
      case ListGuardDecl(id, value) => s"$id ${toClojure(value)}"
      case ListGuardExpr(expr) => s":when ${toClojure(expr)}"
      case ListGuardDeclTupled(ids, value) => s"[${ids.mkString(" ")}] ${toClojure(value)}"
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
      case DefOtherwiseArg => ":default"
      case DefArg(Identifier(id)) => id
      case DefArg(TupleExpr(exprs)) => s"[${exprs.map(toClojure).mkString(" ")}]"
      case DefArg(InfiniteTupleExpr(exprs)) =>
        val rest = exprs.last
        val args = exprs.dropRight(1)
        s"[${args.map(toClojure).mkString(" ")} & ${toClojure(rest)}]"
      case DefArg(expression) => s"${toClojure(expression)}"
    }
  }

  def toClojureDefMatchArg(defArg: DefArg): String = {
    defArg match {
      case DefArg(ConstructorExpression(cls, _)) => s"$cls"
      case DefArg(RecordConstructorExpression(cls, _)) => s"$cls"
      case d => toClojureDefArg(d)
    }
  }

  def toClojureCondGuard(condGuard: CondGuard) : String = {
    if (condGuard.comp.isDefined) {
      s"${toClojure(condGuard.comp.get)} ${toClojure(condGuard.value)}"
    }
    else {
      s":else ${toClojure(condGuard.value)}"
    }
  }

  def toClojureWhereDef(whereDef: WhereDef): String = {
    whereDef match {
      case WhereDefSimple(id, None, body) => s"(def $id ${toClojure(body)})"
      case WhereDefSimple(id, Some(args), body) =>
        s"(def $id (fn [${args.map(toClojure).mkString(" ")}] ${toClojure(body)}))"
      case WhereDefWithGuards(id, Some(args), guards) =>
        s"(def $id (fn [${args.map(toClojure).mkString(" ")}] \n" +
          s"(cond ${guards.map(toClojureWhereGuard).mkString("\n")})))"
      case WhereDefTupled(idList, None, body) =>
        var strBuf = new StringBuilder()
        strBuf ++= s"(def _*temp*_ ${toClojure(body)})\n"
        var i = 0
        for (id <- idList) {
          strBuf ++= s"(def ${id} (nth _*temp*_ $i))\n"
          i += 1
        }
        strBuf.toString()
      case w =>
        println(w)
        ???
    }
  }

  def toClojureWhereDefAsLet(whereDef: WhereDef): String = {
    whereDef match {
      case WhereDefSimple(id, None, body) => s"$id ${toClojure(body)}"
      case WhereDefSimple(id, Some(args), body) =>
        s"$id (fn [${args.map(toClojure).mkString(" ")}] ${toClojure(body)})"
      case WhereDefWithGuards(id, Some(args), guards) =>
        s"$id (fn [${args.map(toClojure).mkString(" ")}] \n" +
          s"(cond ${guards.map(toClojureWhereGuard).mkString("\n")}))"
      case WhereDefTupled(idList, None, body) =>
        var strBuf = new StringBuilder()
        strBuf ++= s"_*temp*_ ${toClojure(body)}\n"
        var i = 0
        for (id <- idList) {
          strBuf ++= s"${id} (nth _*temp*_ $i)\n"
          i += 1
        }
        strBuf.toString()
      case w =>
        println(w)
        ???
    }
  }

  def toClojureWhereGuard(guard: WhereGuard) : String = {
    guard match  {
      case WhereGuard(Some(comp), expr) => s"${toClojure(comp)} ${toClojure(expr)}"
      case WhereGuard(None, expr) => s":else ${toClojure(expr)}"
    }
  }

  def toClojureOguVariable(variable: Variable) : String = {
    variable match {
      case LetVariable(id, expr) => s"(-def-ogu-var- ${toClojureLetId(id)} ${toClojure(expr)})\n"
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

  def isVariable(id: String): Boolean = {
    this.varDecls.contains(id)
  }

  def addVariables(decls: List[Variable]): Unit = {
    for (v <- decls) {
      v match {
        case LetVariable(LetSimpleId(id), _) => this.varDecls = this.varDecls + id
      }
    }
  }

  def removeVariables(decls: List[Variable]): Unit = {
    for (v <- decls) {
      v match {
        case LetVariable(LetSimpleId(id), _) => this.varDecls = this.varDecls - id
      }
    }
  }
}
