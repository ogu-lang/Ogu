package exceptions
import java.io.PrintStream

case class UndefinedIdentifierException(id: String) extends ParserException {
  override def showError(stream: PrintStream): AnyRef = ???
}
