package lexer

import java.io.{File, FileInputStream, InputStream}

import org.joda.time.DateTime

import scala.collection.mutable
import scala.collection.mutable.Stack
import scala.io.Source
import scala.util.{Failure, Success, Try}


class Lexer {

  val encoding = "UTF-8" // files must be encoded in UTF-8
  val bufferSize = 4096
  var indentStack: mutable.Stack[Int] = Stack[Int](0)

  var currentLine = 0
  var currentColumn = 0
  var parenLevel = 0
  var currentString = ""
  var parseMultiLineString = false

  def scanLine(line: String): List[TOKEN] = {
    currentLine += 1
    var tokens = List.empty[TOKEN]
    val withComments = line.split("--")
    val text = withComments.head
    val len = text.length
    currentColumn = 0
    tokens = scanIndentation(text, len, tokens)
    if (currentColumn < len) {
      val str = text.substring(currentColumn)
      val rest = splitLine(str).map(strToToken)
      tokens = tokens.filter(t => t != SKIP).reverse ++ rest
    }
    val result = if (len > 0 && tokens.nonEmpty && parenLevel == 0 && !parseMultiLineString)
      tokens ++ List(NL)
    else
      tokens
    result.filter(t => t != SKIP)
  }

  def splitLine(str: String) : List[String] = {

    var result = List.empty[String]
    val len = str.length
    var ini = 0
    var pos = 0

    def parseQuoted(quot:Char) : Unit = {

      pos += 1
      while (pos < len && str(pos) != quot) {
        if (str(pos) == '\\')
          pos += 1
        pos += 1
      }
      if (pos < len)
        pos += 1
      result = str.substring(ini, pos) :: result
      ini = pos
    }


    while (pos < len) {
      if (isBlank(str(pos))) {
        if (pos > ini)
          result = str.substring(ini, pos) :: result
        while (pos < len && isBlank(str(pos)))
          pos += 1
        ini = pos
      }
      else if (isPunct(str(pos))) {
        if (pos > ini)
          result = str.substring(ini, pos) :: result
        ini = pos
        pos += 1
        result = str.substring(ini, pos) :: result
        ini = pos
      }
      else if (str(pos) == '#') {
        if (pos > ini)
          result = str.substring(ini, pos) :: result
        if (pos + 1 < len && str(pos+1) == '\"') {
          ini = pos
          pos = pos + 1
          parseQuoted('\"')
        }
        else if (pos + 1 < len && str(pos+1) == '/') {
          ini = pos
          pos = pos + 1
          parseQuoted('/')
        }
        else if (pos + 1 < len && isTimeValidChar(str(pos+1))) {
          ini = pos
          pos = pos + 1
          while (pos < len && isTimeValidChar(str(pos))) {
            pos += 1
          }
          result = str.substring(ini, pos) :: result
          ini = pos
        }
        else {
          pos += 1
        }
      }
      else if (str(pos) == '\"') {
        if (pos > ini)
          result = str.substring(ini, pos) :: result
        parseQuoted('\"')
      }
      else if (str(pos) == '\'') {
        if (pos == ini)
          parseQuoted('\'')
        else
          pos += 1
      }
      else {
        pos += 1
      }
    }
    if (ini < len) {
      result = str.substring(ini, pos) :: result
    }
    result.reverse
  }

  def strToToken(str: String) : TOKEN = {
    if (str.head == '\"') {
      if (!str.endsWith("\"")) {
        println(s"PARSE MULTILINE STR (${str})")
        this.currentString = str
        this.parseMultiLineString = true
        return SKIP
      }
      return STRING_LITERAL(str)
    }
    if (parseMultiLineString) {
      this.currentString += str
      if (str.endsWith("\"")) {
        parseMultiLineString = false
        return STRING_LITERAL(currentString)
      }
      return SKIP
    }
    if (str.head == '\'')
      return CHAR_LITERAL(str)
    if (str.head == '#')
      return tryParseHashTag(str)
    if (str.head == ':' && str != "::")
      return ATOM(str)
    var token = tryParseId(str)
    if (token.isInstanceOf[LEXER_ERROR])
      token = tryParseNum(str)
    if (token.isInstanceOf[LEXER_ERROR])
        token = tryParseOp(str)
    token
  }


