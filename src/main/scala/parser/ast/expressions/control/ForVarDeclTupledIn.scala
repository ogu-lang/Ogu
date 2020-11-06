package parser.ast.expressions.control

import parser.ast.expressions.Expression

case class ForVarDeclTupledIn(ids: List[String], initialValue: Expression) extends LoopDeclVariable
