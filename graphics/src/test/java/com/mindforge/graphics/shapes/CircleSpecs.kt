package com.mindforge.graphics.shapes

import com.mindforge.graphics.math.circle
import com.mindforge.graphics.vector
import org.jetbrains.spek.api.*

class CircleSpecs : Spek() {init {
    given("a circle") {
        val x = circle(3)

        on("calling contains for a point inside the circle") {
            val c = x.contains(vector(-1.6, 1.6))

            it("should be true") {
                shouldBeTrue(c)
            }
        }

        on("calling contains for a point outside the circle") {
            val c = x.contains(vector(2.7, 2.8))

            it("should be false") {
                shouldBeFalse(c)
            }
        }
    }
}
}