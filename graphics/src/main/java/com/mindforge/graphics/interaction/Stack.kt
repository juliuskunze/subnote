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
    private val changedTrigger = trigger<Unit>()
    override val changed = changedTrigger

    init {
        initTransforms()

        stackElements.added addObserver {
            elements.add(MutableTransformedElement(it.element))
            initTransforms()
        }
        stackElements.removed addObserver {
            elements.remove(MutableTransformedElement(it.element))
            initTransforms()
        }

        stackElements.mapObservable { it.sizeChanged }.startKeepingAllObserved {
            initTransforms()
        }
    }

    fun initTransforms() {
        elements.clear()

        var partialTransformation = zeroVector2

        for (e in stackElements) {
            elements.add(MutableTransformedElement(e.element, Transforms2.translation(partialTransformation)))

            partialTransformation += e.partialTranslation()
        }
    }
}