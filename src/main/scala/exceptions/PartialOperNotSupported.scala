package exceptions

import java.io.PrintStream

import lexer.SYMBOL

case class PartialOperNotSupported(oper:SYMBOL) extends ParserException {
  override def showError(stream: PrintStream): AnyRef = {
    val msg = s"partial operator $oper not supportd"
    stream.println(msg)
    msg
  }
}
