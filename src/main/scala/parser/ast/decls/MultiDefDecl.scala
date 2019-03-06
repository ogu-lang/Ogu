package parser.ast.decls

import exceptions.InvalidDef
import lexer.TokenStream
import parser.ast._
import parser.ast.expressions.Identifier
import parser.ast.expressions.types.TupleExpression

case class MultiDefDecl(id: String, decls: List[SimpleDefDecl]) extends DefDecl(id) {
  def patternMatching(): Boolean = decls.exists(_.patterMatching())

  val count = decls.map(_.args.size).max

  def args : List[String] = {
    val ids: List[String] = decls.flatMap(decl => decl.args.flatMap {
      case DefArg(Identifier(name)) => Some(name)
      case DefArg(IdIsType(name, _)) => Some(name)
      case DefArg(TupleExpression(List(Identifier(name),_))) => Some(name)
      case _ => None
    }).distinct
    ids ++ 0.until(count-ids.size).map(i => s"arg_$i")
  }
}

object MultiDefDecl {

  def parse(inner: Boolean, tokens: TokenStream, defs: Map[String, MultiDefDecl]) : (Map[String, MultiDefDecl], LangNode) = {
    val defDecl = DefDecl.parse(inner, tokens)
    defDecl match {
      case decl: SimpleDefDecl =>
        defs.get(decl.id) match {
          case None =>
            val mDecl = MultiDefDecl(decl.id, List(decl))
            (defs + (mDecl.id -> mDecl), mDecl)
          case Some(defDecl) =>
            val decls = decl :: defDecl.decls
            val mDecl = MultiDefDecl(defDecl.id, decls)
            (defs + (mDecl.id -> mDecl), mDecl)
        }
      case node:LangNode => (defs, node)
      case _ => throw InvalidDef()
    }
  }

}