package lexer

import java.io.{File, FileInputStream, InputStream}

import org.joda.time.DateTime

import scala.annotation.tailrec
import scala.io.Source
import scala.util.{Failure, Success, Try}


class Lexer {

  private[this] val currentString = new StringBuilder()
  private[this] def parseMultiLineString: Boolean = currentString.nonEmpty

  def scanLine(inputLine: String, lineNum: Int, indentStack: List[Int], parenLevel: Int): (List[TOKEN], List[Int], Int) = {
    val textLine = removeComments(inputLine)
    val (str, indents, newStack) = if (parenLevel > 0) (textLine, Nil, indentStack) else scanIndentation(textLine, indentStack)
    val (tokens, newParenLevel) = splitLine(str, lineNum, parenLevel)
    val result = if (textLine.isEmpty || tokens.isEmpty || newParenLevel > 0  || parseMultiLineString)
      indents ++ tokens
    else
      indents ++ tokens ++ List(Some(NL))
    (result.flatten, newStack, newParenLevel)
  }

  private[this] def removeComments(line: String): String = {
    val commentPos = findCommentPos(line, 0, insideString = false)
    if (commentPos < 0) {
      line
    } else {
      line.substring(0, commentPos)
    }
  }

  private[this] def scanIndentation(text: String, indentStack:List[Int]): (String, List[Option[TOKEN]], List[Int]) = {
    val s = text.dropWhile(isBlank)
    val startColumn = text.length - s.length
    indentStack.headOption match {
      case None => (text, Nil, indentStack)
      case Some(pos) =>
        if (startColumn == pos)
          (s, Nil, indentStack)
        else if (startColumn > pos) {
          (s, List(Some(INDENT)), startColumn :: indentStack)
        }
        else {
          val newIndentStack = indentStack.dropWhile(p => startColumn < p && p > 0)
          (s, List.fill(indentStack.length - newIndentStack.length)(Some(DEDENT)), newIndentStack)
        }
    }
  }

  private[this] def findCommentPos(line:String, pos: Int, insideString: Boolean) : Int = {
    line.headOption match {
      case None => -1
      case Some(c) =>
        if (line.startsWith("--") && !insideString)
          pos
        else {
          c match {
            case '\\' => findCommentPos(line.drop(2), pos + 2, insideString)
            case '"' => findCommentPos(line.tail, pos + 1, !insideString)
            case _ => findCommentPos(line.tail, pos + 1, insideString)
          }
        }
    }
  }

  def splitLine(str: String, currentLine: Int, parenLevel: Int): (List[Option[TOKEN]], Int) = {
    var result = List.empty[Option[TOKEN]]
    val len = str.length
    var ini = 0
    var pos = 0

    def parseQuoted(quot: Char): String = {
      pos += 1
      while (pos < len && str(pos) != quot) {
        if (str(pos) == '\\')
          pos += 1
        pos += 1
      }
      if (pos < len)
        pos += 1
      val quotedStr = str.substring(ini, pos)
      ini = pos
      quotedStr
    }

    var newParenLevel = parenLevel
    while (pos < len) {
      str(pos) match {
        case '\"' =>
          if (pos > ini) {
            val (token, level) = strToToken(str.substring(ini, pos), currentLine, newParenLevel)
            result = token :: result
            newParenLevel = level
          }
          val (token, level) = strToToken(parseQuoted('\"'), currentLine, newParenLevel)
          result =  token :: result
          newParenLevel = level
        case '\'' =>
          if (pos == ini) {
            val (token, level) = strToToken(parseQuoted('\''), currentLine, newParenLevel)
            result = token :: result
            newParenLevel = level
          }
          else
            pos += 1

        case '.' if pos + 2 < len && (str.substring(pos, pos + 3) == "..." || str.substring(pos, pos + 3) == "..<") =>
          if (pos > ini) {
            val (token, level) = strToToken(str.substring(ini, pos), currentLine, newParenLevel)
            result = token :: result
          }
          result = OPER_MAP(str.substring(pos, pos + 3)) :: result
          ini = pos + 3
          pos += 3

        case '.' if pos + 1 < len && str.substring(pos, pos + 2) == ".." =>
          if (pos > ini) {
            val (token, level) = strToToken(str.substring(ini, pos), currentLine, newParenLevel)
            result = token  :: result
            newParenLevel = level
          }
          result = Some(DOTDOT) :: result
          ini = pos + 2
          pos += 2

        case '#' =>
          if (pos > ini) {
            val (token, level) = strToToken(str.substring(ini, pos), currentLine, parenLevel)
            result = token :: result
            newParenLevel = level
          }
          if (pos + 1 >= len) {
            pos += 1
          } else {
            str(pos + 1) match {
              case '\"' =>
                ini = pos
                pos += 1
                val (token, level) = tryParseHashTag(parseQuoted('\"'), currentLine, newParenLevel)
                result = token :: result
                newParenLevel = level
              case '/' =>
                ini = pos
                pos += 1
                val (token, level) = tryParseHashTag(parseQuoted('/'), currentLine, newParenLevel)
                result = token :: result
                newParenLevel = level
              case '{' =>
                ini = pos
                pos += 2
                val (token, level) = tryParseHashTag(str.substring(ini, pos), currentLine, newParenLevel)
                result = token :: result
                newParenLevel = level
                ini = pos
              case c if isTimeValidChar(c) =>
                ini = pos
                pos += 1
                while (pos < len && isTimeValidChar(str(pos))) {
                  pos += 1
                }
                val (token, level) = tryParseHashTag(str.substring(ini, pos), currentLine, newParenLevel)
                result = token :: result
                newParenLevel = level
                ini = pos
              case _ =>
                pos += 1
            }
          }

        case c if isBlank(c) =>
          if (pos > ini) {
            val (token, level) = strToToken(str.substring(ini, pos), currentLine, newParenLevel)
            result = token :: result
            newParenLevel = level
          }
          while (pos < len && isBlank(str(pos)))
            pos += 1
          ini = pos

        case c if isPunct(c) =>
          if (pos > ini) {
            val (token, level) = strToToken(str.substring(ini, pos), currentLine, newParenLevel)
            result = token :: result
            newParenLevel = level
          }
          ini = pos
          pos += 1
          val (token, level) = tryParseOp(str.substring(ini, pos), currentLine, newParenLevel)
          result = token :: result
          newParenLevel = level
          ini = pos

        case _ =>
          pos += 1
      }
    }
    if (ini < len) {
      val (token, level) = strToToken(str.substring(ini, pos), currentLine, parenLevel)
      result = token :: result
      (result.reverse, parenLevel)
    }
    else {
      (result.reverse, newParenLevel)
    }
  }

