package parser

import lexer.{LEXER_ERROR, TOKEN}

case class InvalidNodeException(token: Option[TOKEN]) extends Throwable

case class UnexpectedTokenClassException() extends Throwable
case class UnexpectedTokenException(token: TOKEN, tokens: List[TOKEN]) extends Throwable
case class UndefinedIdentifierException(id: String) extends Throwable

case class InvalidLambdaExpression(tokenOpt: Option[TOKEN]) extends Throwable

case class InvalidLetDeclaration(message: String) extends Throwable

case class CantAssignToExpression() extends Throwable

case class InvalidIfExpression() extends Throwable

case class InvalidUntilAlreadyHasWhile() extends Throwable

case class LexerError(error: LEXER_ERROR) extends Throwable

case class InvalidDef() extends Throwable

case class PartialOperNotSupported(oper:TOKEN) extends Throwable