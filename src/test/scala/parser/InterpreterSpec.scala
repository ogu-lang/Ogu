package parser

import backend.Backend
import clojure.lang
import lexer.Lexer
import org.scalatest.{FlatSpec, Matchers}

import scala.util.{Failure, Success, Try}


class InterpreterSpec extends FlatSpec with Matchers {

  def run(oguScript: String): AnyRef = {
    val stream = getClass.getResourceAsStream(oguScript)
    Backend.compileStream(oguScript, stream)
  }

  def compile(oguScript: String) : Try[_] = {
    val lexer = new Lexer()
    val tryScan = lexer.scan(oguScript, getClass.getResourceAsStream(oguScript))
    tryScan match {
      case Success(tokens) =>
        val parser = new Parser(oguScript, tokens)
        Success(parser.parse())
      case Failure(exception) => Failure(exception)
    }
  }

  private[this] def toList(anyRef: AnyRef) = {
    anyRef match {
      case l:lang.LazySeq =>
        l.toArray().toList.map(toNative)
      case l:lang.PersistentVector =>
        l.toArray().toList.map(toNative)
      case null =>
        List()
    }
  }



  def toNative(anyRef: AnyRef): Any = {
    anyRef match {
      case l:lang.LazySeq =>
        l.toArray().toList
      case v:lang.PersistentVector =>
        v.toArray.toList
      case k:lang.Keyword =>
        k.toString
      case x =>
        //println(s"@@@ ${x} ${x.getClass}")
        x
    }
  }

  def bigInt(value: String): lang.BigInt = lang.BigInt.fromBigInteger(new java.math.BigInteger(value))


  "An compiler" should "compile demo files" in {
    compile("/demos/black-jack.ogu") should not be(Failure(new Exception))
    compile("/demos/snake.ogu") should not be(Failure(new Exception))
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
    run("/misc/test8.ogu") should be (bigInt("620448401733239439360000"))
    run("/misc/test9.ogu") should be (10100)
    toList(run("/misc/test10.ogu")) should be (List(40.0, 20.0))
    run("/misc/test11.ogu") should equal(true)
    run("/misc/test12.ogu") should be(1884.9555921538758)
    run("/misc/test13.ogu") should be(7140)
    run("/misc/test14.ogu") should be(166724149741L)
    run("/misc/test15.ogu") should be(60)
    toList(run("/misc/test16.ogu")) should be(List(4, 7, 6, 8, 11, 4))
    run("/misc/test17.ogu") should be(10)
    run("/misc/test18.ogu") should be(5040)
    run("/misc/test19.ogu") should be(null)

    val palo = List(new java.lang.Character('C'), new java.lang.Character('D'),
      new java.lang.Character('T'), new java.lang.Character('P'))
    val valor = List(new java.lang.Character('A'), 2, 3, 4, 5, 6, 7, 8, 9, 10, new java.lang.Character('J'),
      new java.lang.Character('Q'), new java.lang.Character('K'))
    var expected = List.empty[List[_]]
    for (p <- palo) {
      for (v <- valor)
        expected = List(v, p) :: expected
    }
    expected = expected.reverse
    toList(run("/misc/test20.ogu")) should be(expected)
    run("/misc/test21.ogu") should equal(false)
    run("/misc/test22.ogu") should equal(true)
    run("/misc/test23.ogu") should be(2000)
    toList(run("/misc/test24.ogu")) should be(List(0, 1, 2, 3, 4, 5, 6, 7, 8, 9))
    run("/misc/test25.ogu") should be(20)
    toList(run("/misc/test26.ogu")) should be(List("area of shape: 314.1592653589793", "area of rectangle: 5000", "area of shape: 200"))
    run("/misc/test27.ogu") should be("estas obeso, cuidado!")
    run("/misc/test28.ogu") should be("Hola Pedro")
    toList(run("/misc/test29.ogu")) should be(List(117, ":a", 100, ":a"))
    run("/misc/test30.ogu") should be(90)
    run("/misc/test31.ogu") should be(4685.0)
    run("/misc/test32.ogu") should be("can't divide by 0")
    toList(run("/misc/test33.ogu")) should be(List("abcxyz", "xyz"))
    run("/misc/test34.ogu") should be(80)
    toList(run("/misc/test35.ogu")) should be(List(89, 463))
    run("/misc/test36.ogu") should be (null)
    toList(run("/misc/test37.ogu")) should be (List(22, 21))
    run("/misc/test38.ogu") should be (30)
    run("/misc/test39.ogu") should be (10)
    run("/misc/test40.ogu") should be (80)
    run("/misc/test41.ogu") should be(null)
    run("/misc/test42.ogu") should be(null)
    run("/misc/test43.ogu") should be(null)
    run("/misc/test44.ogu") should equal(false)
    run("/misc/test45.ogu") should be(10)
    run("/misc/test46.ogu") should be(50)
    run("/misc/test47.ogu") should be(463)
    run("/misc/test48.ogu") should be(null)
    run("/demos/black-jack.ogu") should be(10)
  }

  "An Interpeter" should "run alg files" in {
    run("/alg/ack.ogu") should be(10)
    run("/alg/collatz.ogu") should be (66)
    run("/alg/facts.ogu") should equal (true)
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
    run("/alg/e10.ogu") should be(77088)
  }
}
