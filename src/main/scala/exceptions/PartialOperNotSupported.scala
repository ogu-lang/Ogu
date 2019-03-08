package exceptions

import java.io.PrintStream

import lexer.TOKEN

case class PartialOperNotSupported(oper:TOKEN) extends ParserException {
  override def showError(stream: PrintStream): AnyRef = {
    val msg = s"partial operator $oper not supportd"
    stream.println(msg)
    msg
  }
}
