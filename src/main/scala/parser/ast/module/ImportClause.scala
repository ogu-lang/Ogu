package parser.ast.module

import lexer._

import scala.annotation.tailrec

trait ImportClause
case class FromCljRequire(from: String, names: List[ImportAlias]) extends ImportClause
case class FromJvmRequire(from: String, names: List[ImportAlias]) extends ImportClause
case class CljImport(name:List[ImportAlias]) extends ImportClause
case class JvmImport(name:List[ImportAlias]) extends ImportClause

object ImportClause {

  def parse(tokens: TokenStream): Option[List[ImportClause]] = {
    tokens.consumeOptionals(NL)
    var listOfImports = consumeListOfImportClauses(tokens, List.empty)
    if (listOfImports.isEmpty) {
      None
    }
    else {
      Some(listOfImports)
    }
  }

  @tailrec
  private def consumeListOfImportClauses(tokens: TokenStream, list: List[ImportClause]) : List[ImportClause] = {
    if (!tokens.peek(IMPORT) && !tokens.peek(FROM)) {
      list.reverse
    }
    else {
      val clause = if (tokens.peek(IMPORT)) parseImportClause(tokens) else parseFromImportClause(tokens)
      tokens.consumeOptionals(NL)
      consumeListOfImportClauses(tokens, clause :: list)
    }
  }

  private def parseImportClause(tokens:TokenStream): ImportClause = {
    tokens.consume(IMPORT)
    if (parseTag(tokens) == ":jvm") {
      JvmImport(ImportAlias.parseListOfAlias(tokens))
    }
    else {
      CljImport(ImportAlias.parseListOfAlias(tokens))
    }
  }

  private def parseFromImportClause(tokens:TokenStream): ImportClause = {
    tokens.consume(FROM)
    val tag = parseTag(tokens)
    val name = tokens.consume(classOf[ID]).value
    tokens.consume(IMPORT)
    if (tag == ":jvm") {
      FromJvmRequire(name, ImportAlias.parseListOfAlias(tokens))
    }
    else {
      FromCljRequire(name, ImportAlias.parseListOfAlias(tokens))
    }
  }

  def parseTag(tokens:TokenStream): String = {
    if (!tokens.peek(LBRACKET)) {
      ""
    } else {
      tokens.consume(LBRACKET)
      val tag = tokens.consume(classOf[ATOM]).value
      tokens.consume(RBRACKET)
      tag
    }
  }

}
