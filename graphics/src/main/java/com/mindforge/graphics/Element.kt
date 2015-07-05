package com.mindforge.graphics

import com.mindforge.graphics.interaction.Interactive
import com.mindforge.graphics.math.Shape

interface Element<out T> : Interactive<T> {
    val shape: Shape
    val changed: Observable<Unit> get() = observable()
}