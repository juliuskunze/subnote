package com.mindforge.graphics.interaction

import com.mindforge.graphics.*
import java.util.ArrayList
import java.util.HashMap
import kotlin.properties.Delegates

open class Draggable(val element: Element<*>, dragLocation: Vector2 = zeroVector2) : Composed<Any?>, PointersElement<Any?> {
    override val changed = trigger<Unit>()

    var dragLocation by Delegates.observing(dragLocation, changed)
    override val content: Any? get() = element.content

    override val elements: ObservableIterable<TransformedElement<*>> = observableIterable(listOf(object : TransformedElement<Any?> {
        override val element: Element<Any?> = this@Draggable.element
        override val transform: Transform2 get() = Transforms2.translation(this@Draggable.dragLocation)
        override val transformChanged = this@Draggable.changed
    }))

    fun onMoved(pointerKey: PointerKey) {
        dragLocation = pointerKey.pointer.location
        moved(pointerKey)
    }

    val dropped = trigger<PointerKey>()
    val moved = trigger<PointerKey>()

    val observers = ArrayList<Observer>()

    override fun onPointerKeyPressed(pointerKey: PointerKey) {
        observers.add(pointerKey.pointer.moved addObserver { onMoved(pointerKey) })

        pointerKey.key.released addObserver {
            stop()
            observers.forEach { it.stop() }
            observers.clear()
            dropped(pointerKey)
        }
    }

    fun registerDragOnMove(pointerKey : PointerKey) {
        pointerKey.pointer.moved addObserver {
            stop()
            onPointerKeyPressed(pointerKey)
        }
    }
}