package backend

import java.io.{File, FileInputStream, InputStream}

import interpreter.Interpreter
import lexer.Lexer
import parser._

import scala.util.{Failure, Success, Try}


case class Backend(fileNames: List[String]) {

  def compile(): Unit = {
    for (fileName <- fileNames) {
      println(s"scanning ${fileName}...")
      compileFile(fileName)
    }
  }

  def compileFile(fileName: String): AnyRef = {
    Try(new FileInputStream(new File(fileName))) match {
      case Success(inputStream) => compileFile(fileName, inputStream)
      case Failure(exception) =>
        Failure(exception)
    }
  }

  def compileFile(fileName: String, inputStream: InputStream): AnyRef = {
    val lexer = new Lexer()
    val tryScan = lexer.scan(fileName, inputStream)
    tryScan match {
      case Success(tokens) =>
        val parser = new Parser(fileName, tokens, defaultRuntime())
        val ast = parser.parse()
        Interpreter.load(ast)
      case Failure(exception) =>
        Failure(exception)
    }
  }

  def defaultRuntime(): Option[SymbolTable] = {
    val table = SymbolTable(None)
    table.add(FunctionSymbol("println!"))
    table.add(FunctionSymbol("input!"))
    return Some(table)
  }


}
