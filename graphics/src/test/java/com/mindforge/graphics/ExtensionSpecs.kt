package com.mindforge.graphics

import org.jetbrains.spek.api.*
import com.mindforge.graphics.*

public class ExtensionSpecs : Spek() {init {
    given("a [2 3 4] array") {
        val a = array(2, 3, 4)
        on("testing the replaceElements function") {
            it("should be [4 6 8]") {
                val b = a.replaceElements { 2 * it }
                shouldEqual(4, b[0])
                shouldEqual(6, b[1])
                shouldEqual(8, b[2])
            }
        }
    }
}
}