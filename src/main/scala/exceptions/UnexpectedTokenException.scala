package exceptions

import java.io.PrintStream

import lexer.TOKEN

case class UnexpectedTokenException(token: TOKEN, tokens: List[TOKEN]) extends ParserException {
  override def showError(stream: PrintStream): AnyRef = {
    val msg = s"Unexpected token: ${token} at line: "
    stream.println(msg)
    msg
  }
}
