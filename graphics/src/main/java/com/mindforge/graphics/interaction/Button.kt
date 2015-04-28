package com.mindforge.graphics.interaction

import com.mindforge.graphics.*
import com.mindforge.graphics.*
import com.mindforge.graphics.math.*

trait Button : Clickable<Trigger<Unit>>, ColoredElement<Trigger<Unit>> {
    override fun onClick(pointerKey: PointerKey) = content()
}

fun button(
        trigger: Trigger<Unit> = trigger<Unit>(),
        shape: Shape,
        fill: Fill,
        changed: Observable<Unit> = observable(),
        onClick: () -> Unit = {}) = object : Button {
    override val content = trigger
    override val shape = shape
    override val fill = fill
    override val changed = changed

    init {
        content addObserver { onClick() }
    }
}