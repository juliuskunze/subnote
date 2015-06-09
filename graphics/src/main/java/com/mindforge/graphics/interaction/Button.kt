package com.mindforge.graphics.interaction

import com.mindforge.graphics.*
import com.mindforge.graphics.math.Shape
import java.util.concurrent.ScheduledFuture

interface Button : PointersElement<Trigger<Unit>>, Composed<Trigger<Unit>> {
    override fun onPointerKeyPressed (pointerKey: PointerKey) {
        content()
    }
}

fun button(
        shape: Shape,
        elements: ObservableIterable<TransformedElement<*>>,
        changed: Observable<Unit> = observable(),
        trigger: Trigger<Unit> = trigger<Unit>(),
        onLongPressed: (PointerKey) -> Unit = {},
        onDoubleClick: ((PointerKey) -> Unit)? = null,
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

    var lastPressedInMs: Long? = null

    private val longTapDelayInMs: Long = 700
    private val maxDoubleClickDelayInMs = 700

    override fun onPointerKeyPressed (pointerKey : PointerKey) {
        super.onPointerKeyPressed(pointerKey)

        longPressedTask = scheduleDelayed(delayInMs = longTapDelayInMs) {
            try {
                longPressedTask = null
                lastPressedInMs = null

                onPointerKeyLongPressed(pointerKey)
            }
            catch(ex : Exception) {
                // TODO better throw it in main thread
                ex.printStackTrace()
            }
        }

        val l = lastPressedInMs

        if (onDoubleClick != null && l != null && System.currentTimeMillis() - l < maxDoubleClickDelayInMs) {
            cancelLongPressed()
            onDoubleClick(pointerKey)
        }

        lastPressedInMs = System.currentTimeMillis()
    }

    override fun onPointerKeyReleased(pointerKey: PointerKey) {
        cancelLongPressed()
    }

    private fun cancelLongPressed() {
        longPressedTask?.cancel(false)
        longPressedTask = null
    }

    fun onPointerKeyLongPressed(pointerKey: PointerKey) {
        onLongPressed(pointerKey)
    }
}

fun textRectangleButton(inner: TextElement, onLongPressed: (PointerKey) -> Unit = {}, onDoubleClick: (PointerKey) -> Unit = {}, onClick: () -> Unit) = button(
        shape = inner.shape.box(),
        elements = observableIterable(listOf(transformedElement(inner))),
        onLongPressed = onLongPressed,
        onDoubleClick = onDoubleClick,
        onClick = onClick
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