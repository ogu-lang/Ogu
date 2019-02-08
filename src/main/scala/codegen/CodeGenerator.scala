package codegen

import interpreter.Interpreter.toClojure
import parser._

trait CodeGenerator {
  def mkString() : String
}

