package parser.ast.module

import lexer._

trait ImportClause
case class ImportAlias(name:String, alias:Option[String])
case class FromCljRequire(from: String, names: List[ImportAlias]) extends ImportClause
case class FromJvmRequire(from: String, names: List[ImportAlias]) extends ImportClause
case class CljImport(name:List[ImportAlias]) extends ImportClause
case class JvmImport(name:List[ImportAlias]) extends ImportClause

object ImportClause {

  def parse(tokens: TokenStream): Option[List[ImportClause]] = {
    tokens.consumeOptionals(NL)
    var listOfImports = List.empty[ImportClause]
    while (tokens.peek(IMPORT) || tokens.peek(FROM)) {
      if (tokens.peek(IMPORT)) {
        listOfImports = parseImport(tokens) :: listOfImports
      }
      else if (tokens.peek(FROM)) {
        listOfImports = parseFromImport(tokens) :: listOfImports
      }
      tokens.consumeOptionals(NL)
    }
    if (listOfImports.isEmpty) {
      None
    }
    else {
      Some(listOfImports.reverse)
    }
  }

  def parseImport(tokens:TokenStream): ImportClause = {
    tokens.consume(IMPORT)
    if (parseTag(tokens) == ":jvm") {
      JvmImport(parseListOfAlias(tokens))
    }
    else {
      CljImport(parseListOfAlias(tokens))
    }
  }

  def parseFromImport(tokens:TokenStream): ImportClause = {
    tokens.consume(FROM)
    val tag = parseTag(tokens)
    val name = tokens.consume(classOf[ID]).value
    tokens.consume(IMPORT)
    if (tag == ":jvm") {
      FromJvmRequire(name, parseListOfAlias(tokens))
    }
    else {
      FromCljRequire(name, parseListOfAlias(tokens))
    }
  }

  def parseTag(tokens:TokenStream): String = {
    if (tokens.peek(LBRACKET)) {
      tokens.consume(LBRACKET)
      val tag = tokens.consume(classOf[ATOM]).value
      tokens.consume(RBRACKET)
      tag
    } else {
      ""
    }
  }

  def parseListOfAlias(tokens:TokenStream) : List[ImportAlias] = {
    var listOfAlias = List.empty[ImportAlias]
    val impAlias = parseImportAlias(tokens)
    listOfAlias = impAlias :: listOfAlias
    while (tokens.peek(COMMA)) {
      tokens.consume(COMMA)
      tokens.consumeOptionals(NL)
      val impAlias = parseImportAlias(tokens)
      listOfAlias = impAlias :: listOfAlias
    }
    listOfAlias.reverse
  }

  def parseImportAlias(tokens:TokenStream): ImportAlias = {
    val id = if (tokens.peek(classOf[TID])) tokens.consume(classOf[TID]).value else tokens.consume(classOf[ID]).value
    val alias = if (!tokens.peek(AS)) {
      None
    } else {
      tokens.consume(AS)
      Some(tokens.consume(classOf[ID]).value)
    }
    ImportAlias(id, alias)
  }


}
