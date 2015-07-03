package com.mindforge.graphics.interaction

import com.mindforge.graphics.*
import com.mindforge.graphics.math.Shape
import java.util.concurrent.ScheduledFuture

interface Button : LongClickable<Trigger<Unit>>, Composed<Trigger<Unit>> {
    override fun onPointerKeyPressed(pointerKey: PointerKey) {
        content()
    }
}

fun button(
        shape: Shape,
        elements: ObservableIterable<TransformedElement<*>>,
        changed: Observable<Unit> = observable(),
        trigger: Trigger<Unit> = trigger<Unit>(),
        onLongClicked: (PointerKey) -> Unit = {},
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

    var longPressedTask: ScheduledFuture<*>? = null
    var longPressedStartLocation: Vector2? = null
    var longPressedPointerKey: PointerKey? = null
    var lastPressedInMs: Long? = null

    private val longTapDelayInMs: Long = 700
    private val maxDoubleClickDelayInMs = 700
    private val maxLongClickMoveDistance = 20

    override fun onPointerKeyPressed(pointerKey: PointerKey) {
        super.onPointerKeyPressed(pointerKey)

        longPressedStartLocation = pointerKey.pointer.location
        longPressedPointerKey = pointerKey
        longPressedTask = scheduleDelayed(delayInMs = longTapDelayInMs) {
            try {
                lastPressedInMs = null

                longPressedTask = null
                longPressedStartLocation = null
                longPressedPointerKey = null

                onPointerKeyLongClicked(pointerKey)
            } catch(ex: Exception) {
                // TODO better throw it in main thread
                ex.printStackTrace()
            }
        }

        val l = lastPressedInMs

        if (onDoubleClick != null && l != null && System.currentTimeMillis() - l < maxDoubleClickDelayInMs) {
            cancelLongClicked()
            onDoubleClick(pointerKey)
        }

        lastPressedInMs = System.currentTimeMillis()
    }

    override fun onPointerKeyReleased(pointerKey: PointerKey) {
        cancelLongClicked()
    }

    override fun onPointerMoved(pointer: Pointer) {
        val start = longPressedStartLocation
        if (start != null) {
            if ((pointer.location - start).length.toDouble() > maxLongClickMoveDistance) {
                cancelLongClicked()
            }
        }
    }

    override fun onPointerLeft(pointer: Pointer) {
        cancelLongClicked()
    }

    private fun cancelLongClicked() {
        val l = longPressedPointerKey
        longPressedTask?.cancel(false)

        longPressedTask = null
        longPressedStartLocation = null
        longPressedPointerKey = null

        if(l != null) {
            longClickCanceled(l)
        }
    }

    fun onPointerKeyLongClicked(pointerKey: PointerKey) {
        longClick(pointerKey)
        onLongClicked(pointerKey)
    }

    override val longClick = trigger<PointerKey>()
    override val longClickCanceled = trigger<PointerKey>()
}

fun textRectangleButton(inner: TextElement, onLongPressed: (PointerKey) -> Unit = {}, onDoubleClick: (PointerKey) -> Unit = {}, onClick: () -> Unit) = button(
        shape = inner.shape.box(),
        elements = observableIterable(listOf(
                transformedElement(inner) /* DEBUG ,
                transformedElement(coloredElement(inner.shape.box(), Fills.solid(Colors.black * .1))),
                transformedElement(coloredElement(rectangle(vector(10, 1)), Fills.solid(Colors.blue))),
                transformedElement(coloredElement(rectangle(vector(1, 10)), Fills.solid(Colors.blue))) */
        )),
        onLongClicked = onLongPressed,
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
