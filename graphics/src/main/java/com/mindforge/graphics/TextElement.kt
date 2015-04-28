package com.mindforge.graphics

import com.mindforge.graphics.math.*
import com.mindforge.graphics.*
import com.mindforge.graphics.interaction.KeysElement
import com.mindforge.graphics.interaction.PointersElement

trait TextElement : ColoredElement<String> {
    val font: Font
    val size: Number
    override val shape: BoundedShape get() = font.shape(content).scaled(size)
}

fun textElement(content: String, font: Font, size: Number, fill: Fill) = object : TextElement {
    override val content = content
    override val font = font
    override val size = size
    override val fill = fill
}

trait GlyphShape : BoundedShape {
    val character: Char
}

trait TextShape : BoundedShape {
    val text: String
    fun get(index: Int): GlyphShape
}

trait Font {
    fun shape(text: String): Shape
}
