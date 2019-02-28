package codegen.clojure.expressions

import codegen.{CodeGenerator, Translator}
import codegen.clojure.expressions.ExpressionsGen._
import parser.ast.expressions.arithmetic.AddExpression

object ArithmeticsGen {

  implicit object AddExpressionTranslator extends Translator[AddExpression] {

    override def mkString(node: AddExpression): String = {
      s"(+ ${node.args.map(arg => CodeGenerator.buildString(arg))}"
    }

  }

}
