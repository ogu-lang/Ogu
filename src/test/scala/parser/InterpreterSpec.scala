package parser

import backend.Backend
import clojure.lang
import org.scalatest.{FlatSpec, Matchers}


class InterpreterSpec extends FlatSpec with Matchers {

  def run(oguScript: String): AnyRef = {
    val stream = getClass.getResourceAsStream(oguScript)
    Backend.compileStream(oguScript, stream)
  }

  def bigInt(value: String): lang.BigInt = lang.BigInt.fromBigInteger(new java.math.BigInteger(value))

  "An Interpreter" should "run misc files" in {
    run("/misc/test0.ogu") should be (null)
    run("/misc/test1.ogu") should be (11)
    run("/misc/test2.ogu") should be (6)
    run("/misc/test3.ogu") should be ("foobar")
    run("/misc/test4.ogu") should be (9)
    run("/misc/test5.ogu") should be (3999998000000L)
    run("/misc/test6.ogu") should be (8)
    run("/misc/test7.ogu") should be (2)
    run("/misc/test8.ogu") should be (bigInt("620448401733239439360000"))
    run("/misc/test9.ogu") should be (10100)
  }

  "An Interpeter" should "run alg files" in {
    run("/alg/e1.ogu") should be(233168)
    run("/alg/e2.ogu") should be(4613732)
    run("/alg/e3.ogu") should be(6857)
    run("/alg/e4.ogu") should be(906609)
  }
}
