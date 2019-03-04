package interpreter

import scala.collection.JavaConverters._

object Runtime {

  def banner(msg: String) : Unit = {
    println(com.github.lalyos.jfiglet.FigletFont.convertOneLine(msg))
  }

  private[this] var args: List[String] = Nil

  def setArgs(args: List[String]): Unit = {
    this.args = args
  }

  def getArgs() : clojure.lang.IPersistentList = {
    clojure.lang.PersistentList.create(this.args.asJava)
  }
}
