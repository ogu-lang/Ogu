package parser.ast.expressions.literals

import org.joda.time.DateTime

case class DateTimeLiteral(value: DateTime) extends LiteralExpression
