package lexer


case class EmptyTokenStream() extends RuntimeException("no tokens available")

case class CantScanFileException(filename: String, exception: Throwable)
  extends RuntimeException(s"can't scan file $filename, exception = ${exception.getMessage}")

