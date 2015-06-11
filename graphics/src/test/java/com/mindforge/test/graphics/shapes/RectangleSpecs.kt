package com.mindforge.test.graphics.shapes

import com.mindforge.graphics.math.rectangle
import com.mindforge.graphics.vector
import org.jetbrains.spek.api.*
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RectangleSpecs : Spek() {init {
    given("a rectangle") {
        val x = rectangle(vector(1, 3))

        on("calling contains for a point inside the rectangle") {
            val c = x.contains(vector(-0.4, 1))

            it("should be true") {
                shouldBeTrue(c)
            }
        }

        on("calling contains for a point outside the rectangle") {
            val c = x.contains(vector(0, 1.6))

            it("should be false") {
                shouldBeFalse(c)
            }
        }

        on("calling topLeftAtOrigin") {
            val newRect = rectangle(x.size, quadrant = 4)

            it("should be translated accordingly") {
                assertFalse(newRect.contains(vector(0.1, 0.1)))
                assertTrue(newRect.contains(vector(0.9, -2.9)))
            }
        }
    }
}
}