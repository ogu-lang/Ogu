package parser

class Symbol(val value:String) {
  var namespace : Option[SymbolTable] = None

  def setNameSpace(table: SymbolTable) : Unit = {
    namespace = Some(table)
  }
}

case class FunctionSymbol(name: String) extends Symbol(name)
case class VariableSymbol(name: String) extends Symbol(name)
