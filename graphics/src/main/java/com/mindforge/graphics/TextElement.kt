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
    fun shape(text: String): TextShape
}


trait TextInput : Composed<String>, KeysElement<String>, PointersElement<String> {
    var text: String
    override val content: String get() = text

    var cursorPosition: Int
    val textChanged: Observable<String>
}

fun textInput(text: String = "", bound: Rectangle, font: Font, fontFill: Fill, size: Number, backgroundFill: Fill) = object : TextInput {
    override val shape: Shape = null!!
    override val textChanged: Observable<String> = null!!
    override var cursorPosition: Int = null!!
    override var text = text

    private fun textElement() = textElement(text, font, size, fontFill)
    private fun cursor() = coloredElement(rectangle(vector(0,0)), fontFill)
    private fun background() = coloredElement(bound, backgroundFill)

    override val elements = observableList<TransformedElement<*>>()

    private fun refresh() {
        //elements.setTo(textElement(), cursor(), background())
    }
}