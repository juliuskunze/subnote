package com.mindforge.graphics.graphics

import com.mindforge.graphics.*
import java.util.SortedMap

trait Fill {
    fun colorAt(location: Vector2): Color
    fun transform(transform: Transform2): TransformedFill = object : TransformedFill {
        override val original = this@Fill
        override val transform = transform
    }
}

object Fills {
    fun solid(color: Color) = object : SolidFill {
        override val color = color
    }

    val invisible = object : InvisibleFill {}

    fun linearGradient(stops: SortedMap<out Number, Color>) = object : LinearGradient {
        override val stops = stops
    }

    fun radialGradient(stops: SortedMap<out Number, Color>) = object : RadialGradient {
        override val stops = stops
    }
}

trait TransformedFill : Fill {
    val original: Fill
    val transform: Transform2
    override fun colorAt(location: Vector2) = original.colorAt(transform.inverse()(location))
}

trait InvisibleFill : SolidFill {
    override val color: Color get() = Colors.transparent
}

trait SolidFill : Fill {
    val color: Color
    override fun colorAt(location: Vector2) = color
}

trait Gradient : Fill {
    val stops: SortedMap<out Number, Color>
    protected fun colorAt(location: Number): Color {
        val l = location.toDouble()
        val entries = stops.entrySet()

        val equalStops = entries filter { it.key.toDouble() == l }
        if (!equalStops.empty) return equalStops.single().value

        val nextBiggerStop = (entries filter { it.key.toDouble() > l }).firstOrNull()
        val nextSmallerStop = (entries filter { it.key.toDouble() < l }).lastOrNull()

        if (nextBiggerStop == null && nextSmallerStop == null) return Colors.transparent
        if (nextBiggerStop == null) return nextSmallerStop!!.value
        if (nextSmallerStop == null) return nextBiggerStop.value

        val portion = (l - nextSmallerStop.key.toDouble()) / (nextBiggerStop.key.toDouble() - nextSmallerStop.key.toDouble())
        return nextSmallerStop.value * (1 - portion) + nextBiggerStop.value * portion
    }
}

trait LinearGradient : Gradient {
    override fun colorAt(location: Vector2) = super<Gradient>.colorAt(location.x.toDouble())
}

trait RadialGradient : Gradient {
    override fun colorAt(location: Vector2) = super<Gradient>.colorAt(location.length)
}