package com.mindforge.graphics

import org.jetbrains.spek.api.*
import com.mindforge.graphics.*
import com.mindforge.graphics.*
import com.mindforge.graphics.math.rectangle
import com.mindforge.graphics.textElement
import com.mindforge.graphics.Font
import com.mindforge.graphics.Fill
import com.mindforge.graphics.Fills
import com.mindforge.graphics.Colors
import com.mindforge.graphics.math.BoundedShape
import com.mindforge.graphics.math.Shape

class FixedTextSpecs : Spek() {init {
    given("a fixed text element") {
        val s = "Kotlin rocks on the rocks!"
        val x = textElement(
                content = s,
                font = object : Font {
                    override fun shape(text: String) = object : BoundedShape {
                        override fun contains(location: Vector2) = false

                        override val top = 1
                        override val right = 1
                        override val bottom = -1
                        override val left = -1
                    }
                },
                fill = Fills.solid(Colors.black), size = 0.01)

        on("getting the content") {
            val c = x.content

            it("should be the orginal string") {
                shouldEqual(c, s)
            }
        }
    }
}
}