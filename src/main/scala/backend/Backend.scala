package backend

import java.io.File

import clojure.java.api.Clojure
import interpreter.Interpreter
import lexer.Lexer
import parser._

import scala.io.Source
import scala.util.{Failure, Success}


case class Backend(fileNames: List[String]) {

  def compile(): Unit = {
    for (fileName <- fileNames) {
      println(s"scanning ${fileName}...")
      val lexer = new Lexer()
      val tryScan = lexer.scan(fileName)
      tryScan match {
        case Success(tokens) =>
          val parser = new Parser(fileName, tokens, defaultRuntime())
          val ast = parser.parse()
          Interpreter.load(ast)
        case Failure(exception) =>
          println(s"Error léxico: ${exception.getMessage}")
      }
    }


    def defaultRuntime() : Option[SymbolTable] = {
      val table = SymbolTable(None)
      table.add( FunctionSymbol("println!"))
      table.add( FunctionSymbol("input!"))
      return Some(table)
    }


  }

}
