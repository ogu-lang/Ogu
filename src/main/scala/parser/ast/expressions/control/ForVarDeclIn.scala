package parser.ast.expressions.control

import parser.ast.expressions.Expression

case class ForVarDeclIn(id: String, initialValue: Expression) extends LoopDeclVariable
