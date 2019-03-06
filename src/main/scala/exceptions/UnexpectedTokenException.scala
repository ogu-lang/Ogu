package exceptions

import java.io.PrintStream

import lexer.{TOKEN, TokenBox}

case class UnexpectedTokenException(token: TOKEN, box: TokenBox) extends ParserException {
  override def showError(stream: PrintStream): AnyRef = {
    val msg = s"Unexpected token: ${box.token} at line: ${box.line}, expecting: $token"
    stream.println(msg)
    msg
  }
}
