package parser.ast.expressions

import lexer.{PIPE_RIGHT, TokenStream}
import parser.{CallExpression, Expression}


case class ForwardPipeFuncCallExpression(args: List[Expression]) extends CallExpression

object ForwardPipeFuncCallExpression extends LeftAssociativeExpressionParser(ForwardPipeFirstArgFuncCallExpression) {

  override def hasOper(tokens: TokenStream): Boolean = tokens.peek(PIPE_RIGHT)

  override def build(args: List[Expression]): Expression = ForwardPipeFuncCallExpression(args)

  override def consumeOper(tokens: TokenStream): Unit = tokens.consume(PIPE_RIGHT)
}
