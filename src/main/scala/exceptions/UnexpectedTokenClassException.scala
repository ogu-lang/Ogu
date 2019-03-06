package exceptions

import java.io.PrintStream

import lexer.TOKEN

case class UnexpectedTokenClassException(token: TOKEN, line: Int) extends ParserException {
  override def showError(stream: PrintStream): AnyRef = {
    val msg = s"Unexpected token $token in line: $line"
    stream.println(msg)
    msg
  }
}
