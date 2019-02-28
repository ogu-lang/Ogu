package codegen.clojure.expressions

import codegen.Translator
import parser.ast.expressions.literals.{IntLiteral, LiteralExpression}

object LiteralsGen {

  implicit object LiteralExpressionTranslator extends Translator[LiteralExpression] {

    override def mkString(node: LiteralExpression): String = {
      node match {
        case IntLiteral(i) => i.toString
      }
    }
  }
}
