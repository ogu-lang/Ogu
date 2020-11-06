package parser.ast.expressions

case class LambdaSimpleArg(name: String) extends Name(name) with LambdaArg
