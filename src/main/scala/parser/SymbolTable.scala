package parser

import scala.collection.mutable

case class SymbolTable(parent: Option[SymbolTable]) {

  var symbols = mutable.HashMap[String, Symbol]()

  def add(key: String, sym: Symbol) : Unit = {
    symbols.put(key, sym)
    sym.setNameSpace(this)
  }

  def add(sym: Symbol) : Unit = {
    symbols.put(sym.value, sym)
    sym.setNameSpace(this)
  }

  def find(key: String) : Option[Symbol] = {
      return symbols.get(key)
  }

  def findDeep(key: String) : Option[Symbol] =
    symbols.get(key) match {
      case Some(sym) => Some(sym)
      case None => parent match {
        case None => None
        case Some(table) => table.findDeep(key)
      }
    }
}
