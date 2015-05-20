package com.mindforge.graphics

import com.mindforge.graphics.math.*
import com.mindforge.graphics.*
import com.mindforge.graphics.interaction.KeysElement
import com.mindforge.graphics.interaction.PointersElement
import kotlin.properties.Delegates

trait TextElement : ColoredElement<String> {
    val font: Font
    val lineHeight: Number
    override val shape: TextShape get() = font.shape(content, lineHeight)
}

class TextElementImpl(content: String, font: Font, lineHeight: Number, fill: Fill) : TextElement {
    override val changed = trigger<Unit>()

    override var content by Delegates.observed(content, changed)
    override var font by Delegates.observed(font, changed)
    override var lineHeight by Delegates.observed(lineHeight, changed)
    override var fill by Delegates.observed(fill, changed)
}

/* YAGNI trait GlyphShape : Shape {
    val character: Char
}*/

trait LineShape : Shape {
    // YAGNI val glyphs: List<GlyphShape>
    val width: Number
}

trait TextShape : Shape {
    val text: String

    val lineHeight: Number
    val baseline: Number
    val leading: Number

    val lines: List<LineShape>

    fun size() = vector(
                lines.map { it.width.toDouble () }.max() ?: 0.0,
                lineHeight.toDouble() * text.lineCount() + leading.toDouble() * (text.lineCount() - 1)
    )

    fun box() : Shape = rectangle(size()).topLeftAtOrigin() transformed Transforms2.translation(vector(0, lineHeight.toDouble() - baseline.toDouble()))

}

trait Font {
    fun shape(text: String, lineHeight: Number): TextShape
}

fun String.lineCount() = count { it == '\n' } + 1