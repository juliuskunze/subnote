package com.mindforge.graphics.shapes

import com.mindforge.graphics.math.ellipse
import com.mindforge.graphics.vector
import org.jetbrains.spek.api.*

class EllipseSpecs : Spek() {init {
    given("an ellipse") {
        val x = ellipse(vector(1, 3))

        on("calling contains for a point inside the ellipse") {
            val c = x.contains(vector(-0.4, -0.1))

            it("should be true") {
                shouldBeTrue(c)
            }
        }

        on("calling contains for a point outside the ellipse") {
            val c = x.contains(vector(0.49, 1.49))

            it("should be false") {
                shouldBeFalse(c)
            }
        }
    }
}
}