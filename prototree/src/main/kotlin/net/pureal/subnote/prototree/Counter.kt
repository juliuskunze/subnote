package net.pureal.subnote.prototree

import org.w3c.dom.Text
import kotlin.js.dom.html.document
import kotlin.js.dom.html.window

public class Counter(val el: Text) {
    fun step(n: Int) {
        el.textContent="Counter: $n"
        window.setTimeout({step(n+1)}, 1000)
    }

    fun start() {
        step(0)
    }
}