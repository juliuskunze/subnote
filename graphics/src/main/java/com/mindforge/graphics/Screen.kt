package com.mindforge.graphics

import com.mindforge.graphics.*
import com.mindforge.graphics.interaction.*
import com.mindforge.graphics.math.*

interface Screen {
    var content: Composed<*>
    val shape: Shape
    fun elementsAt(location: Vector2) = content.elementsAt(location)
}