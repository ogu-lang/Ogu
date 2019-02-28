package codegen.clojure.expressions

import codegen.{CodeGenerator, Translator}
import codegen.clojure.expressions.FunctionsGen._
import codegen.clojure.expressions.DeclsGen._
import parser.ast.expressions._
import parser.ast.expressions.functions.FunctionCallExpression
import parser.ast.expressions.literals.StringLiteral
import parser.ast.expressions.vars.LetDeclExpression

object ExpressionsGen {


  implicit object ExpressionTranslator extends Translator[Expression] {

    override def mkString(node: Expression): String = {
      node match {
        case Identifier(name) => name
        case StringLiteral(value) => value
        case f: FunctionCallExpression => CodeGenerator.buildString(f)
        case ld: LetDeclExpression => CodeGenerator.buildString(ld)
        case ae:
        case _ => s"EXPRESSION(${node.getClass})"
      }
    }

  }

  implicit object TopLevelExpressionTranslator extends Translator[TopLevelExpression] {
    override def mkString(node: TopLevelExpression): String = {
      CodeGenerator.buildString(node.expression)
    }
  }


}
