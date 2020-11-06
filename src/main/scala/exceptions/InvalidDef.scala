package exceptions
import java.io.PrintStream

case class InvalidDef(line:Int) extends ParserException {
  override def showError(stream: PrintStream): AnyRef = {
    val msg = "Invalid definition at line $line"
    stream.println(msg)
    msg
  }
}
