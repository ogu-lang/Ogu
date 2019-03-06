package exceptions
import java.io.PrintStream

case class InvalidDef() extends ParserException {
  override def showError(stream: PrintStream): AnyRef = ???
}
