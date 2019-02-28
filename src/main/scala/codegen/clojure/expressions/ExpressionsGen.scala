package codegen.clojure.expressions

import codegen.{CodeGenerator, Translator}
import codegen.clojure.expressions.ArithmeticsGen._
import codegen.clojure.expressions.ComparativeGen._
import codegen.clojure.expressions.ControlGen._
import codegen.clojure.expressions.DeclsExprGen._
import codegen.clojure.expressions.FunctionsGen._
import codegen.clojure.expressions.LiteralsGen._
import codegen.clojure.expressions.ListOpsGen._
import codegen.clojure.expressions.LogicalGen._
import codegen.clojure.expressions.PartialOperGen._
import codegen.clojure.expressions.RangeGen._
import codegen.clojure.expressions.TypeExprGen._
import parser.ast.decls.BodyGuardsExpresion
import parser.ast.expressions._
import parser.ast.expressions.arithmetic.PartialOper
import parser.ast.expressions.comparisons.ComparativeExpression
import parser.ast.expressions.control.ControlExpression
import parser.ast.expressions.functions.LambdaExpression
import parser.ast.expressions.list_ops.ListOpExpresion
import parser.ast.expressions.literals.{LiteralExpression, StringLiteral}
import parser.ast.expressions.logical.LogicalExpression
import parser.ast.expressions.types.{DictionaryExpression, TupleExpression, ValidRangeExpression}
import parser.ast.expressions.vars.{LetDeclExpression, VarDeclExpression}


object ExpressionsGen {


  implicit object ExpressionTranslator extends Translator[Expression] {

    override def mkString(node: Expression): String = {
      node match {
        case BlockExpression(expressions) =>
          expressions match {
            case Nil => ""
            case List(e) => mkString(e)
            case _ =>  s"(do ${expressions.map(mkString).mkString("\n")})"
          }
        case Identifier(id) =>
          val strBuf = new StringBuilder()
          if (VarDeclExpressionTranslator.isVariable(id)) {
            strBuf ++= "@"
          }
          val pos = id.lastIndexOf('.')
          if (pos <= 1) {
            strBuf ++= id
          } else {
            var containsClass = false
            val parts = id.split('.')
            for (p <- parts) {
              if (p.head.isUpper) {
                containsClass = true
              }
            }
            if (containsClass) {
              val sb = new StringBuilder(id)
              sb.replace(pos, pos + 1, "/")
              strBuf ++= sb.toString()
            }
            else if (parts.length == 2) {
              strBuf ++= s"(.${parts.last} ${parts.head})"
            }
            else {
              strBuf ++= id
            }
          }
          strBuf.mkString
        case StringLiteral(value) => value
        case ae: ArithmeticExpression => CodeGenerator.buildString(ae)
        case bg: BodyGuardsExpresion => CodeGenerator.buildString(bg)
        case ce: CallExpression => CodeGenerator.buildString(ce)
        case ce: ComparativeExpression => CodeGenerator.buildString(ce)
        case ce: ControlExpression => CodeGenerator.buildString(ce)
        case de: DictionaryExpression => CodeGenerator.buildString(de)
        case le: LambdaExpression => CodeGenerator.buildString(le)
        case lo: ListOpExpresion => CodeGenerator.buildString(lo)
        case le: LiteralExpression => CodeGenerator.buildString(le)
        case le: LogicalExpression => CodeGenerator.buildString(le)
        case ld: LetDeclExpression => CodeGenerator.buildString(ld)
        case po: PartialOper => CodeGenerator.buildString(po)
        case te: TupleExpression => CodeGenerator.buildString(te)
        case re: ValidRangeExpression => CodeGenerator.buildString(re)
        case vd: VarDeclExpression => CodeGenerator.buildString(vd)
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
