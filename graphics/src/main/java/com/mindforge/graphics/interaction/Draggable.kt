package com.mindforge.graphics.interaction

import com.mindforge.graphics.*
import java.util.ArrayList

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

    val pointers = ArrayList<Pointer>()

    val onRelease: (Key) -> Unit = { key: Key ->
        key.released removeObserver onRelease
        pointers.forEach {
            it.moved removeObserver onDrag
        }
        pointers.clear()
    }

    override fun onPointerKeyPressed(pointerKey: PointerKey) {
        pointers.add(pointerKey.pointer)
        pointerKey.pointer.moved addObserver onDrag
        pointerKey.key.released addObserver onRelease
    }

}