package lexer

import parser.{LexerError, UnexpectedTokenClassException, UnexpectedTokenException}


case class TokenStream(var tokens: List[TOKEN]) {

  def nonEmpty : Boolean = tokens.nonEmpty

  def peek(obj: TOKEN) : Boolean = if (tokens.isEmpty) false else tokens.head == obj

  def peek(n: Int, obj: TOKEN) : Boolean =
    if (tokens.isEmpty || tokens.length < n)
      false
    else
      tokens.drop(n-1).head == obj

  def peek[T](t: Class[T]) : Boolean = {
    if (tokens.isEmpty)
      false
    else {
      tokens.head match {
        case lexererror: LEXER_ERROR => throw LexerError(lexererror)
        case _ =>
      }
      val result = t.isAssignableFrom(tokens.head.getClass)
      result
    }
  }

  def nextToken() : Option[TOKEN] = tokens.headOption

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
    println(s"can't consume classof ${t}, tokens=${tokens}")
    throw UnexpectedTokenClassException()
  }

  override def toString: String = {
    tokens.toString()
  }
}
