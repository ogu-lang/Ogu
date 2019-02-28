package codegen.clojure.expressions

import codegen.Translator
import parser.ast.expressions.literals.{DoubleLiteral, IntLiteral, LiteralExpression}

object LiteralsGen {

  implicit object LiteralExpressionTranslator extends Translator[LiteralExpression] {

    override def mkString(node: LiteralExpression): String = {
      node match {
        case DoubleLiteral(d) => d.toString
        case IntLiteral(i) => i.toString
      }
    }
  }
}
