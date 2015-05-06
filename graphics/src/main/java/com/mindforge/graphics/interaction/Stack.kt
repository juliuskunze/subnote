package com.mindforge.graphics.interaction

import com.mindforge.graphics.*

class Stackable(val element: Element<*>, val size: Vector2)

fun horizontalStack(elements: List<Stackable>) = stack(elements, horizontal = true)
fun verticalStack(elements: List<Stackable>) = stack(elements, horizontal = false)

fun stack(elements: List<Stackable>, horizontal: Boolean): List<TransformedElement<*>> {
    val transformedElements = arrayListOf<TransformedElement<*>>()

    var totalTransformation = zeroVector2
    for (e in elements) {
        transformedElements.add(transformedElement(e.element, Transforms2.translation(totalTransformation)))

        totalTransformation += (if (horizontal) e.size.xComponent() else e.size.yComponent())
    }

    return transformedElements
}