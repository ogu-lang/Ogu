package lexer

import java.io.{File, FileInputStream, InputStream}
import org.joda.time.DateTime
import scala.annotation.tailrec
import scala.io.Source
import scala.util.{Failure, Success, Try}

class Lexer {

  type OptToken = Option[TOKEN]
  type TokList = List[TOKEN]
  type OptTokList = List[Option[TOKEN]]
  type IntList = List[Int]

  private[this] val currentString = new StringBuilder()
  private[this] def parseMultiLineString: Boolean = currentString.nonEmpty

  def scanLine(line: String, lineNum: Int, indentStack: IntList, parenLevel: Int): (TokList, IntList, Int) = {
    val text = removeComments(line)
    val (str, indents, newStack) = if (parenLevel > 0) (text, Nil, indentStack) else scanIndentation(text, indentStack)
    val (tokens, newParenLevel) = splitLine(str, lineNum, parenLevel)
    val result = if (text.isEmpty || tokens.isEmpty || newParenLevel > 0  || parseMultiLineString)
      indents ++ tokens
    else
      indents ++ tokens ++ List(Some(NL))
    (result.flatten, newStack, newParenLevel)
  }

  private[this] def removeComments(line: String): String = {
    line.substring(0, findCommentPos(line, pos = 0, insideString = false))
  }

