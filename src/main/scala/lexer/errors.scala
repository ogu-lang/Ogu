package lexer


case class CantScanFileException(filename: String, exception: Throwable)
  extends RuntimeException(s"can't scan file ${filename}, exception = ${exception.getMessage}")

