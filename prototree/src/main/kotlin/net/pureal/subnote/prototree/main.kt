package net.pureal.subnote.prototree

import jquery.jq

fun main(args: Array<String>) {

    val heading = jq("#heading")

    heading.text("Hello Kotlin!")
    val counter = Counter(jq("#main"))
    counter.start()

    jq("#dontclick").click {
        heading.text("I said, don't click!")
        counter.reverse()
    }

}