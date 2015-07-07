package net.pureal.subnote.prototree

import com.mindforge.graphics.ObservableArrayList
import com.mindforge.graphics.TransformedElement
import com.mindforge.graphics.observableArrayListOf
import jquery.jq
import net.pureal.graphics.js.CanvasScreen
import org.w3c.dom.HTMLCanvasElement
import kotlin.browser.document

fun main(args: Array<String>) {
    jq {
        val screen = CanvasScreen(document.getElementById("canvas") as HTMLCanvasElement)
        screen.content = TreeNodeElement(Tree(randomNode()))
    }
}
