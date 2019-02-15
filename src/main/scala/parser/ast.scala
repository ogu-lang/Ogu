package parser

import org.joda.time.DateTime

sealed trait LangNode

case class Module(name: String, decls: List[LangNode]) extends LangNode

trait AssignableExpression



sealed trait Expression extends LangNode

class Name(name: String) extends Expression
trait LambdaArg
case class LambdaSimpleArg(name: String) extends Name(name) with LambdaArg
case class Identifier(name: String) extends Name(name) with AssignableExpression
case class LambdaTupleArg(names: List[String]) extends LambdaArg

trait LetId
case class LetSimpleId(id:String) extends LetId
case class LetTupledId(ids:List[LetId]) extends LetId

trait Variable
case class LetVariable(id: LetId, value: Expression) extends Variable
case class VarVariable(id: String, initialValue: Expression) extends Variable
case class VarTupledVariable(ids: List[String], initialValue : Expression) extends Variable


trait LetDeclExprTrait extends Expression
case class LetDeclExpr(decls: List[Variable], inExpr: Option[Expression]) extends LetDeclExprTrait
case class VarDeclExpr(decls: List[Variable], inExpr: Option[Expression]) extends Expression

trait LoopDeclVariable extends Variable
case class LoopVarDecl(id: String, initialValue: Expression) extends LoopDeclVariable
case class ForVarDeclIn(id: String, initialValue: Expression) extends LoopDeclVariable


case class WhereGuard(guarExpr: Option[Expression], body: Expression)

trait WhereDef
case class WhereDefSimple(id: String, args: Option[List[Expression]], body: Expression) extends WhereDef
case class WhereDefTupled(idList: List[String], args: Option[List[Expression]], body: Expression) extends WhereDef
case class WhereDefWithGuards(id: String, args: Option[List[Expression]], guards: List[WhereGuard]) extends WhereDef
case class WhereDefTupledWithGuards(idList: List[String], args: Option[List[Expression]], guards: List[WhereGuard]) extends WhereDef
case class WhereBlock(whereDefs: List[WhereDef]) extends LangNode

case class DefArg(expression: Expression)

class DefDecl(val id: String) extends LangNode
case class SimpleDefDecl(inner: Boolean, override val id: String, args: List[DefArg], body: Expression, whereBlock: Option[WhereBlock])
  extends DefDecl(id) {
  def patterMatching(): Boolean =
    args.exists {
      case DefArg(Identifier(_)) => false
      case _ => true
    }
}

