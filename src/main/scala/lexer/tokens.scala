package lexer

import org.joda.time.DateTime


sealed trait TOKEN
class OPER extends TOKEN
class PAREN extends TOKEN
trait PIPE_OPER extends OPER
trait LOGICAL_BIN_OPER extends OPER
trait COMPARATIVE_BIN_OPER extends OPER
trait SUM_OPER extends OPER
trait MUL_OPER extends OPER

case object EOF extends TOKEN
case class LEXER_ERROR(line:Int, text:String) extends TOKEN
case object INDENT extends TOKEN
case object DEDENT extends TOKEN
case object NL extends TOKEN
case class ID(value: String) extends TOKEN
trait KEYWORD extends TOKEN
trait CONTROL extends KEYWORD
trait DECL extends KEYWORD
case object CONTAINS extends COMPARATIVE_BIN_OPER
case object DEF extends DECL
case object DO extends KEYWORD
case object ELIF extends KEYWORD
case object ELSE extends KEYWORD
case object FOR extends CONTROL
case object IF extends CONTROL
case object IN extends KEYWORD
case object LET extends DECL
case object LOOP extends CONTROL
case object MODULE extends KEYWORD
case object OTHERWISE extends KEYWORD
case object RECUR extends CONTROL
case object REPEAT extends CONTROL
case object THEN extends CONTROL
case object UNTIL extends CONTROL
case object VAR extends DECL
case object WHEN extends CONTROL
case object WHERE extends KEYWORD
case object WHILE extends CONTROL
case object WITH extends KEYWORD

class LITERAL extends TOKEN
class TEXT_LITERAL(text: String) extends LITERAL
case class STRING_LITERAL(value: String) extends TEXT_LITERAL(value)
case class FSTRING_LITERAL(value: String) extends TEXT_LITERAL(value)
case class CHAR_LITERAL(chr: String) extends TEXT_LITERAL(chr)
case class REGEXP_LITERAL(re: String) extends TEXT_LITERAL(re)
case class INT_LITERAL(value: Int) extends LITERAL
case class DOUBLE_LITERAL(value: Double) extends LITERAL
case class FLOAT_LITERAL(value: Float) extends LITERAL
case class LONG_LITERAL(value: Long) extends LITERAL
case class BIGINT_LITERAL(value: BigInt) extends LITERAL
case class BIGDECIMAL_LITERAL(value: BigDecimal) extends LITERAL
trait DATETIME_LITERAL extends TOKEN
case class ISODATETIME_LITERAL(value: DateTime) extends DATETIME_LITERAL


case object AND extends LOGICAL_BIN_OPER
case object ANDB extends OPER
case object ARROBA extends OPER
case object ARROW extends OPER
trait ASSIGN_OPER extends OPER
case object ASSIGN extends ASSIGN_OPER
case object BACK_ARROW extends OPER
case object COMMA extends OPER
case object CONS extends OPER
case object DIV extends MUL_OPER
case object DIV_ASSIGN extends ASSIGN_OPER
case object DOLLAR extends PIPE_OPER
case object DOT extends OPER
case object DOTDOT extends OPER
case object DOTDOTDOT extends OPER
case object DOTO extends PIPE_OPER
case object DOTO_BACK extends PIPE_OPER
case object EQUALS extends COMPARATIVE_BIN_OPER
case object GUARD extends OPER
case object GE extends COMPARATIVE_BIN_OPER
case object GT extends COMPARATIVE_BIN_OPER
case object LAMBDA extends TOKEN
case object LBRACKET extends PAREN
case object LCURLY extends PAREN
case object LPAREN extends PAREN
case object LE extends COMPARATIVE_BIN_OPER
case object LT extends COMPARATIVE_BIN_OPER
case object MATCHES extends COMPARATIVE_BIN_OPER
case object MINUS extends SUM_OPER
case object MINUS_BIG extends SUM_OPER
case object MINUS_ASSIGN extends ASSIGN_OPER
case object MOD extends MUL_OPER
case object MOD_ASSIGN extends ASSIGN_OPER
case object MULT extends MUL_OPER
case object MULT_BIG extends MUL_OPER
case object MULT_ASSIGN extends ASSIGN_OPER
case object NOT_EQUALS extends COMPARATIVE_BIN_OPER
case object NOT_MATCHES extends COMPARATIVE_BIN_OPER
case object OR extends LOGICAL_BIN_OPER
case object PIPE_LEFT extends PIPE_OPER
case object PIPE_LEFT_FIRST_ARG extends PIPE_OPER
trait FORWARD_PIPE extends PIPE_OPER
case object PIPE_RIGHT extends FORWARD_PIPE
case object PIPE_RIGHT_FIRST_ARG extends FORWARD_PIPE

case object PLUS extends SUM_OPER
case object PLUS_BIG extends SUM_OPER
case object PLUS_ASSIGN extends ASSIGN_OPER
case object PLUS_PLUS extends SUM_OPER
case object POW extends OPER
case object RBRACKET extends PAREN
case object RCURLY extends PAREN
case object RPAREN extends PAREN
case object SEMI extends OPER