  private[this] def scanIndentation(text: String, indentStack: IntList): (String, OptTokList, IntList) = {
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

  @tailrec
  private[this] def findCommentPos(line:String, pos: Int, insideString: Boolean) : Int = {
    line.headOption match {
      case None => pos
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

  @tailrec
  private[this] def findQuot(quot: Set[Char], str: String, pos: Int) : Int = {
    str.headOption match {
      case None => pos
      case Some(c) =>
        c match {
          case q if quot contains q => pos+1
          case '\\' => findQuot(quot, str.drop(2), pos+2)
          case _ => findQuot(quot, str.tail, pos+1)
        }
    }
  }

  private[this] def splitLine(txt: String, currentLine: Int, parenLevel: Int): (OptTokList, Int) = {
    val len = txt.length
    val (tokLst, newParenLevel, ini, pos) = scanTokens(txt, currentLine, parenLevel, Nil, 0, 0)
    if (ini < len) {
      val (token, level) = strToToken(txt.substring(ini, pos), currentLine, newParenLevel)
      ((token :: tokLst).reverse, level)
    }
    else {
      (tokLst.reverse, newParenLevel)
    }
  }

  @tailrec
  private[this]
  def scanTokens(txt:String, cl: Int, pl: Int, tokens: OptTokList, ini:Int, pos: Int): (OptTokList, Int, Int, Int) = {
    val len = txt.length
    if (pos >= len) {
      (tokens, pl, ini, pos)
    }
    else {
      txt(pos) match {
        case '\"' =>
          val (r, npl) = checkToken(txt, ini, pos, cl, pl, tokens)
          val (r2, ip, npl2) = addQuoted(txt, ini, pos, cl, npl, r)
          scanTokens(txt, cl, npl2, r2, ip, ip)
        case '\'' =>
          if (pos != ini)
            scanTokens(txt, cl, pl, tokens, ini, pos+1)
          else{
            val (r, ip, npl) = addQuoted(txt, ini, pos, cl, pl, tokens)
            scanTokens(txt, cl, npl, r, ip, ip)
          }
        case '.' if pos + 2 < len && (txt.substring(pos, pos + 3) == "..." || txt.substring(pos, pos + 3) == "..<") =>
          val (r, npl) = checkToken(txt, ini, pos, cl, pl, tokens)
          val r2 = OPER_MAP(txt.substring(pos, pos + 3)) :: r
          scanTokens(txt, cl, npl, r2, pos+3, pos+3)

        case '.' if pos + 1 < len && txt.substring(pos, pos + 2) == ".." =>
          val (r, npl) = checkToken(txt, ini, pos, cl, pl, tokens)
          scanTokens(txt, cl, npl, Some(DOTDOT) :: r, pos+2, pos+2)

        case '#' =>
          val (r, npl) = checkToken(txt, ini, pos, cl, pl, tokens)
          if (pos + 1 >= len) {
            scanTokens(txt, cl, npl, r, ini, pos+1)
          } else {
            txt(pos + 1) match {
              case '\"' =>
                val (r2, ip, npl2) = addQuoted(txt, pos, pos+1, cl, npl, r)
                scanTokens(txt, cl, npl2, r2, ip, ip)
              case '/' =>
                val (quotedStr, newIni) = parseQuoted('/', txt, pos, pos+1)
                val (token, level) = tryParseHashTag(quotedStr, cl, npl)
                scanTokens(txt, cl, level, token :: r, newIni, newIni)
              case '{' =>
                val (token, level) = tryParseHashTag(txt.substring(ini, pos+2), cl, npl)
                scanTokens(txt, cl, level, token::r, pos+2, pos+2)
              case c if isTimeValidChar(c) =>
                val newPos = skip(pos+1, txt.substring(pos+1), isTimeValidChar)
                val (token, level) = tryParseHashTag(txt.substring(ini, newPos), cl, npl)
                scanTokens(txt, cl, level, token :: r, newPos, newPos)
              case _ =>
                scanTokens(txt, cl, npl, r, ini, pos+1)
            }
          }

        case c if isBlank(c) =>
          val (r, npl) = checkToken(txt, ini, pos, cl, pl, tokens)
          val newPos = skip(pos, txt.substring(pos), isBlank)
          scanTokens(txt, cl, npl, r, newPos, newPos)

        case c if isPunct(c) =>
          val (r, npl) = checkToken(txt, ini, pos, cl, pl, tokens)
          val (token, level) = tryParseOp(txt.substring(pos, pos+1), cl, npl)
          scanTokens(txt, cl, level, token::r, pos+1, pos+1)

        case _ =>
          scanTokens(txt, cl, pl, tokens, ini, pos+1)
      }
    }
  }


  private[this] def parseQuoted(quot: Char, str: String, ini: Int, oldPos: Int): (String, Int) = {
    val pos = findQuot(Set(quot), str.substring(oldPos+1), oldPos+1)
    (str.substring(ini, pos), pos)
  }

  private[this]
  def addQuoted(str: String, ini: Int, pos: Int, cl: Int, pl: Int, lst: OptTokList): (OptTokList, Int, Int) = {
    val (quotedStr, newIni) = parseQuoted(str(pos), str, ini, pos)
    val (token, level) = strToToken(quotedStr, cl, pl)
    (token::lst, newIni, level)
  }


  private[this]
  def checkToken(str: String, ini: Int, pos: Int, cl: Int, pl: Int, lst:OptTokList): (OptTokList, Int) = {
    if (pos > ini) {
      addToken(str, ini, pos, cl, pl, lst)
    } else {
      (lst, pl)
    }
  }

  private[this]
  def addToken(str:String, ini: Int, pos: Int, cl: Int, pl: Int, lst: OptTokList) : (OptTokList, Int) = {
    val (token, level) = strToToken(str.substring(ini, pos), cl, pl)
    (token :: lst, level)
  }
  @tailrec
  private[this] def skip(pos: Int, str: String, f: Char => Boolean): Int = {
    str.headOption match {
      case None => pos
      case Some(c) if f(c) => skip(pos+1, str.tail, f)
      case _ => pos
    }
  }

  private[this]
  def strToToken(str: String, currentLine: Int, parenLevel: Int): (OptToken, Int) = {
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
      str.headOption match {
        case None => (Some(ERROR(currentLine, str)), parenLevel)
        case Some(c) =>
          c match {
            case '\"' if !str.endsWith("\"") =>
              currentString ++= str
              (None, parenLevel)
            case '#' => tryParseHashTag(str, currentLine, parenLevel)
            case ':' if str.length > 1 && str != "::" => (Some(ATOM(str)), parenLevel)
            case '\"' => (Some(STRING(str)), parenLevel)
            case '\'' => (Some(CHAR(str)), parenLevel)
            case _ =>
              val token1 = tryParseId(str, currentLine)
              val token2 = token1 match {
                case Some(ERROR(_, _)) => tryParseNum(str, currentLine)
                case _ => token1
              }
              token2 match {
                case Some(ERROR(_, _)) => tryParseOp(str, currentLine, parenLevel)
                case _ => (token2, parenLevel)
              }
          }
      }
    }
  }

  private[this] def tryParseId(str: String, currentLine: Int): OptToken = {
    KEYWORD_MAP(str) match {
      case Some(token) => Some(token)
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
              parts.lastOption match {
                case None => Some(ID(id))
                case Some(last) =>
                  last.headOption match {
                    case None => Some(ID(id))
                    case Some(c) if c.isUpper => Some(TID(id))
                    case _ => Some(ID(id))
                  }
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

  private[this] def tryParseNum(str: String, currentLine: Int): OptToken = {
    Try(strToNumToken(str)) match {
      case Success(token) => Some(token)
      case Failure(_) => Some(ERROR(currentLine, str))
    }
  }

  private[this] def tryParseOp(str: String, currentLine: Int, parenLevel: Int): (OptToken, Int) = {
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

  private[this] def tryParseHashTag(str: String, currentLine: Int, parenLevel: Int): (OptToken, Int) = {
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

  val punctChars: Set[Char] = Set(',', '(', ')', '[', ']', '{', '}', '\\')

  private[this] def isBlank(c: Char): Boolean = Character.isWhitespace(c)

  private[this] def isIdentifierChar(c: Char): Boolean =
    c match {
      case '_' => true
      case _ => Character.isAlphabetic(c)
    }

  private[this] def isPunct(c: Char): Boolean = punctChars contains c

  private[this] def isTimeValidChar(c: Char): Boolean = {
    c match {
      case '-' | ':' => true
      case _ => Character.isDigit(c) || Character.isUpperCase(c)
    }
  }

  @tailrec
  private[this]
  def mapLines(lines: List[(String, Int)], indentStack: IntList, tokens: List[TokList], parenLevel: Int) : (TokList, IntList) = {
    lines.headOption match {
      case None => (tokens.reverse.flatten, indentStack)
      case Some((text, line)) =>
        val (lineTokens, newIndentStack, newParenLevel) = scanLine(text, line, indentStack, parenLevel)
        mapLines(lines.tail, newIndentStack, lineTokens :: tokens, newParenLevel)
    }
  }

  private[this] def scanLines(lines: Iterator[(String, Int)]): TokenStream = {
    val (tokens, indentStack) = mapLines(lines.toList, List(0), Nil, parenLevel = 0)
    val result = if (indentStack.isEmpty) {
      tokens
    } else {
      val newIndentStack = indentStack.dropWhile(p => p > 0)
      List.fill(indentStack.length - newIndentStack.length)(DEDENT) ++ tokens.reverse
    }
    TokenStream(result.reverse)
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
