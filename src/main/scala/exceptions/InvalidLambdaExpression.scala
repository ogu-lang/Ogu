package exceptions

import java.io.PrintStream

import lexer.TOKEN

case class InvalidLambdaExpression(token: TOKEN, line: Int) extends ParserException {
  override def showError(stream: PrintStream): AnyRef = {
    val msg = s"Invalid lambda expression at line: $line, token: $token"
    stream.println(msg)
    msg
  }
}
