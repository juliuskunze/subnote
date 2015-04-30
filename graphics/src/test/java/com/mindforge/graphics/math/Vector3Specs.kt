package com.mindforge.test.graphics.math

import com.mindforge.graphics.vector
import org.jetbrains.spek.api.*

class Vector3Specs : Spek() {init {
    given("a 3 vector") {
        val x = vector(1.5, -4, 3)

        on("getting the string repesentation") {
            val s = x.toString()

            it("should be correct") {
                shouldEqual(s, "vector(1.5, -4.0, 3.0)")
            }
        }
    }

    given("two unit 3 vectors in x- and z-direction") {
        val eX = vector(1, 0, 0)
        val eY = vector(0, 0, 1)

        on("getting the dot product of the two") {
            val p = eX * eY

            it("should be 0.") {
                shouldEqual(0.0, p)
            }
        }

        on("getting the cross product of the two") {
            val p = eX.crossProduct(eY)

            it("should be (0 -1 0)") {
                shouldEqual(vector(0, -1, 0), p)
            }
        }

    }

}
}