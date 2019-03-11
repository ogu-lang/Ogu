package exceptions

import java.io.PrintStream

import lexer.{SYMBOL, Token}

case class UnexpectedTokenException(token: SYMBOL, box: Token) extends ParserException {
  override def showError(stream: PrintStream): AnyRef = {
    val msg = s"Unexpected token: ${box.symbol} at line: ${box.line}, expecting: $token"
    stream.println(msg)
    msg
  }
}
