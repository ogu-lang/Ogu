package codegen.clojure.expressions

import codegen.Translator
import parser.ast.expressions.literals._

object LiteralsGen {

  implicit object LiteralExpressionTranslator extends Translator[LiteralExpression] {

    override def mkString(node: LiteralExpression): String = {
      node match {
        case BigIntLiteral(bi) => bi.toString()
        case BigDecimalLiteral(bd) => bd.toString()
        case CharLiteral(c) => s"\\${c.stripPrefix("\'").stripSuffix("\'")}"
        case DoubleLiteral(d) => d.toString
        case IntLiteral(i) => i.toString
        case LongLiteral(l) => l.toString
        case Atom(value) => value
      }
    }
  }

}
