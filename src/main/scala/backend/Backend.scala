package backend

import exceptions.ParserException
import interpreter.{Interpreter, Runtime}
import java.io.{File, FileInputStream, InputStream}
import lexer.Lexer
import parser._
import scala.util.{Failure, Success, Try}

object Backend {

  val version = "Ogu compiler version 0.2.5 (Ferrilo)"

  def usage() : Unit = {
    val usageMessage = """
      |       OLA AMIKO MIO DE MI
      |
      |Usage: ogu [options] modules... [-- args...]
      |
      |Options
      |-p, --print      Print Clojure Code
      |-n, --no-banner  Don't Show Ogu akarru banner
      |-h, --help       Show Usage
    """.stripMargin
    println(usageMessage)
  }

  def run(options: Options): Unit = {
    if (options.banner) {
      akarru()
    }
    if (options.usage) {
      usage()
    }
    else {
      for (fileName <- options.files) {
        runFile(fileName, options)
      }
    }
  }

  def runFromResource(fileName: String, options: Options) : AnyRef = {
    Try(getClass.getResourceAsStream(fileName)) match {
      case Success(inputStream) => runStream(fileName, inputStream, options)
      case Failure(exception) => Failure(exception)
    }
  }

  def runFile(fileName: String, options: Options): AnyRef = {
    Try(new FileInputStream(new File(fileName))) match {
      case Success(inputStream) => runStream(fileName, inputStream, options)
      case Failure(exception) => Failure(exception)
    }
  }

  def runStream(fileName: String, inputStream: InputStream, options: Options): AnyRef =
    Option(inputStream) match {
      case None => Failure(new NullPointerException)
      case Some(stream) =>
        val lexer = new Lexer()
        val tryScan = lexer.scan(fileName, stream)
        tryScan match {
          case Success(tokens) =>
            val parser = new Parser(fileName, tokens)
            val tryAst = Try(parser.parse())
            tryAst match {
              case Success(ast) => Interpreter.load(ast, options)
              case Failure(exception) =>
                exception match {
                  case err:ParserException => err.showError(System.err)
                }
            }
          case Failure(exception) => Failure(exception)
          case _ => Nil
        }
    }

  private[this] def akarru() : Unit = {
    Runtime.banner(msg="akarrú")
    println(version)
  }

}
