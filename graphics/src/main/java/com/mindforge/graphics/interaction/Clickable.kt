package com.mindforge.graphics.interaction

import com.mindforge.graphics.*
import com.mindforge.graphics.math.*
import com.mindforge.graphics.*

/*trait Clickable<T> : PointersElement<T> {
    /*private val pressedKeysThatCouldLeadToClick: MutableCollection<PointerKey>

    override fun onPointerKeyPressed(pointerKey: PointerKey) {
        pressedKeysThatCouldLeadToClick.add(pointerKey)
    }

    override fun onPointerKeyReleased(pointerKey: PointerKey) {
        if (pressedKeysThatCouldLeadToClick.contains(pointerKey)) {
            pressedKeysThatCouldLeadToClick.remove(pointerKey)
            onClick(pointerKey)
        }
    }

    override fun onPointerLeaved(pointer: Pointer) {
        if (pressedKeysThatCouldLeadToClick.contains(pointer)) {
            pressedKeysThatCouldLeadToClick.remove(pointer)
        }
    }*/

    fun onClick(pointerKey: PointerKey) {}
}*/
