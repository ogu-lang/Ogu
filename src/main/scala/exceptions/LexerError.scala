package exceptions

import java.io.PrintStream

import lexer.ERROR

case class LexerError(error: ERROR) extends ParserException {
  override def showError(stream: PrintStream): AnyRef = {
    val msg = s"Lexical Error at line ${error.line}: ${error.text}"
    stream.println(msg)
    msg
  }
}
