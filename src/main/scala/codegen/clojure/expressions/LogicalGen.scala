package codegen.clojure.expressions

import codegen.{CodeGenerator, Translator}
import codegen.clojure.expressions.ExpressionsGen._
import parser.ast.expressions.logical.{LogicalAndExpression, LogicalExpression, LogicalOrExpression}

object LogicalGen {

  implicit object LogicalExpressionTranslator extends Translator[LogicalExpression] {

    override def mkString(node: LogicalExpression): String = {
      node match {
        case LogicalOrExpression(args) =>
          s"(or ${args.map(a => CodeGenerator.buildString(a)).mkString(" ")})"

        case LogicalAndExpression(args) =>
          s"(and ${args.map(a => CodeGenerator.buildString(a)).mkString(" ")})"


      }
    }
  }
}
