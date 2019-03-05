package codegen.clojure.expressions

import codegen.clojure.expressions.ArithmeticsGen._
import codegen.clojure.expressions.ComparativeGen._
import codegen.clojure.expressions.ControlGen._
import codegen.clojure.expressions.DeclsExprGen._
import codegen.clojure.expressions.FunctionsGen._
import codegen.clojure.expressions.ListOpsGen._
import codegen.clojure.expressions.LiteralsGen._
import codegen.clojure.expressions.LogicalGen._
import codegen.clojure.expressions.PartialOperGen._
import codegen.clojure.expressions.RangeGen._
import codegen.clojure.expressions.RegexpGen._
import codegen.clojure.expressions.TypeExprGen._
import codegen.{CodeGenerator, Translator}
import parser.ast.decls.BodyGuardsExpresion
import parser.ast.expressions._
import parser.ast.expressions.arithmetic.PartialOper
import parser.ast.expressions.comparisons.ComparativeExpression
import parser.ast.expressions.control.{ControlExpression, ThrowExpression}
import parser.ast.expressions.functions.LambdaExpression
import parser.ast.expressions.list_ops.ListOpExpresion
import parser.ast.expressions.literals.{LiteralExpression, StringLiteral}
import parser.ast.expressions.logical.LogicalExpression
import parser.ast.expressions.regexp.RegexExpression
import parser.ast.expressions.types.{DictionaryExpression, SetExpression, TupleExpression, ValidRangeExpression}
import parser.ast.expressions.vars.{BindDeclExpression, LetDeclExpression, UsingExpression, VarDeclExpression}


object ExpressionsGen {

  implicit object IdentifierTranslator extends Translator[Identifier] {

    override def mkString(node: Identifier): String = {
      val id = node.name
      val prefix = if (VarDeclExpressionTranslator.isVariable(id)) "@" else ""
      val pos = id.lastIndexOf('.')
      if (pos < 1) {
        prefix + id
      }
      else {
        val parts = id.split('.')
        parts match {
          case _ if parts.exists(p => p.headOption.exists(c => c.isUpper)) =>
            val sb = new StringBuilder(id)
            sb.replace(pos, pos + 1, "/")
            prefix + sb.toString()
          case Array(head, last) => s"(.${parts.last} ${parts.head})"
          case _ => prefix + id
        }
      }
    }
  }

  implicit object ExpressionTranslator extends Translator[Expression] {

    override def mkString(node: Expression): String = {
      node match {
        case ArrayAccessExpression(array, index) =>
          s"(aget ${mkString(array)} ${mkString(index)})"

        case BlockExpression(expressions) =>
          expressions match {
            case Nil => ""
            case List(e) => mkString(e)
            case _ =>  s"(do ${expressions.map(mkString).mkString("\n")})"
          }
        case i:Identifier => CodeGenerator.buildString(i)
        case StringLiteral(value) => value
        case ae: ArithmeticExpression => CodeGenerator.buildString(ae)
        case bd: BindDeclExpression => CodeGenerator.buildString(bd)
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
        case re: RegexExpression => CodeGenerator.buildString(re)
        case se: SetExpression => CodeGenerator.buildString(se)
        case te: ThrowExpression => CodeGenerator.buildString(te)
        case te: TupleExpression => CodeGenerator.buildString(te)
        case ue: UsingExpression => CodeGenerator.buildString(ue)
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
