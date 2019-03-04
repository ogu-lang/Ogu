import backend.Backend

object Main {

  sealed trait Args
  case object NoArgs extends Args
  case class FileArgs(fileNames:List[String]) extends Args
  case class FileAndArgs(fileNames:List[String], args: List[String]) extends Args

  def parseArgs(args: List[String]) : Args = {
    args match {
      case Nil => NoArgs
      case _ =>
        val files = args.takeWhile(a => !a.equalsIgnoreCase("--"))
        val rest = args.drop(files.length)
        rest match {
          case Nil => FileArgs(files)
          case _ => FileAndArgs(files, rest.tail)
        }
    }
  }

  val usageMessage = "ogu-parser modulo.ogu ...."

  def main(args: Array[String]): Unit =
    parseArgs(args.toList) match {
      case NoArgs => println(usageMessage)
      case FileArgs(fileNames) => Backend.run(fileNames)
      case FileAndArgs(fileNames, runArgs) => Backend.run(fileNames, runArgs)
    }


}
