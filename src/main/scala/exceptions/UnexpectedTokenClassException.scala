package exceptions

import java.io.PrintStream
import lexer.Token


case class UnexpectedTokenClassException(tokenOpt: Option[Token]) extends ParserException {
  override def showError(stream: PrintStream): AnyRef = {
    tokenOpt match {
      case Some(token) =>
        val msg = s"Unexpected token ${token.symbol} in line: ${token.line}"
        stream.println(msg)
        msg
      case _ => Nil
    }

  }
}
