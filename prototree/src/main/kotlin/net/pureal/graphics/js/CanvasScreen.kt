package net.pureal.graphics.js

import com.mindforge.graphics.*
import com.mindforge.graphics.math.Rectangle
import com.mindforge.graphics.math.rectangle
import kotlin.js.dom.html.window
import kotlin.js.dom.html5.HTMLCanvasElement

class CanvasScreen(var canvas: HTMLCanvasElement) : Screen {

    val context = canvas.getContext("2d")!!

    init {
        window.setInterval({
            context.fillStyle = Colors.white.htmlCode
            context.fillRect(0.0, 0.0, canvas.width, canvas.height)
        }, 20)
    }

    override var content: Composed<*> = composed(observableIterable(emptyList()))

    override val shape: Rectangle get() = rectangle(vector(canvas.width, canvas.height))

}