package codegen.clojure.expressions

import codegen.{CodeGenerator, Translator}
import parser.ast.expressions.functions._
import codegen.clojure.expressions.ExpressionsGen._
import parser.ast.expressions.types.{ConstructorExpression, NewCallExpression, RecordConstructorExpression}
import parser.ast.expressions.{CallExpression, LambdaArg, LambdaSimpleArg, LambdaTupleArg}

object FunctionsGen {

  implicit object CallExpressionExpressionTranslator extends Translator[CallExpression] {
    override def mkString(node: CallExpression): String = {
      node match {
        case ComposeExpressionForward(args) =>
          s"(comp ${args.reverse.map(CodeGenerator.buildString(_)).mkString(" ")})"

        case ComposeExpressionBackward(args) =>
         s"(comp ${args.map(CodeGenerator.buildString(_)).mkString(" ")})"

        case DotoForwardExpression(args) =>
          s"(doto ${args.map(CodeGenerator.buildString(_)).mkString("\n\t")})"

        case DotoBackwardExpression(args) =>
          s"(doto ${args.reverse.map(CodeGenerator.buildString(_)).mkString("\n\t")})"

        case FunctionCallExpression(func, args) =>
          s"(${CodeGenerator.buildString(func)} ${args.map(a => CodeGenerator.buildString(a)).mkString(" ")})"

        case ForwardPipeFuncCallExpression(args) =>
          s"(->> ${args.map(a => CodeGenerator.buildString(a)).mkString(" ")})"

        case ForwardPipeFirstArgFuncCallExpression(args) =>
          s"(-> ${args.map(a => CodeGenerator.buildString(a)).mkString(" ")})"

        case BackwardPipeFuncCallExpression(args) =>
          s"(->> ${args.map(a => CodeGenerator.buildString(a)).mkString(" ")})"

        case FunctionCallWithDollarExpression(func, args) =>
          s"(${CodeGenerator.buildString(func)} ${args.map(a => CodeGenerator.buildString(a)).mkString(" ")})"

        case ConstructorExpression(cls, args) =>
          s"($cls. ${args.map(CodeGenerator.buildString(_)).mkString(" ")})"

        case RecordConstructorExpression(cls, args) =>
          s"(->$cls ${args.map(CodeGenerator.buildString(_)).mkString(" ")})"

        case NewCallExpression(cls, Nil) =>
          s"($cls.)"

        case NewCallExpression(cls, args) =>
          s"($cls. ${args.map(CodeGenerator.buildString(_)).mkString(" ")})"

      }
    }
  }

  implicit object LambdaArgTranslator extends Translator[LambdaArg] {

    override def mkString(node: LambdaArg): String = {
      node match {
        case LambdaSimpleArg(name) => name
        case LambdaTupleArg(names) => s"[${names.mkString(" ")}]"


      }
    }
  }

  implicit object LambdaExpressionTranslator extends Translator[LambdaExpression] {

    override def mkString(node: LambdaExpression): String = {
      s"(fn [${node.args.map(a => CodeGenerator.buildString(a)).mkString(" ")}] ${CodeGenerator.buildString(node.expr)})"
    }
  }

}
