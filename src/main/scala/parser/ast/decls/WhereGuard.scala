package parser.ast.decls

import parser.ast.expressions.Expression

case class WhereGuard(guarExpr: Option[Expression], body: Expression)
