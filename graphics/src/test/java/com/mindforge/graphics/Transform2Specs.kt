package com.mindforge.graphics

import org.jetbrains.spek.api.*
import com.mindforge.graphics.*

class Transform2Specs : Spek() {init {
    given("an identity transform") {
        val t = Transforms2.identity

        on("applying it on a vector") {
            val applied = t(vector(1.4, -11))

            it("should be unchanged") {
                shouldEqual(vector(1.4, -11), applied)
            }
        }
    }

    given("a translation") {
        val t = Transforms2.translation(vector(2, -3))

        on("applying it on a vector") {
            val applied = t(vector(1, 3))

            it("should be translated accordingly") {
                shouldEqual(vector(3, 0), applied)
            }
        }
    }

    given("a rotation") {
        val t = Transforms2.rotation(Math.PI / 2)

        on("applying it on a vector") {
            val x = t(vector(1, 3))

            it("should be rotated accordingly") {
                shouldEqualWithError(vector(-3, 1), x)
            }
        }
    }

    given("a scale") {
        val t = Transforms2.scale(-2)

        on("applying it on a vector") {
            val applied = t(vector(1, 3))

            it("should be scaled accordingly") {
                shouldEqual(vector(-2, -6), applied)
            }
        }
    }

    given("a reflection") {
        val t = Transforms2.reflection(axisAngle = 10 * Math.PI)

        on("applying it on a vector") {
            val applied = t(vector(1, 3))

            it("should be reflected accordingly") {
                shouldEqualWithError(vector(1, -3), applied)
            }
        }
    }

    given("an arbitrary affine transformation") {
        val t = transform(matrix(-4, 3, 5, 0, 4, -0.5, 1, 0, 6))

        on("getting the string representation") {
            val x = t.toString()

            it("should be correct") {
                shouldEqual("transform(matrix(-4.0, 3.0, 5.0, 0.0, 4.0, -0.5, 1.0, 0.0, 6.0))", x)
            }
        }
    }

    given("a translation of (3,3) before a rotation of pi/2") {
        val t = Transforms2.translation(vector(3, 3)) before Transforms2.rotation(Math.PI / 2)

        on("applying it on (1,0)") {
            val x = t(vector(1, 0))

            it("should be (-3,4)") {
                shouldEqual(vector(-3, 4), x)
            }
        }
    }
}
}