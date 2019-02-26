package parser.ast.expressions.control

import lexer._
import parser.ast.expressions.functions.ForwardPipeFuncCallExpression
import parser.ast.expressions.{Expression, ExpressionParser, ParseExpr}

case class RepeatNewVarValue(variable: String, value: Expression)

case class RepeatExpresion(newVariableValues: Option[List[RepeatNewVarValue]]) extends ControlExpression

object RepeatExpresion extends ExpressionParser {

  override def parse(tokens: TokenStream): Expression = {
    tokens.consume(REPEAT)
    if (tokens.peek(WITH))
      tokens.consume(WITH)
    val repeatVariables = parseRepeatVars(tokens, List(parseRepeatNewValue(tokens)))
    tokens.consumeOptionals(NL)
    RepeatExpresion(Some(repeatVariables))
  }

  private[this] def parseRepeatVars(tokens: TokenStream, vars: List[RepeatNewVarValue]) : List[RepeatNewVarValue] = {
    if (!tokens.peek(COMMA)) {
      vars.reverse
    }
    else {
      tokens.consume(COMMA)
      parseRepeatVars(tokens, parseRepeatNewValue(tokens) :: vars)
    }
  }

  private[this] def parseRepeatNewValue(tokens:TokenStream) : RepeatNewVarValue = {
    if (tokens.peek(classOf[ID]) && tokens.peek(2, ASSIGN)) {
      val id = tokens.consume(classOf[ID])
      tokens.consume(ASSIGN)
      val expr = ForwardPipeFuncCallExpression.parse(tokens)
      RepeatNewVarValue(id.value, expr)
    } else {
      RepeatNewVarValue(genId(), ParseExpr.parse(tokens))
    }
  }

  def genId() : String = s"id_${java.util.UUID.randomUUID.toString}"

}
