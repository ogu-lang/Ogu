package parser

import backend.Backend
import org.scalatest.{FlatSpec, Matchers}

import scala.io.Source

class InterpreterSpec extends FlatSpec with Matchers {

  def run(oguScript: String) = {
    val stream = getClass.getResourceAsStream(oguScript)
    Backend.compileStream(oguScript, stream)
  }

  "An Interpreter" should "run misc files" in {
    run("/misc/test0.ogu") should be (null)
    run("/misc/test1.ogu") should be (11)
    run("/misc/test2.ogu") should be (6)
    run("/misc/test3.ogu") should be ("foobar")
    run("/misc/test4.ogu") should be (9)
    run("/misc/test5.ogu") should be (3999998000000L)
    run("/misc/test6.ogu") should be (8)
    run("/misc/test7.ogu") should be (2)
  }
}
