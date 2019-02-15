package parser

import backend.Backend
import clojure.lang
import org.scalatest.{FlatSpec, Matchers}


class InterpreterSpec extends FlatSpec with Matchers {

  def run(oguScript: String): AnyRef = {
    val stream = getClass.getResourceAsStream(oguScript)
    Backend.compileStream(oguScript, stream)
  }

  def toList(anyRef: AnyRef) = {
    anyRef match {
      case l:lang.LazySeq =>
        l.toArray().toList
      case null =>
        List()
    }
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
    toList(run("/misc/test10.ogu")) should be (List(40.0, 20.0))
    run("/misc/test11.ogu") should equal(true)
    run("/misc/test12.ogu") should be(1884.9555921538758)
    run("/misc/test13.ogu") should be(7140)
    run("/misc/test14.ogu") should be(166724149741L)
  }

  "An Interpeter" should "run alg files" in {
    run("/alg/ack.ogu") should be(10)
    run("/alg/collatz.ogu") should be (66)
    run("/alg/facts.ogu") should equal (false)
    run("/alg/pi.ogu") should equal (false)
    toList(run("/alg/qsort.ogu")) should be (List(1, 2, 3, 4, 5, 6, 7, 8, 9))
    run("/alg/e1.ogu") should be(233168)
    run("/alg/e2.ogu") should be(4613732)
    run("/alg/e3.ogu") should be(6857)
    run("/alg/e4.ogu") should be(906609)
    run("/alg/e5.ogu") should be(232792560)
    run("/alg/e6.ogu") should be(25164150)
    run("/alg/e7.ogu") should be(104743)
    run("/alg/e8.ogu") should be(23514624000L)
    run("/alg/e9.ogu") should be(31875000)
  //  run("/alg/e10.ogu") should be(142913828922L)
  }
}
