package codegen.clojure

import codegen.{CodeGenerator, Translator}
import parser.ast.LangNode
import parser.ast.expressions.TopLevelExpression

object LangNodeGen {


  implicit object LangNodeTranslator extends Translator[LangNode] {

    override def mkString(node: LangNode): String = {
      s"LANG_NODE (${node.getClass})"
      implicitly[Translator[LangNode]].mkString(node)
    }
  }
}
