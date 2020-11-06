package parser

import lexer._
import parser.ast.module.Module

class Parser(filename:String, val tokens: TokenStream) {

  def parse(): Module = {
    val nameOfModule = filename.substring(filename.lastIndexOf('/') + 1, filename.lastIndexOf('.'))
      Module.parse(tokens, nameOfModule)
  }

}
