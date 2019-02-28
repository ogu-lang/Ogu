package codegen.clojure.expressions

import codegen.{CodeGenerator, Translator}
import parser.ast.expressions.functions.FunctionCallExpression
import codegen.clojure.expressions.ExpressionsGen._

object FunctionsGen {


  implicit object FunctionCallExpressionTranslator extends Translator[FunctionCallExpression] {
    override def mkString(node: FunctionCallExpression): String = {
      s"(${CodeGenerator.buildString(node.func)} ${node.args.map(a => CodeGenerator.buildString(a)).mkString(" ")})"
    }
  }

}
