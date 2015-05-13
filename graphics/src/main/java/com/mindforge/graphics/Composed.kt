package com.mindforge.graphics

import com.mindforge.graphics.math.*
import com.mindforge.graphics.*
import java.util.ArrayList

trait TransformedElement<T> {
    val element: Element<T>
    val transform: Transform2
    val transformChanged: Observable<Unit> get() = observable()
}

fun transformedElement<T>(element: Element<T>, transform: Transform2 = Transforms2.identity): TransformedElement<T> = object : TransformedElement<T> {
    override val element = element
    override val transform = transform
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
fun composed(elements: List<TransformedElement<*>>, changed: Observable<Unit> = observable<Unit>()) = composed(Unit, elements, changed)

fun observableIterable<T>(
        elements: Iterable<T>,
        added: Observable<T> = observable<T>(),
        removed: Observable<T> = observable<T>()) = object : ObservableIterable<T> {
    override val added = added
    override val removed = removed
    override fun iterator() = elements.iterator()
}