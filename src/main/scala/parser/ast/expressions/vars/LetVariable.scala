package parser.ast.expressions.vars

import parser.ast.expressions.Expression

case class LetVariable(id: LetId, value: Expression) extends Variable
