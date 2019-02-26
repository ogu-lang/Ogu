package backend

import java.io.{File, FileInputStream, InputStream}

import interpreter.Interpreter
import lexer.Lexer
import parser._

import scala.util.{Failure, Success, Try}


object Backend {

  def compile(fileNames: List[String]): Unit = {
    for (fileName <- fileNames) {
      println(s"scanning $fileName...")
      compileFile(fileName)
    }
  }

  def compileFromResource(fileName: String) : AnyRef = {
    Try(getClass.getResourceAsStream(fileName)) match {
      case Success(inputStream) => compileStream(fileName, inputStream)
      case Failure(exception) => Failure(exception)
    }
  }

  def compileFile(fileName: String): AnyRef = {
    Try(new FileInputStream(new File(fileName))) match {
      case Success(inputStream) => compileStream(fileName, inputStream)
      case Failure(exception) => Failure(exception)
    }
  }

  def compileStream(fileName: String, inputStream: InputStream): AnyRef = {
    if (inputStream == null) {
      return Failure(new NullPointerException)
    }

    val lexer = new Lexer()
    val tryScan = lexer.scan(fileName, inputStream)
    tryScan match {
      case Success(tokens) =>
        val parser = new Parser(fileName, tokens)
        val ast = parser.parse()
        Interpreter.load(ast)
      case Failure(exception) => Failure(exception)
    }
  }



}
