package exceptions

import java.io.PrintStream

import lexer.TOKEN

case class UnexpectedTokenClassException(token: TOKEN) extends ParserException {
  override def showError(stream: PrintStream): AnyRef = ???
}
