package com.mindforge.graphics.interaction

import com.mindforge.graphics.*
import com.mindforge.graphics.math.Shape
import java.util.concurrent.ScheduledFuture

trait Button : PointersElement<Trigger<Unit>>, Composed<Trigger<Unit>> {
    override fun onPointerKeyPressed (pointerKey: PointerKey) {
        content()
    }
}

fun button(
        shape: Shape,
        elements: ObservableIterable<TransformedElement<*>>,
        changed: Observable<Unit> = observable(),
        trigger: Trigger<Unit> = trigger<Unit>(),
        onLongPressed: () -> Unit = {},
        onClick: () -> Unit
) = object : Button {

    override val content = trigger
    override val shape = shape
    override val changed = changed
    override val elements = elements

    init {
        trigger addObserver { onClick() }
    }

    var longPressedTask : ScheduledFuture<*>? = null

    override fun onPointerKeyPressed (pointerKey : PointerKey) {
        super.onPointerKeyPressed(pointerKey)

        longPressedTask = scheduleDelayed(delayInMs = 700) {
            longPressedTask = null
            onPointerKeyLongPressed(pointerKey)
        }
    }

    override fun onPointerKeyReleased(pointerKey: PointerKey) {
        longPressedTask?.cancel(false)
        longPressedTask = null
    }

    fun onPointerKeyLongPressed(pointerKey: PointerKey) {
        onLongPressed()
    }
}

fun textRectangleButton(inner: TextElement, onLongPressed: () -> Unit = {}, onClick: () -> Unit) = button(
        shape = inner.shape.box(),
        elements = observableIterable(listOf(transformedElement(inner))),
        onClick = onClick,
        onLongPressed = onLongPressed
    )

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
        elements = observableIterable(listOf(transformedElement(coloredElement(shape, fill))))
)