  def scanIndentation(text: String, len: Int, tokens: List[TOKEN]) : List[TOKEN] = {
    if (parenLevel > 0) {
      return tokens
    }
    while (currentColumn < len && isBlank(text(currentColumn)))
      currentColumn += 1
    if (currentColumn == indentStack.top)
      tokens
    else if (currentColumn > indentStack.top) {
      indentStack.push(currentColumn)
      INDENT :: tokens
    }
    else {
      indentStack.pop()
      var result = DEDENT :: tokens
      while (currentColumn < indentStack.top && indentStack.top > 0) {
        indentStack.pop()
        result = DEDENT :: result
      }
      result
    }
  }


  def tryParseId(str: String) : TOKEN = {
    str match  {
      case "contains" => CONTAINS
      case "def" => DEF
      case "do" => DO
      case "elif" => ELIF
      case "else" => ELSE
      case "false" => BOOL_LITERAL(false)
      case "for" => FOR
      case "if" => IF
      case "in" => IN
      case "lazy" => LAZY
      case "let" => LET
      case "loop" => LOOP
      case "module" => MODULE
      case "otherwise" => OTHERWISE
      case "priv" => PRIVATE
      case "recur" => RECUR
      case "repeat" => REPEAT
      case "set" => SET
      case "then" => THEN
      case "true" => BOOL_LITERAL(true)
      case "until" => UNTIL
      case "var" => VAR
      case "val" =>
        println("deprecated keyword: val")
        LET
      case "when" => WHEN
      case "where" => WHERE
      case "while" => WHILE
      case "with" => WITH
      case _ =>
        var isValidId = false
        var pos = 0
        val len = str.length
        while (pos < len && !isValidId) {
          isValidId = isIdentifierChar(str(pos))
          pos += 1
        }
        if (isValidId) {
          return ID(str)
        }
        LEXER_ERROR(currentLine, str)
    }
  }

  def tryParseNum(str: String) : TOKEN = {
    try {
      val value = BigDecimal(str)
      if (isIntegerValue(value)) {
        if (value < Int.MaxValue)
          return INT_LITERAL(value.toIntExact)
        else if (value < Long.MaxValue)
          return LONG_LITERAL(value.toLongExact)
        else
          return BIGINT_LITERAL(value.toBigInt())
      }
      if (value.isExactDouble)
        return DOUBLE_LITERAL(value.toDouble)
      else if (value.isValidLong)
        return LONG_LITERAL(value.toLongExact)
      else if (value.isBinaryFloat)
        return FLOAT_LITERAL(value.toFloat)
      BIGDECIMAL_LITERAL(value)
    }
    catch {
      case ex: Throwable =>
        LEXER_ERROR(currentLine, str)
    }
  }

  private def isIntegerValue(bd: BigDecimal) = (bd.signum == 0) || bd.scale <= 0

