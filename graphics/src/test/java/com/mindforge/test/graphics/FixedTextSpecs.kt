package com.mindforge.test.graphics

import org.jetbrains.spek.api.*
import com.mindforge.graphics.*
import com.mindforge.graphics.*
import com.mindforge.graphics.math.rectangle
import com.mindforge.graphics.Font
import com.mindforge.graphics.Fill
import com.mindforge.graphics.Fills
import com.mindforge.graphics.Colors
import com.mindforge.graphics.math.Shape

class FixedTextSpecs : Spek() {init {
    given("a fixed text element") {
        val s = "Kotlin rocks on the rocks!"
        val x = TextElementImpl(
                content = s,
                font = object : Font {
                    override fun shape(text: String, lineHeight: Number) = object : TextShape {
                        override fun contains(location: Vector2) = false

                        override val lineHeight = lineHeight
                        override val baseline = 0
                        override val leading = 0
                        override val lines = listOf<LineShape>()
                        override val text = text
                    }
                },
                fill = Fills.solid(Colors.black), lineHeight = 0.01)

        on("getting the content") {
            val c = x.content

            it("should be the original string") {
                shouldEqual(c, s)
            }
        }
    }
}
}