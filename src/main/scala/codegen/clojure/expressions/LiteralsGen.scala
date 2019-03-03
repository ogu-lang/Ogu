package codegen.clojure.expressions

import codegen.Translator
import parser.ast.expressions.literals._

object LiteralsGen {

  implicit object LiteralExpressionTranslator extends Translator[LiteralExpression] {

    override def mkString(node: LiteralExpression): String = {
      node match {
        case Atom(value) => value
        case BoolLiteral(value) => value.toString
        case StringLiteral(str) => str
        case FStringLiteral(str) => s"(fmt $str)"
        case RegexpLiteral(re) => "#\"" + re + "\""
        case BigIntLiteral(bi) => bi.toString()
        case BigDecimalLiteral(bd) => bd.toString()
        case CharLiteral(c) =>
          val s = s"${c.stripPrefix("\'").stripSuffix("\'")}"
          "\\" + s.stripPrefix("\\")
        case DateTimeLiteral(date) => "#inst  \"" + s"$date" + "\""
        case DoubleLiteral(d) => d.toString
        case IntLiteral(i) => i.toString
        case LongLiteral(l) => l.toString
      }
    }
  }

}
