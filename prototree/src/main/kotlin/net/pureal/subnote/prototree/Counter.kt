package net.pureal.subnote.prototree

import jquery.JQuery
import kotlin.js.dom.html.window

public class Counter(val el: JQuery) {

    private var d = 1

    fun step(n: Int) {
        el.text("Counter: $n")
        window.setTimeout({ step(n + d) }, 1000)
    }

    fun start() {
        step(0)
    }

    fun reverse() {
        d *= -1;
    }
}