  private[this] def strToToken(str: String, currentLine: Int, parenLevel: Int): (Option[TOKEN], Int) = {
    if (parseMultiLineString) {
      currentString ++= str
      if (!str.endsWith("\"")) {
        (None, parenLevel)
      }
      else {
        val str = currentString.mkString
        currentString.clear()
        (Some(STRING(str)), parenLevel)
      }
    }
    else {
      str.head match {
        case '\"' if !str.endsWith("\"") =>
          currentString ++= str
          (None, parenLevel)
        case '\"' => (Some(STRING(str)), parenLevel)
        case '\'' => (Some(CHAR(str)), parenLevel)
        case '#' => tryParseHashTag(str, currentLine, parenLevel)
        case ':' if str.length > 1 && str != "::" => (Some(ATOM(str)), parenLevel)
        case _ =>
          var token = tryParseId(str, currentLine)
          token match {
            case Some(ERROR(_,_)) => token = tryParseNum(str, currentLine)
            case _ => /**/
          }
          token match {
            case Some(ERROR(_,_)) =>  tryParseOp(str, currentLine, parenLevel)
            case _ => (token, parenLevel)
          }
      }
    }
  }

  private[this] def tryParseId(str: String, currentLine: Int): Option[TOKEN] = {
    KEYWORD_MAP(str) match {
      case Some(token) =>Some(token)
      case None =>
        val s = str.takeWhile(c => !isIdentifierChar(c))
        if (s.length >= str.length) {
          Some(ERROR(currentLine, str))
        }
        else {
          val id = if (str.endsWith("...")) str.substring(0, str.length - 3) else str
          id.headOption match {
            case None => Some(ERROR(currentLine, str))
            case Some(c) if !id.contains('.') && c.isUpper => Some(TID(id))
            case _ if id.contains('.') =>
              val parts = id.split('.')
              parts.last.headOption match {
                case None => Some(ID(id))
                case Some(c) if c.isUpper => Some(TID(id))
                case _ => Some(ID(id))
              }
            case _ => Some(ID(id))
          }
        }
    }
  }

  private[this] def isIntegerValue(bd: BigDecimal): Boolean = (bd.signum == 0) || bd.scale <= 0

  private[this] def strToNumToken(str: String): TOKEN = {
    val value = BigDecimal(str)
    if (isIntegerValue(value)) {
      value match {
        case i if i < Int.MaxValue => INT(value.toIntExact)
        case l if l < Long.MaxValue => LONG(value.toLongExact)
        case _ => BIGINT(value.toBigInt())
      }
    }
    else {
      value match {
        case d if d.isExactDouble => DOUBLE(value.toDouble)
        case l if l.isValidLong => LONG(value.toLongExact)
        case b if b.isBinaryFloat => FLOAT(value.toFloat)
        case _ => BIGDECIMAL(value)
      }
    }
  }

