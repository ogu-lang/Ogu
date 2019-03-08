package exceptions
import java.io.PrintStream

case class UndefinedIdentifierException(id: String, line:Int) extends ParserException {
  override def showError(stream: PrintStream): AnyRef = {
    val msg = s"Undefined id: $id at line $line"
    stream.println(msg)
    msg
  }
}
