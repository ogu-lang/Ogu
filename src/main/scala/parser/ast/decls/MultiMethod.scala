package parser.ast.decls

import parser.ast.expressions.Expression
import parser.ast.{DefArg, WhereBlock}

case class MultiMethod(inner: Boolean, id: String, matches: List[DefArg], args: List[DefArg], body: Expression, whereBlock: Option[WhereBlock])
  extends DefDecl(id)
