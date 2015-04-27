package com.mindforge.graphics.graphics

import com.mindforge.graphics.math.*
import com.mindforge.graphics.*
import com.mindforge.graphics.interaction.*

trait Element<out T> : Interactive<T> {
    val shape: Shape
    val changed: Observable<Unit> get() = observable()
}