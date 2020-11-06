package interpreter

import scala.collection.JavaConverters._

object Runtime {

  def banner(msg: String) : Unit = {
    println(com.github.lalyos.jfiglet.FigletFont.convertOneLine(msg))
  }

}
