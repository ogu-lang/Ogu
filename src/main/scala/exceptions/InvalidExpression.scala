package exceptions

import java.io.PrintStream

import lexer.TOKEN

case class InvalidExpression(token: TOKEN, line: Int) extends ParserException {
  override def showError(stream: PrintStream): AnyRef = {
    val msg = s"Invalid expression at line: $line, near token: $token"
    stream.println(msg)
    msg
  }
}