  def tryParseOp(str: String) : TOKEN = {
    var pos = 0
    var oper = ""
    val len = str.length
    while (pos < len && isOpChar(str(pos))) {
      oper += str(pos)
      pos += 1
    }
    if (pos < len)
      return LEXER_ERROR(currentLine, str)
    oper match {
      case "&&" => AND
      case "&" => ANDB
      case "@" => ARROBA
      case "->" => ARROW
      case "=" => ASSIGN
      case "<-" => BACK_ARROW
      case "," => COMMA
      case "::" => CONS
      case "/" => DIV
      case "/=" => DIV_ASSIGN
      case "$" => DOLLAR
      case "." => DOT
      case ".." => DOTDOT
      case "..<" => DOTDOTLESS
      case "..." => DOTDOTDOT
      case "!>" => DOTO
      case "<!" => DOTO_BACK
      case "==" => EQUALS
      case ">=" => GE
      case ">" => GT
      case "|" => GUARD
      case "\\" => LAMBDA
      case "<=" => LE
      case "[" =>
        parenLevel += 1
        LBRACKET
      case "{" =>
        parenLevel += 1
        LCURLY
      case "(" =>
        parenLevel += 1
        LPAREN
      case "<" => LT
      case "=~" => MATCHES
      case "-" => MINUS
      case "<->" => MINUS_BIG
      case "-=" => MINUS_ASSIGN
      case "%" => MOD
      case "%=" => MOD_ASSIGN
      case "*" => MULT
      case "<*>" => MULT_BIG
      case "*=" => MULT_ASSIGN
      case "=/=" => NOT_EQUALS
      case "!~" => NOT_MATCHES
      case "||" => OR
      case "<|" => PIPE_LEFT
      case "|<" => PIPE_LEFT_FIRST_ARG
      case "|>" => PIPE_RIGHT
      case ">|" => PIPE_RIGHT_FIRST_ARG
      case "+" => PLUS
      case "<+>" => PLUS_BIG
      case "+=" => PLUS_ASSIGN
      case "++" => PLUS_PLUS
      case "^" => POW
      case "]" =>
        parenLevel -= 1
        RBRACKET
      case "}" =>
        parenLevel -= 1
        RCURLY
      case ")" =>
        parenLevel -= 1
        RPAREN
      case _ => LEXER_ERROR(currentLine, str)
    }
  }

  def tryParseHashTag(str: String) : TOKEN = {
    val len = str.length
    if (len > 1 && str(1) == '\"')
      return FSTRING_LITERAL(str.substring(1))
    if (len > 1 && str(1) == '/')
      return REGEXP_LITERAL(str.substring(1))
    try {
       ISODATETIME_LITERAL(new DateTime(str.substring(1)))
    }
    catch {
      case _ : Throwable => LEXER_ERROR(currentLine, str)
    }
  }

  val opChars: Set[Char] = Set('@', '~', '$', '+','-','*', '/', '%', '^', '|', '&', '=', '<', '>', '(', ')', '[', ']',
    '{', '}', '!', '?', '.', ':', ';', ',', '\\')

  val punctChars: Set[Char] = Set(',', '(', ')', '[', ']', '{', '}', '\\')

  def isBlank(c: Char) : Boolean = Character.isWhitespace(c)

  def isIdentifierChar(c: Char) : Boolean = Character.isAlphabetic(c) || c == '_'

  def isPunct(c: Char) : Boolean = punctChars contains c

  def isNumericChar(c: Char) : Boolean = Character.isDigit(c)

  def isTimeValidChar(c: Char) : Boolean = c == '-' || c == ':' || Character.isDigit(c) || Character.isUpperCase(c)

  def isOpChar(c: Char) : Boolean = opChars contains c

  def scanLines(lines: Iterator[String]): TokenStream = {
    var result = lines.flatMap { text =>
        scanLine(text)
    }.toList.reverse
    if (indentStack.nonEmpty) {
      while (indentStack.nonEmpty && indentStack.top > 0) {
        result = DEDENT :: result
        indentStack.pop()
      }
    }
    new TokenStream(result.reverse)
  }

  def scanFromResources(filename: String) : Try[TokenStream] = {
    scan(filename, getClass.getResourceAsStream(filename))
  }

  def scan(filename: String, fileStream: InputStream) : Try[TokenStream] = {
    Try(Source.fromInputStream(fileStream)) match {
      case Failure(e) =>
        Failure(CantScanFileException(filename, e))
      case Success(rdr) =>
        Success(scanLines(rdr.getLines))
    }
  }

  def scan(filename:String) : Try[TokenStream] = {
    scan(filename, new FileInputStream(new File(filename)))
  }

  def scanString(code:String) : Try[TokenStream] = {
    Try {
      scanLines(code.split('\n').toIterator)
    }
  }

}
