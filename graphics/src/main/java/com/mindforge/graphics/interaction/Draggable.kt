package com.mindforge.graphics.interaction

import com.mindforge.graphics.*
import java.util.ArrayList
import java.util.HashMap

class Draggable(val element: Element<*>, var dragPosition: Vector2 = zeroVector2) : Composed<Any?>, PointersElement<Any?> {
    override val content: Any? get() = element.content
    override val changed = trigger<Unit>()

    override val elements: ObservableIterable<TransformedElement<*>> = observableIterable(listOf(object : TransformedElement<Any?> {
        override val element: Element<Any?> = this@Draggable.element
        override val transform: Transform2 get() = Transforms2.translation(dragPosition)
        override val transformChanged = this@Draggable.changed
    }))

    val onDrag = { pointer: Pointer ->
        dragPosition = pointer.location
        changed()
    }

    val pointerObservers = ArrayList<Observer>()

    override fun onPointerKeyPressed(pointerKey: PointerKey) {
        pointerObservers.add(pointerKey.pointer.moved addObserver { onDrag(it) })

        pointerKey.key.released addObserver {
            stop()
            pointerObservers.forEach { it.stop() }
            pointerObservers.clear()
        }
    }
}