case class MultiDefDecl(override val id: String, decls: List[SimpleDefDecl]) extends DefDecl(id) {
  def patternMatching(): Boolean = decls.exists(_.patterMatching())

  def args : List[String] = {
    var count = decls.map(_.args.size).max
    var ids = List.empty[String]
    for (decl <- decls) {
      for (arg <- decl.args) {
        arg match {
          case DefArg(Identifier(id)) =>
            if (!ids.contains(id))
              ids = id :: ids
          case _ =>
        }
      }
    }
    if (ids.size == count) {
      ids.reverse
    }
    else {
      throw InvalidDef()
    }
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
case class FunctionCallWithDollarExpression(func: Expression, args: List[Expression]) extends CallExpression
case class ForwardPipeFuncCallExpression(args: List[Expression]) extends CallExpression
case class ForwardPipeFirstArgFuncCallExpression(args: List[Expression]) extends CallExpression
case class BackwardPipeFuncCallExpression(args: List[Expression]) extends CallExpression
case class BackwardPipeFirstArgFuncCallExpression(args: List[Expression]) extends CallExpression

case class LambdaExpression(args: List[LambdaArg], expr: Expression) extends Expression

case class Atom(value: String) extends Expression

case class BlockExpression(expressions: List[Expression]) extends Expression

class ControlExpression extends Expression
case class ForExpression(variables: List[ForVarDeclIn], body: Expression) extends ControlExpression
case class LoopExpression(variables: List[LoopVarDecl], guard: Option[LoopGuard], body: Expression) extends ControlExpression
case class WhileExpression(comp: Expression, body: Expression) extends ControlExpression
case class UntilExpression(comp: Expression, body: Expression) extends ControlExpression
case class WhenExpression(comp: Expression, body: Expression) extends ControlExpression

case class RepeatNewVarValue(variable: String, value: Expression)
case class RepeatExpr(newVariableValues: Option[List[RepeatNewVarValue]]) extends ControlExpression
case class RecurExpr(args: List[Expression]) extends ControlExpression

case class ElifPart(comp: Expression, body: Expression)
case class IfExpression(comp: Expression, thenPart: Expression, elifPart: List[ElifPart], elsePart: Expression) extends ControlExpression


case class LazyExpression(expr: Expression) extends Expression

class BinaryExpression(val left: Expression, val right: Expression) extends Expression



class LogicalExpression(l1: Expression, r1: Expression)  extends BinaryExpression(l1, r1)
case class LogicalAndExpression(override val left: Expression, override val right: Expression) extends LogicalExpression(left, right)
case class LogicalOrExpression(override val left: Expression, override val right: Expression) extends LogicalExpression(left, right)


class ComparativeExpression(override val left: Expression, override val right: Expression) extends BinaryExpression(left, right)
case class LessThanExpr(override val left: Expression, override val right: Expression) extends ComparativeExpression(left, right)
case class GreaterThanExpr(override val left: Expression, override val right: Expression) extends ComparativeExpression(left, right)
case class LessOrEqualThanExpr(override val left: Expression, override val right: Expression) extends ComparativeExpression(left, right)
case class GreaterOrEqualThanExpr(override val left: Expression, override val right: Expression) extends ComparativeExpression(left, right)
case class EqualsExpr(override val left: Expression, override val right: Expression) extends ComparativeExpression(left, right)
case class NotEqualsExpr(override val left: Expression, override val right: Expression) extends ComparativeExpression(left, right)
case class MatchExpr(override val left: Expression, override val right: Expression) extends ComparativeExpression(left, right)
case class NoMatchExpr(override val left: Expression, override val right: Expression) extends ComparativeExpression(left, right)
case class ContainsExpr(override val left: Expression, override val right: Expression) extends ComparativeExpression(left, right)

case class ConsExpression(override val left: Expression, override val right: Expression) extends BinaryExpression(left, right)

class SumExpression(override val left: Expression, override val right: Expression) extends BinaryExpression(left, right)
case class AddExpression(override val left: Expression, override val right: Expression) extends SumExpression(left, right)
case class SubstractExpression(override val left: Expression, override val right: Expression) extends SumExpression(left, right)
case class ConcatExpression(override val left: Expression, override val right: Expression) extends SumExpression(left, right)


class MultExpression(override val left: Expression, override val right: Expression) extends BinaryExpression(left, right)
case class MultiplyExpression(override val left: Expression, override val right: Expression) extends MultExpression(left, right)
case class MultiplyBigExpression(override val left: Expression, override val right: Expression) extends MultExpression(left, right)
case class DivideExpression(override val left: Expression, override val right: Expression) extends MultExpression(left, right)
case class ModExpression(override val left: Expression, override val right: Expression) extends MultExpression(left, right)


case class PowerExpression(base: Expression, exponent: Expression) extends Expression

case class ArrayAccessExpression(array: Expression, index: Expression) extends Expression with AssignableExpression


case class SimpleAssignExpr(left: Expression, right: Expression) extends Expression with AssignableExpression
case class PlusAssignExpr(left: Expression, right: Expression) extends Expression with AssignableExpression
case class MinusAssignExpr(left: Expression, right: Expression) extends Expression with AssignableExpression
case class MultAssignExpr(left: Expression, right: Expression) extends Expression with AssignableExpression
case class DivAssignExpr(left: Expression, right: Expression) extends Expression with AssignableExpression
case class ModAssignExpr(left: Expression, right: Expression) extends Expression with AssignableExpression


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

trait PartialOper extends Expression
case class PartialAdd(args: List[Expression]) extends PartialOper
case class PartialSub(args: List[Expression]) extends PartialOper
case class PartialMul(args: List[Expression]) extends PartialOper
case class PartialDiv(args: List[Expression]) extends PartialOper
case class PartialMod(args: List[Expression]) extends PartialOper
case class PartialEQ(args: List[Expression]) extends PartialOper
case class PartialNE(args: List[Expression]) extends PartialOper
case class PartialLT(args: List[Expression]) extends PartialOper
case class PartialLE(args: List[Expression]) extends PartialOper
case class PartialGT(args: List[Expression]) extends PartialOper
case class PartialGE(args: List[Expression]) extends PartialOper
case class PartialPow(args: List[Expression]) extends PartialOper
case class PartialCons(args: List[Expression]) extends PartialOper
case class PartialConcat(args: List[Expression]) extends PartialOper

case class DictionaryExpression(items: List[(Expression, Expression)]) extends Expression