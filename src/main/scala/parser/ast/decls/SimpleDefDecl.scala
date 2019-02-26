package parser.ast.decls

import parser.ast.expressions.{Expression, Identifier}
import parser.ast.{DefArg, WhereBlock}

case class SimpleDefDecl(inner: Boolean, id: String, args: List[DefArg], body: Expression, whereBlock: Option[WhereBlock])
  extends DefDecl(id) {
    def patterMatching(): Boolean =
      args.exists {
        case DefArg(Identifier(_)) => false
        case _ => true
      }
  }
