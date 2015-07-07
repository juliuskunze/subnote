package net.pureal.graphics.js

import com.mindforge.graphics.*
import com.mindforge.graphics.math.Circle
import com.mindforge.graphics.math.Rectangle
import com.mindforge.graphics.math.rectangle
import jquery.jq
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import kotlin.browser.window

class CanvasScreen(var canvas: HTMLCanvasElement) : Screen {

    val context = canvas.getContext("2d") as CanvasRenderingContext2D

    init {
        resize()
        window.setInterval({
            context.fillStyle = Colors.white.htmlCode
            context.resetTransform()
            context.fillRect(0.0, 0.0, canvas.width.toDouble(), canvas.height.toDouble())
            context.draw(content)
        }, 20)
        window.onresize = { resize() }
    }

    private fun resize(): Boolean {
        val parent = jq(canvas.parentElement!!)
        canvas.width = parent.width().toInt()
        canvas.height = Math.min(parent.height().toInt(), window.innerHeight.toInt())
        return true
    }

    override var content: Composed<*> = composed(observableIterable(emptyList()))

    override val shape: Rectangle get() = rectangle(vector(canvas.width, canvas.height))

}

fun CanvasRenderingContext2D.draw(element: Element<*>) {
    when (element) {
        is Composed<*> -> {
            element.elements.forEach {
                save()
                transform(it.transform)
                draw(it.element)
                restore()
            }
        }
        is ColoredElement<*> -> {
            val fill = element.fill
            val shape = element.shape
            when (fill) {
                is SolidFill -> fillStyle = fill.color.htmlCode
                else -> throw UnsupportedOperationException("Cannot draw fill $fill.")
            }
            when (shape) {
                is Circle -> fillPath { arc(0.0, 0.0, shape.radius.toDouble(), 0.0, 2 * Math.PI, false) }
                else -> throw UnsupportedOperationException("Cannot draw shape $shape.")
            }
        }
        else -> throw UnsupportedOperationException("Cannot draw element $element.")
    }
}

fun CanvasRenderingContext2D.fillPath(constructPath: CanvasRenderingContext2D.() -> Unit) {
    beginPath()
    constructPath()
    closePath()
    fill()
}

fun CanvasRenderingContext2D.transform(transform: Transform2) = transform.matrix.let {
    this.transform(it.a.toDouble(), it.d.toDouble(), it.b.toDouble(), it.e.toDouble(), it.c.toDouble(), it.f.toDouble())
}
