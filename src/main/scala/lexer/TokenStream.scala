package lexer

import parser.{LexerError, UnexpectedTokenClassException, UnexpectedTokenException}


case class TokenStream(var tokens: List[TOKEN]) {

  def nonEmpty : Boolean = tokens.nonEmpty

  def isEmpty : Boolean = tokens.isEmpty

  def peek(obj: TOKEN) : Boolean = if (tokens.isEmpty) false else tokens.head == obj

  def peek(n: Int, obj: TOKEN) : Boolean =
    if (tokens.isEmpty || tokens.length < n)
      false
    else
      tokens.drop(n-1).head == obj

  def peek[T](n: Int, t: Class[T]) : Boolean =
    if (tokens.isEmpty || tokens.length < n)
      false
    else {
      val token = tokens.drop(n-1).head
      t.isAssignableFrom(token.getClass)
    }

  def peek[T](t: Class[T]) : Boolean = {
    if (tokens.isEmpty)
      false
    else {
      tokens.head match {
        case lexererror: LEXER_ERROR =>
          println(s"@@@LEXER ERROR head= ${tokens.head}")
          throw LexerError(lexererror)
        case _ =>
      }
      t.isAssignableFrom(tokens.head.getClass)
    }
  }

  def nextToken() : Option[TOKEN] = tokens.headOption

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
      Some(result.asInstanceOf[T])
    }
  }

  def consume(obj: TOKEN) : TOKEN = {
    if (peek(obj)) {
      val result = tokens.head
      tokens = tokens.tail
      return result
    }
    println(s"can't consume $obj, tokens=$tokens")
    throw UnexpectedTokenException(obj, tokens)
  }

  def consume[T](t:Class[T]) : T = {
    if (peek(t)) {
      val result = tokens.head
      tokens = tokens.tail
      return result.asInstanceOf[T]
    }
    println(s"can't consume classof $t, tokens=$tokens")
    throw UnexpectedTokenClassException()
  }

  def consumeOptional(tok: TOKEN): Unit = {
    if (peek(tok)) {
      consume(tok)
    }
  }

  def consumeOptionals(tok: TOKEN): Unit = {
    while (peek(tok)) {
      consume(tok)
    }
  }

  override def toString: String = {
    tokens.toString()
  }
}
