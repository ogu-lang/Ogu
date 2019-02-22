package lexer

import java.io.{File, FileInputStream, InputStream}
import org.joda.time.DateTime
import scala.io.Source
import scala.util.{Failure, Success, Try}


class Lexer {

  private[this] var indentStack: List[Int] = List(0)

  private[this] var parenLevel = 0
  private[this] val currentString = new StringBuilder()
  private[this] var parseMultiLineString = false

  def scanLine(inputLine: String, lineNumber: Int): List[TOKEN] = {
    // remove comments
    val withComments = inputLine.split("--")
    val textLine = withComments.head
    val len = textLine.length
    val (iniCol, indents) = scanIndentation(textLine, lineNumber, len)
    val tokens = if (iniCol >= len) {
      indents
    }
    else {
      val str = textLine.substring(iniCol)
      val rest = splitLine(str, lineNumber)
      indents ++ rest
    }
    val result = if (len > 0 && tokens.nonEmpty && parenLevel == 0 && !parseMultiLineString)
      tokens ++ List(NL)
    else
      tokens
    result.filter(t => t != SKIP)
  }

  def splitLine(str: String, currentLine: Int): List[TOKEN] = {

    var result = List.empty[TOKEN]
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

    while (pos < len) {
      str(pos) match {
        case '\"' =>
          if (pos > ini)
            result = strToToken(str.substring(ini, pos), currentLine) :: result
          result = strToToken(parseQuoted('\"'), currentLine) :: result
        case '\'' =>
          if (pos == ini)
            result = strToToken(parseQuoted('\''), currentLine) :: result
          else
            pos += 1

        case '.' if pos + 2 < len && (str.substring(pos, pos + 3) == "..." || str.substring(pos, pos + 3) == "..<") =>
          if (pos > ini) result = strToToken(str.substring(ini, pos), currentLine) :: result
          result = OPER_MAP(str.substring(pos, pos + 3)).get :: result
          ini = pos + 3
          pos += 3

        case '.' if pos + 1 < len && str.substring(pos, pos + 2) == ".." =>
          if (pos > ini) result = strToToken(str.substring(ini, pos), currentLine) :: result
          result = DOTDOT :: result
          ini = pos + 2
          pos += 2

        case '#' =>
          if (pos > ini) result = strToToken(str.substring(ini, pos), currentLine) :: result
          if (pos + 1 >= len) {
            pos += 1
          } else {
            str(pos + 1) match {
              case '\"' =>
                ini = pos
                pos += 1
                result = tryParseHashTag(parseQuoted('\"'), currentLine) :: result
              case '/' =>
                ini = pos
                pos += 1
                result = tryParseHashTag(parseQuoted('/'), currentLine) :: result
              case '{' =>
                ini = pos
                pos += 2
                result = tryParseHashTag(str.substring(ini, pos), currentLine) :: result
                ini = pos
              case c if isTimeValidChar(c) =>
                ini = pos
                pos += 1
                while (pos < len && isTimeValidChar(str(pos))) {
                  pos += 1
                }
                result = tryParseHashTag(str.substring(ini, pos), currentLine) :: result
                ini = pos
              case _ =>
                pos += 1
            }
          }

        case c if isBlank(c) =>
          if (pos > ini)
            result = strToToken(str.substring(ini, pos), currentLine) :: result
          while (pos < len && isBlank(str(pos)))
            pos += 1
          ini = pos

        case c if isPunct(c) =>
          if (pos > ini) result = strToToken(str.substring(ini, pos), currentLine) :: result
          ini = pos
          pos += 1
          result = tryParseOp(str.substring(ini, pos), currentLine) :: result
          ini = pos

        case _ =>
          pos += 1
      }
    }
    if (ini < len) {
      result = strToToken(str.substring(ini, pos), currentLine) :: result
    }
    result.reverse
  }

  private[this] def strToToken(str: String, currentLine: Int): TOKEN = {
    if (parseMultiLineString) {
      currentString ++= str
      if (!str.endsWith("\"")) {
        SKIP
      }
      else {
        parseMultiLineString = false
        STRING_LITERAL(currentString.mkString)
      }
    }
    else {
      str.head match {
        case '\"' if !str.endsWith("\"") =>
          currentString.clear()
          currentString ++= str
          this.parseMultiLineString = true
          SKIP
        case '\"' => STRING_LITERAL(str)
        case '\'' => CHAR_LITERAL(str)
        case '#' => tryParseHashTag(str, currentLine)
        case ':' if str.length > 1 && str != "::" => ATOM(str)
        case _ =>
          var token = tryParseId(str, currentLine)
          if (token.isInstanceOf[LEXER_ERROR])
            token = tryParseNum(str, currentLine)
          if (token.isInstanceOf[LEXER_ERROR])
            token = tryParseOp(str, currentLine)
          token
      }
    }
  }


