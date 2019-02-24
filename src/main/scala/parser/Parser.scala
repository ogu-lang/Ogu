package parser

import lexer._
import parser.ast.module.Module

class Parser(filename:String, val tokens: TokenStream, defaultSymbolTable: Option[SymbolTable]) {


  def parse(): LangNode = {
    val nameOfModule = filename.substring(filename.lastIndexOf('/') + 1, filename.lastIndexOf('.'))
    Module.parse(tokens, nameOfModule)
  }

}
