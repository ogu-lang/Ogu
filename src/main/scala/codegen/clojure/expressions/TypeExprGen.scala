package codegen.clojure.expressions

import codegen.{CodeGenerator, Translator}
import codegen.clojure.expressions.ExpressionsGen._
import parser.ast.expressions.types.{DictionaryExpression, SetExpression, TupleExpression}

object TypeExprGen {

  implicit object DictionaryExpressionTranslator extends Translator[DictionaryExpression] {

    override def mkString(node: DictionaryExpression): String = {
      s"{${node.items.map(i => CodeGenerator.buildString(i._1) + " " + CodeGenerator.buildString(i._2)).mkString(" ")}}"
    }
  }

  implicit object TupleExpressionTranslator extends Translator[TupleExpression] {

    override def mkString(node: TupleExpression): String = {
      node match {
        case TupleExpression(exprs) =>
          s"[${exprs.map(e => CodeGenerator.buildString(e)).mkString(" ")}]"
      }

    }

  }

  implicit object SetExpressionTranslator extends Translator[SetExpression] {

    override def mkString(node: SetExpression): String = {
      s"#{${node.values.map(CodeGenerator.buildString(_)).mkString(" ")}}"
    }




  }
}