  def scanIndentation(text: String, currentLine: Int,  len: Int): (Int, List[TOKEN]) =
    if (parenLevel > 0) {
      (0, List.empty[TOKEN])
    }
    else {
      val s = text.takeWhile(isBlank)
      val startColumn = s.length
      if (startColumn == indentStack.head)
        (startColumn, List.empty[TOKEN])
      else if (startColumn > indentStack.head) {
        indentStack = startColumn :: indentStack
        (startColumn, List(INDENT))
      }
      else {
        val n = indentStack.length
        indentStack = indentStack.dropWhile(p => startColumn < p && p > 0)
        val result = List.fill(n-indentStack.length)(DEDENT)
        (startColumn, result)
      }
    }


  def tryParseId(str: String, currentLine: Int): TOKEN = {
    KEYWORD_MAP(str) match {
      case Some(token) => token
      case None =>
        //var isValidId = false
        val s = str.takeWhile(c => !isIdentifierChar(c))
        val isValidId = s.length < str.length
        if (!isValidId) {
          LEXER_ERROR(currentLine, str)
        } else {
          val id = if (str.endsWith("...")) {
            str.substring(0, str.length - 3)
          } else {
            str
          }
          if (id.contains('.')) {
            val parts = id.split('.')
            if (parts.last.head.isUpper) {
              return TID(id)
            }
          }
          else if (id.head.isUpper) {
            return TID(id)
          }
          ID(id)
        }
    }
  }

  private[this] def isIntegerValue(bd: BigDecimal): Boolean = (bd.signum == 0) || bd.scale <= 0

  private[this] def strToNumToken(str: String) : TOKEN = {
    val value = BigDecimal(str)
    if (isIntegerValue(value)) {
      if (value < Int.MaxValue)
        INT_LITERAL(value.toIntExact)
      else if (value < Long.MaxValue)
        LONG_LITERAL(value.toLongExact)
      else
        BIGINT_LITERAL(value.toBigInt())
    }
    else {
      if (value.isExactDouble)
        DOUBLE_LITERAL(value.toDouble)
      else if (value.isValidLong)
        LONG_LITERAL(value.toLongExact)
      else if (value.isBinaryFloat)
        FLOAT_LITERAL(value.toFloat)
      else
        BIGDECIMAL_LITERAL(value)
    }
  }

  private[this] def tryParseNum(str: String, currentLine: Int): TOKEN = {
    Try(strToNumToken(str)) match {
      case Success(token) => token
      case Failure(_) => LEXER_ERROR(currentLine, str)
    }
  }

  private[this] def tryParseOp(str: String, currentLine: Int): TOKEN =
    OPER_MAP(str) match {
      case Some(token) =>
        token match {
          case LPAREN => parenLevel += 1
          case LBRACKET => parenLevel += 1
          case LCURLY => parenLevel += 1
          case HASHLCURLY => parenLevel += 1
          case RPAREN => parenLevel -= 1
          case RBRACKET => parenLevel -= 1
          case RCURLY => parenLevel -= 1
          case _ => // nothing
        }
        token
      case None =>
        LEXER_ERROR(currentLine, str)
    }

  private[this] def tryParseHashTag(str: String, currentLine: Int): TOKEN =
    str match {
      case "#{" =>
        parenLevel += 1
        HASHLCURLY
      case _ if str.startsWith("#\"") =>
        FSTRING_LITERAL(str.substring(1))
      case _ if str.startsWith("#/") =>
        REGEXP_LITERAL(str.substring(2, str.length - 1))
      case _ =>
        Try(ISODATETIME_LITERAL(new DateTime(str.substring(1)))) match {
          case Success(token) => token
          case Failure(_) => LEXER_ERROR(currentLine, str)
        }
    }

  val opChars: Set[Char] = Set('@', '~', '$', '+', '-', '*', '/', '%', '^', '|', '&', '=', '<', '>', '(', ')', '[', ']',
    '{', '}', '!', '?', '.', ':', ';', ',', '\\')

  val punctChars: Set[Char] = Set(',', '(', ')', '[', ']', '{', '}', '\\')

  def isBlank(c: Char): Boolean = Character.isWhitespace(c)

  def isIdentifierChar(c: Char): Boolean = Character.isAlphabetic(c) || c == '_'

  def isPunct(c: Char): Boolean = punctChars contains c

  def isNumericChar(c: Char): Boolean = Character.isDigit(c)

  def isTimeValidChar(c: Char): Boolean = c == '-' || c == ':' || Character.isDigit(c) || Character.isUpperCase(c)

  def isOpChar(c: Char): Boolean = opChars contains c

  def scanLines(lines: Iterator[(String, Int)]): TokenStream = {
    val tokens = lines.flatMap {
      case (text,line) =>
        scanLine(text, line)
    }.toList
    val result = if (indentStack.isEmpty) {
      tokens
    } else {
        val n = indentStack.length
        indentStack = indentStack.dropWhile(p => p > 0)
        List.fill(n - indentStack.length)(DEDENT) ++ tokens.reverse
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
        Success(scanLines(rdr.getLines.zipWithIndex.filter(_._1.length > 0)))
    }
  }

  def scan(filename: String): Try[TokenStream] = {
    scan(filename, new FileInputStream(new File(filename)))
  }

  def scanString(code: String): Try[TokenStream] = {
    Try {
      scanLines(code.split('\n').zipWithIndex.filter(_._1.length > 0).toIterator)
    }
  }

}
