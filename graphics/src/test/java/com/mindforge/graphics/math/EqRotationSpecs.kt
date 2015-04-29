package com.mindforge.graphics.math

import com.mindforge.graphics.math.simplifier.rotate
import org.jetbrains.spek.api.*

public class EqRotationSpecs : Spek() {init {
    given("reals for addition/subtration rotation") {
        on("rotating 5 + 70 + .8") {
            val a = real(5) + real(70) + real(.8)
            it("should stay the same") {
                shouldEqual(a, a.rotate())
            }
        }
        on("rotating -2 + 5 - 4 + 7 - -.3") {
            val a = real(-2) + real(5) - real(4) + real(7) - real(-.3)
            it("should be (5 + 7 + .3) - (4 + 2)") {
                val b = (real(5) + real(7) + real(.3)) - (real(4) + real(2))
                shouldEqual(b, a.rotate())
            }
        }
        on("rotating -2 - 0 + -4 - 0 - .5") {
            val a = real(-2) - real(0) + real(-4) - real(0) - real(.5)
            it("should be 0 - (2 + 4 + .5)") {
                val b = real(0) - (real(2) + real(4) + real(.5))
                shouldEqual(b, a.rotate())
                shouldEqual(a.calculate(), a.rotate().calculate())
            }
        }
        on("rotating -10 + 2 - (4 - 5 + 0) - (-5 - (10 - 3)") {
            val a = real(-10) + real(2) - (real(4) - real(5) + real(0)) - (real(-5) - (real(10) - real(3)))
            it("should be (2 + 5 + 5 + 10) - (10 + 4 + 3)") {
                val b = (real(2) + real(5) + real(5) + real(10)) - (real(10) + real(4) + real(3))
                shouldEqual(b, a.rotate())
                shouldEqual(a.calculate(), a.rotate().calculate())
            }
        }
    }
    given("reals for multiplication/division rotation") {
        on("rotating -2 * 4 / (3 * 7 / 5) / 1") {
            val a = real(-2) * real(4) / (real(3) * real(7) / real(5)) / real(1)
            it("should be (-2 * 4 * 5) / (3 * 7 * 1)") {
                val b = (real(-2) * real(4) * real(5)) / (real(3) * real(7))
                shouldEqual(b, a.rotate())
            }
        }
        on("rotating 2 * 3 / (1 / 4) / (0 / 1)") {
            val a = real(2) * real(3) / (real(1) / real(4)) / (real(0) / real(1))
            it("should be (2 * 3 * 4) / 0") {
                val b = (real(2) * real(3) * real(4)) / real(0)
                shouldEqual(b, a.rotate())
            }
        }
    }
}
}