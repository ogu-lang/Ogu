package backend

sealed trait OPT
object NoOptions extends OPT
case class Options(banner: Boolean, print: Boolean, usage: Boolean, files: List[String], args: List[String]) extends OPT

object EmptyOptions extends Options(false, false, false, Nil, Nil)

object Options {

  def parse(args: List[String]) : OPT = {
    args match {
      case Nil => NoOptions
      case _ =>
        val init = args.takeWhile(a => !a.equalsIgnoreCase("--"))
        val rest = args.drop(init.length)
        parse(init, rest.dropWhile(a => a.equalsIgnoreCase("--")))
    }
  }

  private[this] def parse(args: List[String], rest: List[String]) : Options = {
    parse(args, Options(true, false, false, Nil, rest))
  }

  private[this] def parse(args: List[String], options: Options) : Options = {
    args.headOption match {
      case None => options.copy(files = options.files.reverse)
      case Some(value) =>
        value match {
          case "-n" | "--no-banner" => parse(args.tail, options.copy(banner = false))
          case "-p" | "--print" => parse(args.tail, options.copy(print = true))
          case "-h" | "--help" => parse(args.tail, options.copy(usage = true))
          case _ => parse(args.tail, options.copy(files = value :: options.files))
        }
    }
  }

}