  private[this] def tryParseNum(str: String, currentLine: Int): Option[TOKEN] = {
    Try(strToNumToken(str)) match {
      case Success(token) => Some(token)
      case Failure(_) => Some(ERROR(currentLine, str))
    }
  }

  private[this] def tryParseOp(str: String, currentLine: Int, parenLevel: Int): (Option[TOKEN], Int) = {
    OPER_MAP(str) match {
      case Some(token) =>
        val newLevel = token match {
          case LPAREN | LBRACKET | LCURLY | HASHLCURLY => parenLevel + 1
          case RPAREN | RBRACKET | RCURLY => parenLevel - 1
          case _ => parenLevel
        }
        (Some(token), newLevel)
      case None =>
        (Some(ERROR(currentLine, str)), parenLevel)
    }
  }

  private[this] def tryParseHashTag(str: String, currentLine: Int, parenLevel: Int): (Option[TOKEN], Int) = {
    str match {
      case "#{" =>
        (Some(HASHLCURLY), parenLevel + 1)
      case _ if str.startsWith("#\"") =>
        (Some(FSTRING(str.substring(1))), parenLevel)
      case _ if str.startsWith("#/") =>
        (Some(REGEXP(str.substring(2, str.length - 1))), parenLevel)
      case _ =>
        Try(ISODATETIME(new DateTime(str.substring(1)))) match {
          case Success(token) => (Some(token), parenLevel)
          case Failure(_) => (Some(ERROR(currentLine, str)), parenLevel)
        }
    }
  }

  val opChars: Set[Char] = Set('@', '~', '$', '+', '-', '*', '/', '%', '^', '|', '&', '=', '<', '>', '(', ')', '[', ']',
    '{', '}', '!', '?', '.', ':', ';', ',', '\\')

  val punctChars: Set[Char] = Set(',', '(', ')', '[', ']', '{', '}', '\\')

  def isBlank(c: Char): Boolean = Character.isWhitespace(c)

  def isIdentifierChar(c: Char): Boolean =
    c match {
      case '_' => true
      case _ => Character.isAlphabetic(c)
    }

  def isPunct(c: Char): Boolean = punctChars contains c

  def isNumericChar(c: Char): Boolean = Character.isDigit(c)

  def isTimeValidChar(c: Char): Boolean = {
    c match {
      case '-' | ':' => true
      case _ => Character.isDigit(c) || Character.isUpperCase(c)
    }
  }

  def isOpChar(c: Char): Boolean = opChars contains c

  @tailrec
  private[this] def mapLines(lines: List[(String, Int)], indentStack: List[Int], tokens: List[List[TOKEN]], parenLevel: Int) : (List[TOKEN], List[Int]) = {
    lines.headOption match {
      case None => (tokens.reverse.flatten, indentStack)
      case Some((text, line)) =>
        val (lineTokens, newIndentStack, newParenLevel) = scanLine(text, line, indentStack, parenLevel)
        mapLines(lines.tail, newIndentStack, lineTokens :: tokens, newParenLevel)
    }
  }

  def scanLines(lines: Iterator[(String, Int)]): TokenStream = {
    val (tokens, indentStack) = mapLines(lines.toList, List(0), Nil, parenLevel = 0)
    val result = if (indentStack.isEmpty) {
      tokens
    } else {
      val newIndentStack = indentStack.dropWhile(p => p > 0)
      List.fill(indentStack.length - newIndentStack.length)(DEDENT) ++ tokens.reverse
    }
    TokenStream(result.reverse)
  }

  def scanFromResources(filename: String): Try[TokenStream] = {
    scan(filename, getClass.getResourceAsStream(filename))
  }

  def scan(filename: String, fileStream: InputStream): Try[TokenStream] = {
    Try(Source.fromInputStream(fileStream)) match {
      case Failure(e) =>
        Failure(CantScanFileException(filename, e))
      case Success(rdr) =>
        Success(scanLines(rdr.getLines.zipWithIndex.filter { case (s, _) => s.nonEmpty }))
    }
  }

  def scan(filename: String): Try[TokenStream] = {
    scan(filename, new FileInputStream(new File(filename)))
  }

  def scanString(code: String): Try[TokenStream] = {
    Try {
      scanLines(code.split('\n').zipWithIndex.filter { case (s, _) => s.nonEmpty }.toIterator)
    }
  }

}
