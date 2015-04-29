package com.mindforge.graphics.shapes

import com.mindforge.graphics.Transforms2
import com.mindforge.graphics.math.rectangle
import com.mindforge.graphics.vector
import org.jetbrains.spek.api.*

class ShapeSpecs : Spek() {init {
    given("a translated rectangle translated") {
        val x = rectangle(vector(1, 3)).transformed(Transforms2.translation(vector(5, 5)))

        on("calling contains for a point inside the rectangle") {
            val c = x.contains(vector(4.6, 4))

            it("should be true") {
                shouldBeTrue(c)
            }
        }

        on("calling contains for a point outside the rectangle") {
            val c = x.contains(vector(0, 0))

            it("should be true") {
                shouldBeFalse(c)
            }
        }
    }
}
}