package com.mindforge.graphics.math

import com.mindforge.graphics.identityMatrix3
import com.mindforge.graphics.matrix
import com.mindforge.graphics.vector
import org.jetbrains.spek.api.*

class Matrix3Specs : Spek() {init {
    given("an arbitrary 3 square matrix") {
        val m = matrix(
                1, -1, 4,
                3, 4, -5,
                -2, 0, 1)

        on("getting the determinant") {
            val x = m.determinant

            it("should be correct") {
                shouldEqual(29.0, x)
            }
        }

        on("getting a submatrix") {
            val x = m.subMatrix(1, 2)

            it("should be correct") {
                shouldEqual(matrix(1, -1, -2, 0), x)
            }
        }

        on("getting the transpose") {
            val x = m.transpose()

            it("should be correct") {
                shouldEqual(matrix(
                        1, 3, -2,
                        -1, 4, 0,
                        4, -5, 1), x)
            }
        }

        on("getting the adjugate") {
            val x = m.adjugate()

            it("should be correct") {
                shouldEqual(matrix(
                        4, 1, -11,
                        7, 9, 17,
                        8, 2, 7), x)

            }
        }

        on("getting a row") {
            val x = m.row(2)

            it("should be correct") {
                shouldEqual(vector(-2, 0, 1), x)
            }
        }

        on("getting a column") {
            val x = m.column(1)

            it("should be correct") {
                shouldEqual(vector(-1, 4, 0), x)
            }
        }

        on("getting the inverse") {
            val i = m.inverse()!!

            it("should be correct") {
                shouldEqual(matrix(
                        4, 1, -11,
                        7, 9, 17,
                        8, 2, 7) / 29, i)
            }
        }

        on("getting the string representation") {
            val x = m.toString()

            it("should be correct") {
                shouldEqual("matrix(1.0, -1.0, 4.0, 3.0, 4.0, -5.0, -2.0, 0.0, 1.0)", x)
            }
        }

        on("multiplying it with a vector") {
            val x = m * vector(1, 2, 3)

            it("should be correct") {
                shouldEqual(vector(11, -4, 1), x)
            }
        }

        on("multiplying it with another matrix with determinant 0") {
            val x = m * matrix(1, 2, 3, 4, 5, 6, 7, 8, 9)

            it("should be correct") {
                shouldEqual(matrix(25, 29, 33, -16, -14, -12, 5, 4, 3), x)
            }
        }

        on("multiplying it with another matrix with determinant different from 0") {
            val x = m * matrix(-2, 2, 3, 1, 5, -6, -4, 0, 3)

            it("should be correct") {
                shouldEqual(matrix(-19, -3, 21, 18, 26, -30, 0, -4, -3), x)
            }
        }
    }

    given("a 3 square matrix with determinant 0") {
        val m = matrix(
                1, 2, 3,
                4, 5, 6,
                7, 8, 9)

        on("getting the determinant") {
            val d = m.determinant

            it("should be 0") {
                shouldEqual(0.0, d)
            }
        }

        on("getting the inverse") {
            it("should return null") {
                shouldEqual(m.inverse(), null)
            }
        }
    }

    given("the 3 identity matrix") {
        val m = identityMatrix3

        on("multiplying it with an arbitrary vector") {
            val x = m * vector(0, 1, -2.5)

            it("should result in an unchanged vector") {
                shouldEqual(vector(0, 1, -2.5), x)
            }
        }
    }
}
}