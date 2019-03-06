package exceptions

import java.io.PrintStream

trait ParserException extends Throwable {

  def showError(stream: PrintStream): AnyRef

}
