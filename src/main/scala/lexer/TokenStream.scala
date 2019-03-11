package lexer

import exceptions.{LexerError, UnexpectedTokenClassException, UnexpectedTokenException}


case class TokenStream(var tokens: List[Token]) {

  def nonEmpty: Boolean = tokens.nonEmpty

  def isEmpty: Boolean = tokens.isEmpty

  def peek(obj: SYMBOL): Boolean = {
    tokens.headOption match {
      case None => false
      case Some(Token(symbol, line)) => symbol.equals(obj)
    }
  }

  def peek(n: Int, obj: SYMBOL): Boolean = {
    if (tokens.isEmpty || tokens.length < n)
      false
    else
      tokens.drop(n - 1).headOption match {
        case None => false
        case Some(Token(token, line)) => token == obj
      }
  }

  def peek[T](n: Int, t: Class[T]) : Boolean = {
    if (tokens.isEmpty || tokens.length < n)
      false
    else {
      tokens.drop(n - 1).headOption match {
        case None => false
        case Some(Token(token, line)) => t.isAssignableFrom(token.getClass)
      }
    }
  }

  def peek[T](t: Class[T]) : Boolean = {
    tokens.headOption match {
      case None => false
      case Some(Token(token, line)) =>
        token match {
          case lexererror: ERROR =>
            throw LexerError(lexererror)
          case _ =>
        }
        t.isAssignableFrom(token.getClass)
    }
  }

  def currentLine(): Int =
    tokens.headOption match {
      case None => -1
      case Some(Token(token, line)) => line
    }

  def nextSymbol() : SYMBOL = tokens.headOption match {
    case None => EOF
    case Some(Token(token, line)) => token
  }

  def nextToken() : Option[Token] = tokens.headOption

  def pop() : SYMBOL = {
    tokens.headOption match {
      case None => EOF
      case Some(Token(result, line)) =>
        tokens = tokens.tail
        result
    }
  }

  def consume(n: Int, token: SYMBOL): Unit = {
    if (n > 0) {
      consume(token)
      consume(n-1, token)
    }
  }

  def consume[T]() : Option[T] = {
    tokens.headOption match {
      case None => None
      case Some(result) =>
        tokens = tokens.tail
        Some(result.symbol.asInstanceOf[T])
    }
  }

  def consume(obj: SYMBOL) : SYMBOL = {
    if (peek(obj)) {
      tokens.headOption match {
        case Some(result) =>
          tokens = tokens.tail
          result.symbol
        case _ => EOF
      }
    } else {
      throw UnexpectedTokenException(tokens.headOption)
    }
  }

  def consume[T](t:Class[T]) : T = {
    tokens.headOption match {
      case None => throw UnexpectedTokenClassException(None)
      case Some(token) =>
        val result = token.symbol
        tokens = tokens.tail
        result.asInstanceOf[T]
    }
  }

  def consumeOptional(tok: SYMBOL): Unit = {
    if (peek(tok)) {
      consume(tok)
    }
  }

  def consumeOptionals(tok: SYMBOL): Unit = {
    if (peek(tok)) {
      consume(tok)
      consumeOptionals(tok)
    }
  }

  override def toString: String = {
    tokens.toString()
  }
}
