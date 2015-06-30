package com.mindforge.graphics.interaction

import com.mindforge.graphics.*
import java.util.ArrayList
import java.util.HashMap
import kotlin.properties.Delegates

open class Draggable(element: Element<*>, startLocation: Vector2 = zeroVector2) : DraggableBase(element, startLocation) {
    val dropped = trigger<PointerKey>()
    val moved = trigger<Pointer>()

    private val observers = ArrayList<Observer>()

    override fun onPointerKeyPressed(pointerKey: PointerKey) {
        startDrag(pointerKey)
    }

    fun startDrag(pointerKey: PointerKey) {
        observers.add(pointerKey.pointer.moved addObserver { onMoved(pointerKey.pointer) })

        pointerKey.key.released addObserver {
            stop()
            observers.forEach { it.stop() }
            observers.clear()
            dropped(pointerKey)
        }
    }

    fun onMoved(pointer: Pointer) {
        location = pointer.location
        moved(pointer)
    }
}

open class DraggableBase(val element: Element<*>, startLocation: Vector2 = zeroVector2) : Composed<Any?>, PointersElement<Any?> {
    override val content: Any? get() = element.content
    override val changed = trigger<Unit>()
    var location by Delegates.observed(startLocation, changed)

    override val elements: ObservableIterable<TransformedElement<*>> = observableIterable(listOf(object : TransformedElement<Any?> {
        override val element: Element<Any?> = this@DraggableBase.element
        override val transform: Transform2 get() = Transforms2.translation(this@DraggableBase.location)
        override val transformChanged = this@DraggableBase.changed
    }))
}