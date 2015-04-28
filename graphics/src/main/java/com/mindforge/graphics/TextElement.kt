package com.mindforge.graphics

import com.mindforge.graphics.math.*
import com.mindforge.graphics.*
import com.mindforge.graphics.interaction.KeysElement
import com.mindforge.graphics.interaction.PointersElement

trait TextElement : ColoredElement<String> {
    val font: Font
    val size: Number
    override val shape: Shape get() = font.shape(content).transformed(Transforms2.scale(size))
}

fun textElement(content: String, font: Font, size: Number, fill: Fill) = object : TextElement {
    override val content = content
    override val font = font
    override val size = size
    override val fill = fill
}

trait Font {
    fun shape(text: String): Shape
}