package com.mindforge.graphics.math

import com.mindforge.graphics.math.sets.*
import org.jetbrains.spek.api.*

public class SetSpecs : Spek() {init {
    given("some integers to test") {
        array<Number>(0, 205, 1.2E4, -40, basicReal("42")).forEach {
            on("checking if $it is an Integer") {
                it("should be true") {
                    shouldBeTrue(it in IntegerSet)
                }
            }
        }
        array<Number>(Infinity, .02, -1.5E-12, 87.5, basicReal("-0.912E+2")).forEach {
            on("checking if $it is an Integer") {
                it("should be false") {
                    shouldBeFalse(it in IntegerSet)
                }
            }
        }
    }
    given("some numbers to test in a range") {
        array<Number>(.3, basicReal(25.2), 67, 112E5).forEach {
            on("checking if $it is positive") {
                it("should Be True") {
                    shouldBeTrue(it in RealSet.Positive)
                }
            }
        }
        array<Number>(0, -33, basicReal("0.00"), Infinity, -44.4).forEach {
            on("checking if $it is positive") {
                it("should Be False") {
                    shouldBeFalse(it in RealSet.Positive)
                }
            }
        }
        on("checking if Infinities are in ther Set of Real Numbers") {
            it("should be false") {
                shouldBeFalse(Infinity in RealSet)
                shouldBeFalse(-Infinity in RealSet)
            }
        }
    }
    given("the set (-2,2)") {
        val e = openRealSet(-2, 2)
        on("testing with [-2,2]") {
            val f = realSet(-2, 2)
            it("should be in [-2,2] but not the other way round") {
                shouldBeTrue(e in f)
                shouldBeFalse(f in e)
            }
        }
        on("testing with RealSets and IntegerSets") {
            it("should be in Full RealSet but not in positive or negative ones and IntegerSets") {
                shouldBeTrue(e in RealSet)
                shouldBeFalse(e in RealSet.PositiveAndZero)
                shouldBeFalse(e in RealSet.NegativeAndZero)
                shouldBeFalse(e in IntegerSet)
            }
        }
        on("testing with SetIntersections") {
            it("should be empty, intersected with EmptySet") {
                shouldEqual(EmptySet, setIntersection(e, EmptySet).simplifySets())
            }
            it("should be empty, intersected with (2, 4)") {
                shouldEqual(EmptySet, setIntersection(e, openRealSet(2, 4)).simplifySets())
            }
            it("should be (0,1] when intersecting with (0,1]") {
                val f = realSet(0, 1, false, true)
                shouldEqual(f, setIntersection(e, f).simplifySets())
            }
        }
    }
    given("a [-1, Infinity) Multiple Set of pi")
    {
        val pi = basicReal("3.1415926") // whatever
        val s = multipleOfSet(pi, -1, Infinity)
        on("checking numbers that should be in the set") {
            array<Number>(pi * -1, 0, pi * 5, pi, 31415926).forEach {
                it("$it should be in that set") {
                    shouldBeTrue(it in s)
                }
            }
        }
        on("checking numbers that should not be in the set") {
            array<Number>(pi * -2, Infinity, 3.14, 314159, 20000).forEach {
                it("$it should NOT be in that set") {
                    shouldBeTrue(it !in s)
                }
            }
        }
    }


}
}