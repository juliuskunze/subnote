package com.mindforge.graphics.interaction

import com.mindforge.graphics.*
import com.mindforge.graphics.math.TranslatedRectangle
import kotlin.properties.Delegates

class Stackable(element: Element<*>, shape: TranslatedRectangle) {
    val element = element
    private val sizeChangedTrigger = trigger<Unit>()
    var shape by Delegates.observed(shape, sizeChangedTrigger)
    val shapeChanged: Observable<Unit> = sizeChangedTrigger
}

fun horizontalStack(elements: ObservableIterable<Stackable>) = Stack(elements, horizontal = true)
fun verticalStack(elements: ObservableIterable<Stackable>) = Stack(elements, horizontal = false)

class Stack(val stackElements: ObservableIterable<Stackable>, val horizontal: Boolean) : Composed<Unit> {
    private fun Stackable.partialTranslation() = if (horizontal) shape.original.size.xComponent() else -shape.original.size.yComponent()
    private fun Stackable.offset(): Vector2 {
        val c = shape.centerLocation
        val s = shape.original.size
        return (if (horizontal) c.xComponent() else c.yComponent()) -
                (if (horizontal) s.xComponent() else -s.yComponent()) / 2
    }

    override val elements = ObservableArrayList<TransformedElement<*>> ()

    override val content = Unit
    override val changed = trigger<Unit>()

    init {
        initTransforms()
    }

    private val observer1 = stackElements.mapObservable { it.shapeChanged }.startKeepingAllObserved {
        initTransforms()
    }

    private val observer2 = stackElements.added addObserver {
        elements.add(MutableTransformedElement(it.element))
        initTransforms()
    }

    private val observer3 = stackElements.removed addObserver  {
        elements.remove(MutableTransformedElement(it.element))
        initTransforms()
    }

    fun removeObservers() {
        observer1.stop()
        observer2.stop()
        observer3.stop()
    }

    private fun initTransforms() {
        elements.clear()

        var partialTransformation = zeroVector2

        for (e in stackElements) {
            elements.add(MutableTransformedElement(e.element, Transforms2.translation(partialTransformation - e.offset())))

            partialTransformation += e.partialTranslation()
        }

        changed()
    }

    fun length() = stackElements.map {
        val size = it.shape.original.size
        (if(horizontal) size.x else size.y).toDouble()
    }.sum()
}