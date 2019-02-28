package codegen

trait Translator[A] {
  def mkString(node:A): String
}

object Translator {

  def apply[A: Translator]: Translator[A] = implicitly
}
