package net.pureal.graphics.js

import com.mindforge.graphics.Color

val hexDigits = ('0'..'9') + ('A'..'F')
fun Int.toHexNibble() = hexDigits[this % 16]
fun Int.toHexByte() = "${div(16).toHexNibble()}${toHexNibble()}"
val Color.htmlCode: String get() = listOf(r, g, b).map { it.toDouble().times(255).toInt().toHexByte() }.joinToString("", "#")