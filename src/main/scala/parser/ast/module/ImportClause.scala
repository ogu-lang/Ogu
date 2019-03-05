package parser.ast.module

import lexer._
import parser.UnexpectedTokenException

import scala.annotation.tailrec

trait ImportClause
case class FromCljRequire(from: String, names: List[ImportAlias]) extends ImportClause
case class FromCljRequireAll(from: String) extends ImportClause
case class FromCljRequireStatic(from: String, names: List[ImportAlias]) extends ImportClause
case class FromJvmRequire(from: String, names: List[ImportAlias]) extends ImportClause
case class FromJvmRequireAll(from: String) extends ImportClause
case class FromJvmRequireStatic(from: String, names: List[ImportAlias]) extends ImportClause
case class CljImport(name:List[ImportAlias]) extends ImportClause
case class JvmImport(name:List[ImportAlias]) extends ImportClause

object ImportClause {

  def parse(tokens: TokenStream): Option[List[ImportClause]] = {
    tokens.consumeOptionals(NL)
    val listOfImports = consumeListOfImportClauses(tokens, List.empty)
    if (listOfImports.isEmpty) {
      None
    }
    else {
      Some(listOfImports)
    }
  }

  @tailrec
  private[this] def consumeListOfImportClauses(tokens: TokenStream, list: List[ImportClause]) : List[ImportClause] = {
    if (!tokens.peek(IMPORT) && !tokens.peek(FROM)) {
      list.reverse
    }
    else {
      val clause = if (tokens.peek(IMPORT)) parseImport(tokens) else parseFrom(tokens)
      tokens.consumeOptionals(NL)
      consumeListOfImportClauses(tokens, clause :: list)
    }
  }

  private[this] def parseImport(tokens:TokenStream): ImportClause = {
    tokens.consume(IMPORT)
    parseTag(tokens) match {
      case ":jvm" => JvmImport(ImportAlias.parseListOfAlias(tokens))
      case _ => CljImport(ImportAlias.parseListOfAlias(tokens))
    }
  }

  private[this] def parseFrom(tokens:TokenStream): ImportClause = {
    tokens.consume(FROM)
    val tag = parseTag(tokens)
    val (name, isType) = {
      val token = tokens.pop()
      token match {
        case ID(id) =>
          (id, false)
        case TID(tid) => (tid, true)
        case _ => throw UnexpectedTokenException(token, tokens.tokens)
      }
    }
    tokens.consume(IMPORT)
    if (isType) {
      tag match {
        case ":jvm" => FromJvmRequireStatic(name, ImportAlias.parseListOfAlias(tokens))
        case _ =>  FromCljRequireStatic(name, ImportAlias.parseListOfAlias(tokens))
      }
    }
    else {
      if (tokens.peek(MULT)) {
        tokens.consume(MULT)
        tag match {
          case ":jvm" => FromJvmRequireAll(name)
          case _ => FromCljRequireAll(name)
        }
      } else {
        tag match {
          case ":jvm" => FromJvmRequire(name, ImportAlias.parseListOfAlias(tokens))
          case _ => FromCljRequire(name, ImportAlias.parseListOfAlias(tokens))
        }
      }
    }
  }

  def parseTag(tokens:TokenStream): String = {
    if (!tokens.peek(LBRACKET)) {
      ""
    }
    else {
      tokens.consume(LBRACKET)
      val tag = tokens.consume(classOf[ATOM]).value
      tokens.consume(RBRACKET)
      tag
    }
  }

}
