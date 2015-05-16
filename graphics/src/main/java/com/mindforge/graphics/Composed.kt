package com.mindforge.graphics

import com.mindforge.graphics
import com.mindforge.graphics.math.Shape
import com.mindforge.graphics.math.shape
import kotlin.properties.Delegates

trait TransformedElement<T> {
    val element: Element<T>
    val transform: Transform2
    val transformChanged: Observable<Unit>
}

fun transformedElement<T>(element: Element<T>, transform: Transform2 = Transforms2.identity): TransformedElement<T> = object : TransformedElement<T> {
    override val element = element
    override val transform = transform
    override val transformChanged: Observable<Unit> = observable()
}

class MutableTransformedElement<T>(element: Element<T>, transform: Transform2 = Transforms2.identity) : TransformedElement<T> {
    override val element = element
    private val transformChangedTrigger = trigger<Unit>()
    override var transform by Delegates.observing(transform, transformChangedTrigger)
    override val transformChanged: Observable<Unit> = transformChangedTrigger
}

trait Composed<T> : Element<T> {
    fun elementsAt(location: Vector2): Iterable<TransformedElement<*>> = elements.flatMap {
        val locationRelativeToElement = it.transform.inverse()(location)
        val element = it.element
        val subElements: List<TransformedElement<*>> = when (element) {
            is Composed -> element.elementsAt(locationRelativeToElement).map { subElement ->
                transformedElement(subElement.element, it.transform before subElement.transform)
            }
            else -> listOf()
        }
        subElements + if (when (element) {
            is Composed -> subElements.any()
            else -> element.shape.contains(locationRelativeToElement)
        }) listOf(it) else listOf()
    }

    val elements: ObservableIterable<TransformedElement<*>>

    override val shape: Shape get() = shape { elementsAt(it).any() }

    final fun allElements(): Set<Element<*>> = setOf(this) union elements.flatMap {
        it.element.let {
            when (it) {
                is Composed<*> -> it.allElements()
                else -> setOf(it)
            }
        }
    }

    protected final fun containsRecursive(element: Element<*>): Boolean {
        val elements = elements.map { it.element }
        return elements.contains(element) || elements.any { it is Composed<*> && it.containsRecursive(element) }
    }

    protected final fun pathTo(recursiveElement: Element<*>): List<TransformedElement<*>> = elements.flatMap {
        val element = it.element
        if (recursiveElement === element) listOf(it)
        else if (element is Composed<*> && element.containsRecursive(recursiveElement)) (listOf(it) + element.pathTo(recursiveElement))
        else listOf()
    }

    final fun totalTransform(recursiveElement: Composed<*>): Transform2 {
        val path = pathTo(recursiveElement = recursiveElement)

        return path.fold(Transforms2.identity, { transform, composed -> transform.before(composed.transform) })
    }
}

fun composed<T>(
        content: T,
        elements: ObservableIterable<TransformedElement<*>>,
        changed: Observable<Unit> = observable<Unit>()) = object : Composed<T> {
    override val content = content
    override val elements = elements
    override val changed = changed
}

fun composed<T>(content: T, elements: List<TransformedElement<*>>, changed: Observable<Unit> = observable<Unit>()): Composed<T> = composed(content, observableIterable(elements), changed)

fun composed(elements: ObservableIterable<TransformedElement<*>>, changed: Observable<Unit> = observable<Unit>()) = composed(Unit, elements, changed)