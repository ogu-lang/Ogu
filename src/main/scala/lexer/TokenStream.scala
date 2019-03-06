package lexer

import exceptions.{LexerError, UnexpectedTokenClassException, UnexpectedTokenException}


case class TokenStream(var tokens: List[TokenBox]) {

  def nonEmpty: Boolean = tokens.nonEmpty

  def isEmpty: Boolean = tokens.isEmpty

  def peek(obj: TOKEN): Boolean = {
    tokens.headOption match {
      case None => false
      case Some(TokenBox(token, line)) => token.equals(obj)
    }
  }

  def peek(n: Int, obj: TOKEN): Boolean = {
    if (tokens.isEmpty || tokens.length < n)
      false
    else
      tokens.drop(n - 1).headOption match {
        case None => false
        case Some(TokenBox(token, line)) => token == obj
      }
  }

  def peek[T](n: Int, t: Class[T]) : Boolean = {
    if (tokens.isEmpty || tokens.length < n)
      false
    else {
      tokens.drop(n - 1).headOption match {
        case None => false
        case Some(TokenBox(token, line)) => t.isAssignableFrom(token.getClass)
      }
    }
  }

  def peek[T](t: Class[T]) : Boolean = {
    tokens.headOption match {
      case None => false
      case Some(TokenBox(token, line)) =>
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
      case Some(TokenBox(token, line)) => line
    }

  def nextToken() : TOKEN = tokens.headOption match {
    case None => EOF
    case Some(TokenBox(token, line)) => token
  }

  def nextTokenBox() : TokenBox = tokens.head

  def pop() : TOKEN = {
    tokens.headOption match {
      case None => EOF
      case Some(TokenBox(result, line)) =>
        tokens = tokens.tail
        result
    }
  }

  def consume(n: Int, token: TOKEN): Unit = {
    if (n > 0) {
      consume(token)
      consume(n-1, token)
    }
  }

  def consume[T]() : Option[T] = {
    if (tokens.isEmpty)
      None
    else {
      val result = tokens.head
      tokens = tokens.tail
      Some(result.token.asInstanceOf[T])
    }
  }

  def consume(obj: TOKEN) : TOKEN = {
    if (peek(obj)) {
      val result = tokens.head
      tokens = tokens.tail
      result.token
    } else {
      throw UnexpectedTokenException(obj, tokens.head)
    }
  }

  def consume[T](t:Class[T]) : T = {
    if (peek(t)) {
      val result = tokens.head.token
      tokens = tokens.tail
      result.asInstanceOf[T]
    }
    else {
      throw UnexpectedTokenClassException(tokens.head.token, tokens.head.line)
    }
  }

  def consumeOptional(tok: TOKEN): Unit = {
    if (peek(tok)) {
      consume(tok)
    }
  }

  def consumeOptionals(tok: TOKEN): Unit = {
    if (peek(tok)) {
      consume(tok)
      consumeOptionals(tok)
    }
  }

  override def toString: String = {
    tokens.toString()
  }
}
