import backend.Backend

object Main {

  sealed trait Args
  case object NoArgs extends Args
  case class FileArgs(fileNames:List[String]) extends Args

  def checkArgs(args: Array[String]) : Args = {
    if (args.size == 0)
      NoArgs
    else
      FileArgs(args.toList)
  }

  val usageMessage = "ogu-parser modulo.ogu ...."

  def compileModules(fileNames: List[String]) : Unit = {
    val backend = Backend(fileNames)
    backend.compile()
  }

  def main(args: Array[String]): Unit =
    checkArgs(args) match {
      case NoArgs => println(usageMessage)
      case FileArgs(fileNames) => compileModules(fileNames)
    }


}
