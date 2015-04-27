package com.mindforge.graphics.graphics

import com.mindforge.graphics.math.*
import com.mindforge.graphics.*

trait TransformedElement<T> {
    val element: Element<T>
    val transform: Transform2
}

fun transformedElement<T>(element: Element<T>, transform: Transform2 = Transforms2.identity): TransformedElement<T> = object : TransformedElement<T> {
    override val element = element
    override val transform = transform
}

trait Composed<T> : Element<T> {
    fun elementsAt(location: Vector2): Iterable<TransformedElement<*>> = elements flatMap {
        val transformedLocation = it.transform.inverse()(location)
        val contains = it.element.shape.contains(transformedLocation)

        if (!contains) listOf<TransformedElement<*>>() else if (it is Composed<*>) it.elementsAt(transformedLocation) map {x -> transformedElement(it, it.transform before x.transform)} else listOf(it)}

    val elements : ObservableIterable<TransformedElement<*>>

    //fun elementsRecursively() : Iterable<Element<*>>  = elements flatMap { if(it is Composed<*>) it.elementsRecursively() + it else listOf(it) }
}

fun composed<T>(
        content: T,
        elements: ObservableIterable<TransformedElement<*>>,
        changed: Observable<Unit> = observable<Unit>()) = object : Composed<T> {
    override val content = content
    override val elements = elements
    override val shape = concatenatedShape(elements mapObservable { it.element.shape transformed it.transform })
    override val changed = changed
}

fun composed(elements: ObservableIterable<TransformedElement<*>>, changed: Observable<Unit> = observable<Unit>()) = composed(Unit, elements, changed)

fun observableIterable<T>(
        elements: Iterable<T>,
        added: Observable<T> = observable<T>(),
        removed: Observable<T> = observable<T>()) = object : ObservableIterable<T> {
    override val added = added
    override val removed = removed
    override fun iterator() = elements.iterator()
}
