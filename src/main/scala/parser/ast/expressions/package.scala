package parser.ast

import lexer._

package object expressions {

  def funcCallEndToken(tokens:TokenStream) : Boolean =
    if (tokens.isEmpty) {
      true
    } else {
      tokens.nextToken().exists { next =>
        next == NL || next.isInstanceOf[PIPE_OPER] || next.isInstanceOf[OPER] || next.isInstanceOf[DECL] ||
          next == INDENT || next == DEDENT || next == ASSIGN ||
          next == DOLLAR || next == COMMA || next == LET || next == VAR || next == DO || next == THEN ||
          next == ELSE || next == RPAREN || next == IN || next == RBRACKET || next == RCURLY || next == WHERE
      }
    }
}
