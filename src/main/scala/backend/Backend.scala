package backend


import interpreter.Interpreter
import java.io.{File, FileInputStream, InputStream}
import lexer.Lexer
import parser._

import scala.util.{Failure, Success, Try}


object Backend {

  def run(fileNames: List[String]): Unit = {
    run(fileNames, Nil)
  }

  def run(fileNames: List[String], args: List[String]): Unit = {
    println(s"run with args = ${args}")
    for (fileName <- fileNames) {
      runFile(fileName, args)
    }
  }

  def runFromResource(fileName: String, args: List[String]) : AnyRef = {
    Try(getClass.getResourceAsStream(fileName)) match {
      case Success(inputStream) => runStream(fileName, inputStream, args)
      case Failure(exception) => Failure(exception)
    }
  }

  def runFile(fileName: String, args: List[String]): AnyRef = {
    Try(new FileInputStream(new File(fileName))) match {
      case Success(inputStream) => runStream(fileName, inputStream, args)
      case Failure(exception) => Failure(exception)
    }
  }

  def runStream(fileName: String, inputStream: InputStream, args: List[String]): AnyRef =
    Option(inputStream) match {
      case None => Failure(new NullPointerException)
      case Some(stream) =>
        val lexer = new Lexer()
        val tryScan = lexer.scan(fileName, stream)
        tryScan match {
          case Success(tokens) =>
            val parser = new Parser(fileName, tokens)
            Interpreter.load(parser.parse(), args)
          case Failure(exception) => Failure(exception)
        }
    }

}
