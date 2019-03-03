package codegen.clojure.expressions

import codegen.{CodeGenerator, Translator}
import codegen.clojure.expressions.ExpressionsGen._
import parser.ast.expressions.list_ops.{ConcatExpression, ConsExpression, ContainsExpr, ListOpExpresion}

object ListOpsGen {

  implicit object ListOpExpresionTranslator extends Translator[ListOpExpresion] {

    override def mkString(node: ListOpExpresion): String = {
      node match {
        case ConcatExpression(args) =>
          s"(concat ${args.map(CodeGenerator.buildString(_)).mkString(" ")})"

        case ConsExpression(args) =>
          s"(cons ${args.map(CodeGenerator.buildString(_)).mkString(" ")})"

        case ContainsExpr(left, right) =>
          s"(elem ${CodeGenerator.buildString(right)} ${CodeGenerator.buildString(left)})"
      }
    }

  }

}
