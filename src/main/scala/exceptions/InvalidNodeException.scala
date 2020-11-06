package exceptions

import java.io.PrintStream

import lexer.SYMBOL

case class InvalidNodeException(token: SYMBOL, line:Int) extends ParserException {
  override def showError(stream: PrintStream): AnyRef = {
    val msg = s"Invalide node declaration at line: $line, near token: $token"
    stream.println(msg)
    msg
  }
}
