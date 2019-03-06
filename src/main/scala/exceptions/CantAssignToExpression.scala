package exceptions
import java.io.PrintStream

case class CantAssignToExpression() extends ParserException {
  override def showError(stream: PrintStream): AnyRef = ???
}
