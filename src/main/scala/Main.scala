import backend.{Backend, NoOptions, Options}

object Main {


  def main(args: Array[String]): Unit =
    Options.parse(args.toList) match {
      case NoOptions => Backend.usage()
      case options:Options => Backend.run(options)
    }

}
