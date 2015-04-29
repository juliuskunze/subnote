package com.mindforge.graphics

import com.mindforge.graphics.math.circle
import com.mindforge.graphics.math.rectangle
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.shouldBeFalse
import org.jetbrains.spek.api.shouldBeTrue
import org.jetbrains.spek.api.shouldEqual

class ComposedSpecs : Spek() {init {
    given("a composed element of a rectangle and a circle") {
        val c = coloredElement(shape = circle(0.6), fill = Fills.solid(Colors.red))
        val r = coloredElement(shape = rectangle(vector(1, 1)), fill = Fills.solid(Colors.blue))
        val x = composed(observableIterable(listOf(c, r) map { transformedElement(it) : TransformedElement<*> }))

        on("getting the shape") {
            val s = x.shape

            it("should be the concatenated shape") {
                shouldBeTrue(s.contains(vector(0, 0.59)))
                shouldBeTrue(s.contains(vector(0.499, 0.499)))
                shouldBeFalse(s.contains(vector(0.51, 0.35)))
            }
        }

        on("getting the elements at a location contained by all elements") {
            val e = x.elementsAt(vector(0, 0))

            it("should return all elements") {
                shouldEqual(2, e.count())
                shouldEqual(c.shape, e.elementAt(0).element.shape)
                shouldEqual(r.shape, e.elementAt(1).element.shape)
            }
        }

        on("getting the elements at a location contained only by the circle") {
            val e = x.elementsAt(vector(0, 0.59))

            it("should return the circle") {
                shouldEqual(c.shape, e.single().element.shape)
            }
        }
    }
}
}