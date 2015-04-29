package com.mindforge.graphics.math

import org.jetbrains.spek.api.*
import com.mindforge.graphics.*
import java.lang.Double
import java.lang.Float

public class BasicRealSpecs : Spek() {init {
    private class to(val a: Number, val b: Number, val r: Number) {}
    given("Strings that are to be converted to BasicReal") {

        on("creating a basic Real of '212'") {
            val br = basicReal("212")
            it("should be 212E+0") {
                shouldEqual(0L, br.exponent)
                shouldEqual(BigInteger(212), br.number)
            }
        }
        on("creating a basic Real of '1.23'") {
            val br = basicReal("1.23")
            it("should be 123E-2") {
                shouldEqual(-2L, br.exponent)
                shouldEqual(BigInteger(123), br.number)
                shouldBeFalse(br.sign)
            }
        }
        on("creating a basic Real of '-0' and '000'") {
            val str1 = "000"
            val str2 = "-0"
            val br1 = basicReal(str1)
            val br2 = basicReal(str2)
            it("should be 0") {
                shouldEqual(0L, br1.exponent)
                shouldEqual(BigInteger(0), br1.number)
                shouldEqual(0L, br2.exponent)
                shouldEqual(BigInteger(0), br2.number)
            }
        }
        on("creating a basic Real of '-001010'") {
            val br = basicReal("-001010")
            it("should be -101E+1") {
                shouldEqual(1L, br.exponent)
                shouldEqual(BigInteger(-101), br.number)
                shouldBeTrue(br.sign)
            }
        }
        on("creating a basic Real of '0.03302'") {
            val br = basicReal("0.03302")
            it("should be 3302E-5") {
                shouldEqual(-5L, br.exponent)
                shouldEqual(BigInteger(3302), br.number)
                shouldBeFalse(br.sign)
            }
        }
        on("creating a basic Real of '6.0E-23'") {
            val br = basicReal("6.0E-23")
            it("should be 6E-23") {
                shouldEqual(-23L, br.exponent)
                shouldEqual(BigInteger(6), br.number)
                shouldBeFalse(br.sign)
            }
        }
        on("creating a basic Real of '-44.36200E+15") {
            val br = basicReal("-44.36200E+15")
            it("should be -44362E+12") {
                shouldEqual(12L, br.exponent)
                shouldEqual(BigInteger(-44362), br.number)
                shouldBeTrue(br.sign)
            }
        }


        on("creating some invalid strings'") {
            array(" 400,2", "23*E+8", "@202020").forEach {
                it("\"${it}\" should cause an exception") {
                    shouldThrow<IllegalArgumentException>() { basicReal(it) }
                }
            }
        }

        on("creating some strings and checking toString()'") {
            array("2990", "50.1", "-6E+20", "-1.01010101E-16").forEach {
                it("should be the kotlin Code to create this \"${it}\" ") {
                    shouldEqual("BasicReal(\"${it}\")", basicReal(it).toString())
                }
            }
        }

        on("creating some strings and checking toMathematicalString()'") {
            array("2990", "1.05", "-6E+20", "-1.01010101E-20", "-120030", ".022", "-.44", "0").forEach {
                it("should be a mathematical String \"${it}\" ") {
                    shouldEqual("${it}", basicReal(it).toMathematicalString())
                }
            }
        }
    }
    given("basicReals that are to be added / substracted") {
        on("adding 2 + 200") {
            val res = (basicReal(2) + basicReal(200)) as BasicReal
            it("should be 202") {
                shouldEqual(BigInteger(202), res.number)
                shouldEqual(0L, res.exponent)
            }
        }
        on("adding 15 + .02 + 560") {
            val res = (basicReal("15") + basicReal(".02") + basicReal("560")) as BasicReal
            it("should be 575.02") {
                shouldEqual(BigInteger(57502), res.number)
                shouldEqual(-2L, res.exponent)
            }
        }
        on("adding 25.4 + (-33)") {
            val res = (basicReal(25.4) + basicReal(-33)) as BasicReal
            it("should be -7.6") {
                shouldEqual(BigInteger(-76), res.number)
                shouldEqual(-1L, res.exponent)
                shouldBeTrue(res.sign)
            }
        }
        on("subtracting -10 with -3000") {
            val res = (basicReal(-10) - basicReal(-3000)) as BasicReal
            it("should be 299E+1") {
                shouldEqual(BigInteger(299), res.number)
                shouldEqual(+1L, res.exponent)
                shouldBeFalse(res.sign)
            }
        }
    }
    given("some basic Reals that are to be multiplied")
    {
        on("multiplying .2 with -.03")
        {
            val res = (basicReal(".2") * basicReal("-.03")) as BasicReal
            it("should be -6E-3") {
                shouldEqual(BigInteger(-6), res.number)
                shouldEqual(-3L, res.exponent)
            }
        }
        on("multiplying 2.4E+7 with 1.1E+4") {
            val res = (basicReal("2.4E+7") * basicReal("1.1E+4")) as BasicReal
            it("should be 2.64E+11") {
                shouldEqual(BigInteger(264), res.number)
                shouldEqual(+9L, res.exponent)
            }
        }
        on("multiplying 3001 with 0") {
            val res = (basicReal(3001) * basicReal(0)) as BasicReal
            it("should be 0E+0") {
                shouldEqual(BigInteger(0), res.number)
                shouldEqual(0L, res.exponent)
            }
        }
    }
    given("some basic Reals to divide") {
        on("dividing 2 by -5") {
            val res = (basicReal(2) / basicReal(-5)) as BasicReal
            it("should be -4E-1") {
                shouldEqual(BigInteger(-4), res.number)
                shouldEqual(-1L, res.exponent)
            }
        }
        on("dividing 160 by .1") {
            val res = (basicReal("160") / basicReal(".1")) as BasicReal
            it("should be 16E+2") {
                shouldEqual(BigInteger(16), res.number)
                shouldEqual(2L, res.exponent)
            }
        }
        on("dividing 63 by 9000") {
            val res = (basicReal(63) / basicReal(9000)) as BasicReal
            it("should be 7E-3") {
                shouldEqual(BigInteger(7), res.number)
                shouldEqual(-3L, res.exponent)
            }
        }
        on("dividing 1001 by 0") {
            it("should throw an Exception 'Division by 0'") {
                shouldThrow<ArithmeticException> { basicReal(1001) / basicReal(0) }
            }
        }
        on("dividing 2 by 3") {
            it("should throw an Exception 'Inaccurate Division'") {
                shouldThrow<RuntimeException> { basicReal(2) / basicReal(3) }
            }
        }
    }
    given("some basic Reals that are to be minimized")
    {
        on("minimizing 10000E+0") {
            val b = basicReal(BigInteger(10000), 0).minimize()
            it("should be 1E+4") {
                shouldEqual(BigInteger(1), b.number)
                shouldEqual(4L, b.exponent)
            }
        }
        on("minimizing 0E+4") {
            val b = basicReal(BigInteger(0), 4).minimize()
            it("should be 0E+0") {
                shouldEqual(BigInteger(0), b.number)
                shouldEqual(0L, b.exponent)
            }
        }
        on("minimizing -1.3200E-1") {
            val b = basicReal(BigInteger(-13200), -5).minimize()
            it("should be -132E-3") {
                shouldEqual(BigInteger(-132), b.number)
                shouldEqual(-3L, b.exponent)
            }
        }
    }
    given("basic Reals for comparison test")
    {
        on("testing if 10 == 10") {
            it("should be true") {
                shouldBeTrue(basicReal(10).equals(basicReal(10)))
            }
        }
        on("testing if -1 < +1") {
            it("should be true") {
                shouldBeTrue(basicReal(-1) < basicReal(+1))
            }
        }
        on("testing if -4 < -2") {
            it("should be true") {
                shouldBeTrue(basicReal(-4) < basicReal(-2))
            }
        }
        on("testing if -1 < -.999") {
            it("should be true") {
                shouldBeTrue(basicReal(-1) < basicReal(-.999))
            }
        }
        on("testing if .99 > .8") {
            it("should be true") {
                shouldBeTrue(basicReal(.99) > basicReal(.8))
            }
        }
    }
    given("basic Reals for comparison tests with other types") {
        on("testing if 1 < Infinity") {
            it("should be true") {
                shouldBeTrue(basicReal(1) < Float.POSITIVE_INFINITY)
                shouldBeTrue((-Infinity + 10000) equals Double.NEGATIVE_INFINITY)
                shouldBeTrue(NegativeInfinity < activeEnvironment.intReal("-2.2E+40"))
            }
        }

    }
    given("basic Reals for rounding tests") {
        on("floor-ing some numbers") {
            it("should be 2 for 2.7") {
                shouldEqual(basicReal(2.7).floor(), 2)
            }
            it("should be -12 for -11.01") {
                shouldEqual(basicReal("-11.01").floor(), -12)
            }
            it("should be 2.4E4 for 2.4E4") {
                shouldEqual(basicReal("2.4E4").floor(), 24000)
            }
        }
        on("ceil-ing some numbers") {
            it("should be 3 for 2.7") {
                shouldEqual(basicReal(2.7).ceil(), 3)
            }
            it("should be -11 for -11.01") {
                shouldEqual(basicReal("-11.01").ceil(), -11)
            }
            it("should be 2.4E4 for 2.4E4") {
                shouldEqual(basicReal("2.4E4").ceil(), 24000)
            }
        }
        on("round-ing some numbers") {
            it("should be 3 for 2.7") {
                shouldEqual(basicReal(2.7).round(), 3)
            }
            it("should be -11 for -11.01") {
                shouldEqual(basicReal("-11.01").round(), -11)
            }
            it("should be 2.4E4 for 2.4E4") {
                shouldEqual(basicReal("2.4E4").round(), 24000)
            }
        }
    }
    given("numbers for gcd tests") {
        array(to(4, 2, 2), to(33, 0, 33), to(.3, 2, .1), to(36, 36, 36), to(20, 44, 4)).forEach {
            on("calculating the gcd of ${it.a} and ${it.b}") {
                it("should be ${it.r}") {
                    shouldEqual(it.r.asCalculatable(), gcd(it.a, it.b))
                }
            }
        }
    }
    given("numbers for lcm tests") {
        array(to(4, 2, 4), to(33, 0, 0), to(.3, 2, 6), to(36, 36, 36), to(20, 44, 220)).forEach {
            on("calculating the lcm of ${it.a} and ${it.b}") {
                it("should be ${it.r}") {
                    shouldEqual(it.r.asCalculatable(), lcm(it.a, it.b))
                }
            }
        }
    }


}
}