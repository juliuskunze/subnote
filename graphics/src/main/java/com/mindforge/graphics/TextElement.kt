package com.mindforge.graphics

import com.mindforge.graphics.math.Shape
import com.mindforge.graphics.math.rectangle
import kotlin.properties.Delegates

trait TextElement : ColoredElement<String> {
    val font: Font
    val lineHeight: Number
    override val shape: TextShape get() = font.shape(content, lineHeight)
}

class TextElementImpl(content: String, font: Font, lineHeight: Number, fill: Fill) : TextElement {
    private val onChanged = trigger<Unit>()

    override val changed = trigger<Unit>()

    override var content by Delegates.observed(content, onChanged)
    override var font by Delegates.observed(font, onChanged)
    override var lineHeight by Delegates.observed(lineHeight, onChanged)
    override var fill by Delegates.observed(fill, onChanged)

    init {
        onChanged.addObserver {
            shapeValue = super.shape
            changed()
        }
    }

    private var shapeValue = super.shape
    override val shape: TextShape get() = shapeValue
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

    fun box() = rectangle(size(), quadrant = 4)
}

trait Font {
    fun shape(text: String, lineHeight: Number): TextShape
}

fun String.lineCount() = count { it == '\n' } + 1