package com.mindforge.test.graphics

import com.mindforge.graphics.*
import com.mindforge.graphics.math.circle
import com.mindforge.graphics.math.rectangle
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.shouldBeFalse
import org.jetbrains.spek.api.shouldBeTrue
import org.jetbrains.spek.api.shouldEqual
import kotlin.test.assertEquals

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

    given("three nested elements") {
        val innerTransform = Transforms2.translation(vector(2, 2))
        val middleTransform = Transforms2.rotation(0.4)

        val inner = transformedElement(coloredElement(rectangle(vector(2, 2)), Fills.invisible), innerTransform)
        val middle = transformedElement(composed(observableIterable(listOf<TransformedElement<*>>(inner))), middleTransform)
        val outer = composed(observableIterable(listOf<TransformedElement<*>>(middle)))

        on("getting the path to inner") {
            val path = outer.pathTo(inner.element)

            it("should be all the elements") {
                assertEquals(listOf(middle, inner), path)
            }
        }

        on("getting the total transform of the inner") {
            outer.totalTransform(inner.element)

            it("should be the middle transform combined with the first transform") {
                assertEquals(middleTransform before innerTransform, outer.totalTransform(inner.element))
            }
        }

        on("getting whether the outer contains inner recursively") {
            val c = outer.containsRecursively(inner.element)

            it("should be true") {
                assert(c)
            }
        }

        on("getting whether the outer contains middle recursively") {
            val c = outer.containsRecursively(middle.element)

            it("should be true") {
                assert(c)
            }
        }
    }
}
}