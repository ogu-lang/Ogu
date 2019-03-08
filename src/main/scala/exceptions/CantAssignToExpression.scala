package exceptions
import java.io.PrintStream

case class CantAssignToExpression(line: Int) extends ParserException {
  override def showError(stream: PrintStream): AnyRef = {
    val msg = s"can't assign to expression in line $line"
    stream.println(msg)
    msg
  }
}
