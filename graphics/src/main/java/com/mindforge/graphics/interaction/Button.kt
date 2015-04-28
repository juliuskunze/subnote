package com.mindforge.graphics.interaction

import com.mindforge.graphics.*
import com.mindforge.graphics.math.Shape

trait Button : Clickable<Trigger<Unit>>, Composed<Any?> {
    override fun onClick(pointerKey: PointerKey) = content()
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