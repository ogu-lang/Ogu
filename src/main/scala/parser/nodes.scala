package parser

import org.joda.time.DateTime
import parser.ast.functions.ClassMethodDecl

trait LangNode

trait AssignableExpression


trait Expression extends LangNode

class Name(name: String) extends Expression
trait LambdaArg
case class LambdaSimpleArg(name: String) extends Name(name) with LambdaArg
case class Identifier(name: String) extends Name(name) with AssignableExpression
case class LambdaTupleArg(names: List[String]) extends LambdaArg

case class IdIsType(id: String, cl: String) extends Expression

trait LetId
case class LetSimpleId(id:String) extends LetId
case class LetTupledId(ids:List[LetId]) extends LetId

trait Variable
case class LetVariable(id: LetId, value: Expression) extends Variable


trait LetDeclExprTrait extends Expression
case class LetDeclExpr(decls: List[Variable], inExpr: Option[Expression]) extends LetDeclExprTrait
case class VarDeclExpr(decls: List[Variable], inExpr: Option[Expression]) extends Expression
case class BindDeclExpr(decls: List[Variable], inExpr: Expression) extends Expression

trait LoopDeclVariable extends Variable
case class LoopVarDecl(id: String, initialValue: Expression) extends LoopDeclVariable
case class ForVarDeclIn(id: String, initialValue: Expression) extends LoopDeclVariable
case class ForVarDeclTupledIn(ids: List[String], initialValue: Expression) extends LoopDeclVariable


case class WhereGuard(guarExpr: Option[Expression], body: Expression)

trait WhereDef
case class WhereDefSimple(id: String, args: Option[List[Expression]], body: Expression) extends WhereDef
case class WhereDefTupled(idList: List[String], args: Option[List[Expression]], body: Expression) extends WhereDef
case class WhereDefWithGuards(id: String, args: Option[List[Expression]], guards: List[WhereGuard]) extends WhereDef
case class WhereDefTupledWithGuards(idList: List[String], args: Option[List[Expression]], guards: List[WhereGuard]) extends WhereDef
case class WhereBlock(whereDefs: List[WhereDef]) extends LangNode

case class DefArg(expression: Expression)
object DefOtherwiseArg extends DefArg(null)


class DefDecl(id: String) extends LangNode
case class MultiMethod(inner: Boolean, id: String, matches: List[DefArg], args: List[DefArg], body: Expression, whereBlock: Option[WhereBlock])
  extends DefDecl(id)

case class SimpleDefDecl(inner: Boolean, id: String, args: List[DefArg], body: Expression, whereBlock: Option[WhereBlock])
  extends DefDecl(id) {
  def patterMatching(): Boolean =
    args.exists {
      case DefArg(Identifier(_)) => false
      case _ => true
    }
}


case class MultiDefDecl(id: String, decls: List[SimpleDefDecl]) extends DefDecl(id) {
  def patternMatching(): Boolean = decls.exists(_.patterMatching())

  def args : List[String] = {
    val count = decls.map(_.args.size).max
    var ids = List.empty[String]
    decls.foreach(decl => decl.args.foreach {
      case DefArg(Identifier(name)) if !ids.contains(name) => ids = name :: ids
      case DefArg(IdIsType(name, _)) if !ids.contains(name) => ids = name :: ids
      case _ =>
    })
    var i = 0
    while (ids.size < count) {
      ids = s"arg_$i" :: ids
      i += 1
    }
    ids.reverse
  }
}

trait LiteralExpression extends Expression
case class StringLiteral(value: String) extends LiteralExpression
case class CharLiteral(value: String) extends LiteralExpression

case class BoolLiteral(value: Boolean) extends LiteralExpression
case class IntLiteral(value: Int) extends LiteralExpression
case class LongLiteral(value: Long) extends LiteralExpression
case class FloatLiteral(value: Float) extends LiteralExpression
case class DoubleLiteral(value: Double) extends LiteralExpression
case class BigIntLiteral(value: BigInt) extends LiteralExpression
case class BigDecimalLiteral(value: BigDecimal) extends LiteralExpression

trait LoopGuard extends Expression
case class WhileGuardExpr(comp:Expression) extends LoopGuard
case class UntilGuardExpr(comp:Expression) extends LoopGuard

case class DateTimeLiteral(value: DateTime) extends LiteralExpression

case class RegexpLiteral(value: String) extends LiteralExpression
case class FStringLiteral(value: String) extends LiteralExpression

trait CallExpression extends Expression
case class FunctionCallExpression(func: Expression, args:List[Expression]) extends CallExpression
case class NewCallExpression(cls: String, args: List[Expression]) extends CallExpression

case class LambdaExpression(args: List[LambdaArg], expr: Expression) extends Expression

case class Atom(value: String) extends Expression



class BinaryExpression(val left: Expression, val right: Expression) extends Expression





trait ValidRangeExpression extends Expression
case class RangeExpression(rangeInit:Expression, rangeEnd:Expression) extends ValidRangeExpression
case class RangeExpressionUntil(rangeInit:Expression, rangeEnd:Expression) extends ValidRangeExpression
case class RangeWithIncrementExpression(rangeInit:Expression, rangeIncrement: Expression, rangeEnd:Expression) extends ValidRangeExpression
case class RangeWithIncrementExpressionUntil(rangeInit:Expression, rangeIncrement: Expression, rangeEnd:Expression) extends ValidRangeExpression
case class InfiniteRangeExpression(rangeInit: Expression) extends ValidRangeExpression
case class InfiniteRangeWithIncrementExpression(rangeInit: Expression, rangeIncrement: Expression)
case class EmptyListExpresion() extends ValidRangeExpression


trait ListGuard
case class ListGuardDecl(id: String, value: Expression) extends ListGuard
case class ListGuardExpr(value: Expression) extends ListGuard
case class ListGuardDeclTupled(ids: List[String], value: Expression) extends ListGuard


case class ListExpression(expressions: List[Expression], guards: Option[List[ListGuard]]) extends ValidRangeExpression


trait DefBodyGuardExpr
case class DefBodyGuardExpression(comp: Expression, body: Expression) extends DefBodyGuardExpr
case class DefBodyGuardOtherwiseExpression(body: Expression) extends DefBodyGuardExpr
case class BodyGuardsExpresion(guards: List[DefBodyGuardExpr]) extends Expression
case class BodyGuardsExpresionAndWhere(guards: List[DefBodyGuardExpr], whereBlock: WhereBlock) extends Expression


case class TupleExpr(expressions: List[Expression]) extends Expression
case class InfiniteTupleExpr(expressions: List[Expression]) extends Expression


case class DictionaryExpression(items: List[(Expression, Expression)]) extends Expression
case class SetExpression(values: List[Expression]) extends Expression