package codegen.clojure.expressions

import codegen.{CodeGenerator, Translator}
import codegen.clojure.expressions.ExpressionsGen._
import parser.ast.expressions.arithmetic._

object PartialOperGen {

  implicit object PartialOperTranslator extends Translator[PartialOper] {

    override def mkString(node: PartialOper): String = {
      node match {
        case PartialAdd(args) =>
          if (args.isEmpty) "+" else  s"(+ ${args.map(a => CodeGenerator.buildString(a)).mkString(" ")})"

        case PartialSub(args) =>
          if (args.isEmpty) "-" else s"(- ${args.map(a => CodeGenerator.buildString(a)).mkString(" ")})"

        case PartialMul(args) =>
          if (args.isEmpty) "*'" else s"(* ${args.map(a => CodeGenerator.buildString(a)).mkString(" ")})"

        case PartialDiv(args) =>
          if (args.isEmpty) "/" else s"(/ ${args.map(a => CodeGenerator.buildString(a)).mkString(" ")})"

        case PartialMod(args) =>
          if (args.isEmpty) "%'" else s"(% ${args.map(a => CodeGenerator.buildString(a)).mkString(" ")})"

        case PartialEQ(args) =>
          if (args.isEmpty) "=" else s"(= ${args.map(a => CodeGenerator.buildString(a)).mkString(" ")})"

        case PartialNE(args) =>
          if (args.isEmpty) "not=" else s"(not= ${args.map(a => CodeGenerator.buildString(a)).mkString(" ")})"

        case PartialLT(args) =>
          if (args.isEmpty) "<" else s"(< ${args.map(a => CodeGenerator.buildString(a)).mkString(" ")})"

        case PartialLE(args) =>
          if (args.isEmpty) "<=" else s"(<= ${args.map(a => CodeGenerator.buildString(a)).mkString(" ")})"

        case PartialGT(args) =>
          if (args.isEmpty) ">" else s"(> ${args.map(a => CodeGenerator.buildString(a)).mkString(" ")})"

        case PartialGE(args) =>
          if (args.isEmpty) ">=" else s"(>= ${args.map(a => CodeGenerator.buildString(a)).mkString(" ")})"

        case PartialCons(args) =>
          if (args.isEmpty) "cons" else s"(cons ${args.map(a => CodeGenerator.buildString(a)).mkString(" ")})"

        case PartialConcat(args) =>
          if (args.isEmpty) "concat" else s"(concat ${args.map(a => CodeGenerator.buildString(a)).mkString(" ")})"
      }
    }

  }

}
