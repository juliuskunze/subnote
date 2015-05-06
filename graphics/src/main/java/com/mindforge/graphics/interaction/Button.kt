package com.mindforge.graphics.interaction

import com.mindforge.graphics.*
import com.mindforge.graphics.math.Shape
import com.mindforge.graphics.math.rectangle

trait Button : PointersElement<Trigger<Unit>>, Composed<Trigger<Unit>> {
    override fun onPointerKeyPressed (pointerKey: PointerKey) = content()
}

fun button(
        shape: Shape,
        elements: ObservableIterable<TransformedElement<*>>,
        changed: Observable<Unit> = observable(),
        trigger: Trigger<Unit> = trigger<Unit>(),
        onClick: () -> Unit = {}
) = object : Button {
    override val content = trigger
    override val shape = shape
    override val changed = changed
    override val elements = elements

    init {
        content addObserver { onClick() }
    }
}

fun textRectangleButton(text: String, fill: Fill, font: Font, size: Number, onClick: () -> Unit): Button {
    val textElement = textElement(text, font, size, fill)

    val shape = textElement.shape.box()
    return button(
            shape = shape,
            elements = observableIterable(listOf(
                    transformedElement(textElement)/*,
                    transformedElement(coloredElement(rectangle(vector(30, 2)), Fills.solid(Colors.red))),
                    transformedElement(coloredElement(rectangle(vector(2, 30)), Fills.solid(Colors.red))),
                    transformedElement(coloredElement(shape, object : Fill {
                        override fun colorAt(location: Vector2) = fill.colorAt(location) * 0.5
                    }))*/
            )),
            onClick = onClick
    )
}

fun coloredButton(
        shape: Shape,
        fill: Fill,
        changed: Observable<Unit> = observable(),
        trigger: Trigger<Unit> = trigger<Unit>(),
        onClick: () -> Unit = {}
) = button(
        onClick = onClick,
        shape = shape,
        trigger = trigger,
        changed = changed,
        elements = observableIterable(listOf<TransformedElement<*>>(transformedElement(coloredElement(shape, fill))))
)