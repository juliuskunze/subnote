package com.mindforge.graphics.graphics

import com.mindforge.graphics.*
import com.mindforge.graphics.interaction.*
import com.mindforge.graphics.math.*

trait Screen {
    var content: Composed<*>
    val shape: Shape
}