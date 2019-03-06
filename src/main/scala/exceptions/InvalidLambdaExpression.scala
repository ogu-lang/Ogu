package exceptions

import java.io.PrintStream

import lexer.TOKEN

case class InvalidLambdaExpression(token: TOKEN) extends ParserException {
  override def showError(stream: PrintStream): AnyRef = ???
}
