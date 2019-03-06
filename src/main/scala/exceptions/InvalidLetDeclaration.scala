package exceptions
import java.io.PrintStream

case class InvalidLetDeclaration(message: String) extends ParserException {
  override def showError(stream: PrintStream): AnyRef = ???
}
