package exceptions

import java.io.PrintStream

import lexer.SYMBOL

case class InvalidLambdaExpression(token: SYMBOL, line: Int) extends ParserException {
  override def showError(stream: PrintStream): AnyRef = {
    val msg = s"Invalid lambda expression at line: $line, token: $token"
    stream.println(msg)
    msg
  }
}
