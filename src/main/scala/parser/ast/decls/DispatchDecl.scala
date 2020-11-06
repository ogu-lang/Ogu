package parser.ast.decls

import lexer._
import parser.ast.expressions.Expression
import parser.ast.expressions.functions.ForwardPipeFuncCallExpression
import parser.ast.LangNode

sealed trait DispatcherTrait
object ClassDispatcher extends DispatcherTrait
case class ExpressionDispatcher(expr:Expression) extends DispatcherTrait

case class DispatchDecl(id:String, dispatcher: DispatcherTrait) extends LangNode

object DispatchDecl {

  def parse(inner: Boolean, tokens:TokenStream) : DispatchDecl = {
    tokens.consume(DISPATCH)
    val id = tokens.consume(classOf[ID]).value
    tokens.consume(WITH)
    if (tokens.peek(CLASS)) {
      tokens.consume(CLASS)
      DispatchDecl(id, ClassDispatcher)
    } else {
      DispatchDecl(id, ExpressionDispatcher(ForwardPipeFuncCallExpression.parse(tokens)))
    }
  }
}
