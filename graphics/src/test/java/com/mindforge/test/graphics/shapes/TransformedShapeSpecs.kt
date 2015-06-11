package com.mindforge.test.graphics.shapes

import com.mindforge.graphics.Transforms2
import com.mindforge.graphics.math.rectangle
import com.mindforge.graphics.vector
import org.jetbrains.spek.api.*

class TransformedShapeSpecs : Spek() {init {
    given("a transformed rectangle") {
        val x = rectangle(vector(1, 3)).transformed(Transforms2.translation(vector(10,0)))

        on("calling contains for a point inside the rectangle") {
            val c = x.contains(vector(9.6, 1))

            it("should be true") {
                shouldBeTrue(c)
            }
        }

        on("calling contains for a point outside the rectangle") {
            val c = x.contains(vector(10, 1.6))

            it("should be false") {
                shouldBeFalse(c)
            }
        }
    }
}
}