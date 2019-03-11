package exceptions

import java.io.PrintStream

import lexer.{SYMBOL, Token}

case class UnexpectedTokenException(tokenOpt: Option[Token]) extends ParserException {
  override def showError(stream: PrintStream): AnyRef = {
    tokenOpt match {
      case Some(token) =>
        val msg = s"Unexpected token: ${token.symbol} at line: ${token.line}, expecting: $token"
        stream.println(msg)
        msg
      case _ => Nil
    }
  }
}
