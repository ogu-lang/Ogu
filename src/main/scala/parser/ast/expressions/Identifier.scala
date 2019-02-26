package parser.ast.expressions

case class Identifier(name: String) extends Name(name) with AssignableExpression
