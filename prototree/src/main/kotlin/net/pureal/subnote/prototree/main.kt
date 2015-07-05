package net.pureal.subnote.prototree

import com.mindforge.graphics.*
import com.mindforge.graphics.math.circle
import jquery.jq
import net.pureal.graphics.js.CanvasScreen
import org.w3c.dom.HTMLCanvasElement
import kotlin.browser.document
import kotlin.browser.window

fun main(args: Array<String>) {
    jq {
        val screen = CanvasScreen(document.getElementById("canvas") as HTMLCanvasElement)
        val radius = 50
        val ball = MutableTransformedElement(coloredElement(circle(radius), Fills.solid(Colors.red)))
        val elements: ObservableArrayList<TransformedElement<*>> = observableArrayListOf(ball)
        screen.content = composed(elements)
        var direction = vector(10, 10)
        var location = vector(radius, radius)
        window.setInterval({
            val width = screen.shape.size.x.toDouble()
            val height = screen.shape.size.y.toDouble()
            if ((location + direction).x.toDouble() < radius) direction = vector(Math.abs(direction.x.toDouble()), direction.y)
            if ((location + direction).x.toDouble() > width-radius) direction = vector(-Math.abs(direction.x.toDouble()), direction.y)
            if ((location + direction).y.toDouble() < radius) direction = vector(direction.x, Math.abs(direction.y.toDouble()))
            if ((location + direction).y.toDouble() > height-radius) direction = vector(direction.x, -Math.abs(direction.y.toDouble()))
            location += direction
            ball.transform = Transforms2.translation(location)
            true
        }, 20)
    }
}
