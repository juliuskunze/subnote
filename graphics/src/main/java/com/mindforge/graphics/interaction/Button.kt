package com.mindforge.graphics.interaction

import com.mindforge.graphics.*
import com.mindforge.graphics.math.Shape
import com.mindforge.graphics.math.rectangle
// not available in JS: import java.util.concurrent.ScheduledFuture

trait Button : PointersElement<Trigger<Unit>>, Composed<Trigger<Unit>> {
    override fun onPointerKeyPressed(pointerKey: PointerKey) {
        content()
    }
}

fun button(
        shape: Shape,
        elements: ObservableIterable<TransformedElement<*>>,
        changed: Observable<Unit> = observable(),
        trigger: Trigger<Unit> = trigger<Unit>(),
        onLongPressed: (PointerKey) -> Unit = {},
        onClick: () -> Unit
) = object : Button {

    override val content = trigger
    override val shape = shape
    override val changed = changed
    override val elements = elements

    init {
        trigger addObserver { onClick() }
    }

    //var longPressedTask: ScheduledFuture<*>? = null

    override fun onPointerKeyPressed(pointerKey: PointerKey) {
        super.onPointerKeyPressed(pointerKey)

        /*longPressedTask = scheduleDelayed(delayInMs = 700) {
            try {
                longPressedTask = null
                onPointerKeyLongPressed(pointerKey)
            } catch(ex: Exception) {
                // TODO better throw it in main thread
                ex.printStackTrace()
            }
        }*/
    }

    override fun onPointerKeyReleased(pointerKey: PointerKey) {
        /*longPressedTask?.cancel(false)
        longPressedTask = null*/
    }

    fun onPointerKeyLongPressed(pointerKey: PointerKey) {
        onLongPressed(pointerKey)
    }
}

fun textRectangleButton(inner: TextElement, onLongPressed: (PointerKey) -> Unit = {}, onClick: () -> Unit) = button(
        shape = inner.shape.box(),
        elements = observableIterable(listOf(
                transformedElement(inner) /* DEBUG */,
                transformedElement(coloredElement(inner.shape.box(), Fills.solid(Colors.black * .1))),
                transformedElement(coloredElement(rectangle(vector(10, 1)), Fills.solid(Colors.blue))),
                transformedElement(coloredElement(rectangle(vector(1, 10)), Fills.solid(Colors.blue))) /**/
        )),
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