package com.mindforge.test.graphics.math

import com.mindforge.graphics.matrix
import com.mindforge.graphics.vector
import org.jetbrains.spek.api.*

class Matrix2Specs : Spek() {init {
    given("a 2 square matrix") {
        val m = matrix(1, 2.0, -4, 3)

        on("getting their components") {
            it("should be correct") {
                shouldEqual(1, m.a)
                shouldEqual(2.0, m.b)
                shouldEqual(-4, m.c)
                shouldEqual(3, m.d)
            }
        }

        on("getting their components by position") {
            it("should be correct") {
                shouldEqual(1, m[0, 0])
                shouldEqual(2.0, m[1, 0])
                shouldEqual(-4, m[0, 1])
                shouldEqual(3, m[1, 1])
            }
        }

        on("getting a row") {
            val x = m.row(1)

            it("should be correct") {
                shouldEqual(vector(-4, 3), x)
            }
        }

        on("getting a column") {
            val x = m.column(0)

            it("should be correct") {
                shouldEqual(vector(1, -4), x)
            }
        }

        on("multiplying it with a vector") {
            val x = m * vector(1, 4)

            it("should be correct") {
                shouldEqual(vector(9, 8), x)
            }
        }

        on("getting the determinant") {
            val d = m.determinant

            it("should be correct") {
                shouldEqual(11.0, d)
            }
        }

        on("getting the inverse") {
            val i = m.inverse()

            it("should be correct") {
                shouldEqual(matrix(3.0 / 11, -2.0 / 11, 4.0 / 11, 1.0 / 11), i)
            }
        }

        on("getting the string representation") {
            val x = m.toString()

            it("should be correct") {
                shouldEqual("matrix(1.0, 2.0, -4.0, 3.0)", x)
            }
        }
    }
}
}