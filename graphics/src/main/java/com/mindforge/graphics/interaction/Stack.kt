package com.mindforge.graphics.interaction

import com.mindforge.graphics.*
import kotlin.properties.Delegates

class Stackable(element: Element<*>, size: Vector2) {
    val element = element
    var size by Delegates.observable(initial = size) { meta, old, new -> sizeChangedTrigger() }
    private val sizeChangedTrigger = trigger<Unit>()
    val sizeChanged: Observable<Unit> = sizeChangedTrigger
}

fun horizontalStack(elements: ObservableIterable<Stackable>) = Stack(elements, horizontal = true)
fun verticalStack(elements: ObservableIterable<Stackable>) = Stack(elements, horizontal = false)

class Stack(val stackElements: ObservableIterable<Stackable>, val horizontal: Boolean) : Composed<Unit> {
    private fun Stackable.partialTranslation() = if (horizontal) size.xComponent() else -size.yComponent()

    override val elements = ObservableArrayList<TransformedElement<*>> ()

    override val content = Unit
    override val changed = trigger<Unit>()

    init {
        initTransforms()
    }

    val observer1 = stackElements.mapObservable { it.sizeChanged }.startKeepingAllObserved {
        initTransforms()
    }

    val observer2 = stackElements.added addObserver {
        elements.add(MutableTransformedElement(it.element))
        initTransforms()
    }

    val observer3 = stackElements.removed addObserver  {
        elements.remove(MutableTransformedElement(it.element))
        initTransforms()
    }

    fun removeObservers() {
        observer1.stop()
        observer2.stop()
        observer3.stop()
    }

    fun initTransforms() {
        elements.clear()

        var partialTransformation = zeroVector2

        for (e in stackElements) {
            elements.add(MutableTransformedElement(e.element, Transforms2.translation(partialTransformation)))

            partialTransformation += e.partialTranslation()
        }

        changed()
    }
}