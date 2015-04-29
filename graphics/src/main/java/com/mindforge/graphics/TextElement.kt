package com.mindforge.graphics

import com.mindforge.graphics.math.*
import com.mindforge.graphics.*
import com.mindforge.graphics.interaction.KeysElement
import com.mindforge.graphics.interaction.PointersElement

trait TextElement : ColoredElement<String> {
    val font: Font
    val lineHeight: Number
    override val shape: TextShape get() = font.shape(content, lineHeight)
}

fun textElement(content: String, font: Font, lineHeight: Number, fill: Fill) = object : TextElement {
    override val content = content
    override val font = font
    override val lineHeight = lineHeight
    override val fill = fill
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

    fun box(): Shape = rectangle(vector(
            lines.map { it.width.toDouble () }.max() ?: 0.0,
            lineHeight.toDouble() * text.lineCount() + leading.toDouble() * (text.lineCount() - 1)
    )).topLeftAtOrigin() transformed Transforms2.translation(vector(0, lineHeight.toDouble() - baseline.toDouble()))

}

trait Font {
    fun shape(text: String, lineHeight: Number): TextShape
}

fun String.lineCount() = count { it == '\n' } + 1