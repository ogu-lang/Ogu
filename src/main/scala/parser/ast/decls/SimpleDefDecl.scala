package parser.ast.decls

import parser.ast.expressions.types.{ConstructorExpression, ListExpression}
import parser.ast.expressions.{Expression, Identifier}

case class SimpleDefDecl(inner: Boolean, id: String, args: List[DefArg], body: Expression, whereBlock: Option[WhereBlock])
  extends DefDecl(id) {
  def patterMatching(): Boolean =
    args.exists {
      case DefArg(Identifier(_)) => false
      case _ => true
    }

  def isMulti(): Boolean =
    args.exists {
      case DefArg(ConstructorExpression(_, _)) => true
      case _ => false
    }

}
