package com.mindforge.graphics.interaction

import com.mindforge.graphics.*
import com.mindforge.graphics.math.*
import kotlin.properties.Delegates

class Scrollable(element: Element<*>, val nearestValidLocation: (Vector2) -> Vector2 = {it}, startLocation: Vector2 = zeroVector2) : DraggableBase(element, startLocation) {
    private var lastLocation: Vector2? = null

    override fun onPointerKeyPressed(pointerKey: PointerKey) {
        lastLocation = pointerKey.pointer.location
    }

    override fun onPointerKeyReleased(pointerKey: PointerKey) {
        lastLocation = null
    }

    override fun onPointerMoved(pointer: Pointer) {
        val last = lastLocation
        val current = pointer.location
        if (last != null) {
            location = nearestValidLocation(location + current - last)
            lastLocation = current
            changed()
        }
    }
}