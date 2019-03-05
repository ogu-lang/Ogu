package codegen.clojure.expressions

import codegen.clojure.expressions.ExpressionsGen._
import codegen.{CodeGenerator, Translator}
import parser.ast.expressions.types.{DictionaryExpression, SetExpression, TupleExpression}

object TypeExprGen {

  implicit object DictionaryExpressionTranslator extends Translator[DictionaryExpression] {

    override def mkString(node: DictionaryExpression): String = {
      s"{${node.items.map{case (k,v) => CodeGenerator.buildString(k) + " " + CodeGenerator.buildString(v)}.mkString(" ")}}"
    }
  }

  implicit object TupleExpressionTranslator extends Translator[TupleExpression] {

    override def mkString(node: TupleExpression): String = {
      s"[${node.expressions.map(e => CodeGenerator.buildString(e)).mkString(" ")}]"
    }

  }

  implicit object SetExpressionTranslator extends Translator[SetExpression] {

    override def mkString(node: SetExpression): String = {
      s"#{${node.values.map(CodeGenerator.buildString(_)).mkString(" ")}}"
    }

  }
}
