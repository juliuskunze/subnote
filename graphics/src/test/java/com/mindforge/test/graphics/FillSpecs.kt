package com.mindforge.test.graphics

import org.jetbrains.spek.api.*
import com.mindforge.graphics.*
import com.mindforge.graphics.*
import com.mindforge.graphics.math.*

class FillSpecs : Spek() {init {
    given("a solid fill") {
        val x = Fills.solid(Colors.blue)

        listOf(vector(2, 2), vector(-4.43, 0), vector(1, -23234)).map({ location -> x.colorAt(location) }).forEach {
            on("getting the color at the arbitrary location ${it}") {
                it("should be the one defined color") {
                    shouldEqual(Colors.blue, it)
                }
            }
        }
    }

    given("an invsible fill") {
        val x = Fills.invisible

        listOf(vector(2, 2), vector(-4.43, 0), vector(1, -23234)).map({ location -> x.colorAt(location) }).forEach {
            on("getting the color at the arbitrary location ${it}") {
                it("should be transparent") {
                    shouldEqual(Colors.transparent, it)
                }
            }
        }
    }

    given("a linear gradient fill from black to white stop 0 to 2") {
        val x = Fills.linearGradient(sortedMapOf(Pair<Number, Color>(0, Colors.black), Pair<Number, Color>(2, Colors.white)))

        on("getting the color at a location with x-coordinate 0") {
            val c = x.colorAt(vector(0, -4.5))

            it("should be black") {
                shouldEqual(Colors.black, c)
            }
        }

        on("getting the color at a location with x-coordinate 1.5") {
            val c = x.colorAt(vector(1.5, -17))

            it("should be gray (75%)") {
                shouldEqual(Colors.gray(.75), c)
            }
        }

        on("getting the color at a location with x-coordinate 2") {
            val c = x.colorAt(vector(2, 1400.234))

            it("should be white") {
                shouldEqual(Colors.white, c)
            }
        }

        on("getting the color at a location with an x-coordinate greater than 2") {
            val c = x.colorAt(vector(300, 0))

            it("should be white") {
                shouldEqual(Colors.white, c)
            }
        }

        on("getting the color at a location with x-coordinate smaller than 0") {
            val c = x.colorAt(vector(-300, 0))

            it("should be black") {
                shouldEqual(Colors.black, c)
            }
        }
    }

    given("a radial gradient fill from black to white stop 0 to 2") {
        val x = Fills.radialGradient(sortedMapOf(Pair<Number, Color>(0, Colors.black), Pair<Number, Color>(2, Colors.white)))

        on("getting the color at the origin") {
            val c = x.colorAt(vector(0, 0))

            it("should be black") {
                shouldEqual(Colors.black, c)
            }
        }

        on("getting the color at distance 1 from the origin in x-direction") {
            val c = x.colorAt(vector(1, 0))

            it("should be gray (50%)") {
                shouldEqual(Colors.gray(.5), c)
            }
        }

        on("getting the color at distance 1 from the origin in y-direction") {
            val c = x.colorAt(vector(0, 1))

            it("should be gray (50%)") {
                shouldEqual(Colors.gray(.5), c)
            }
        }

        on("getting the color at distance 2 from the origin in counter-y-direction") {
            val c = x.colorAt(vector(0, -2))

            it("should be white") {
                shouldEqual(Colors.white, c)
            }
        }

        on("getting the color at a distance greater than 2 from the origin") {
            val c = x.colorAt(vector(24, -2))

            it("should be white") {
                shouldEqual(Colors.white, c)
            }
        }
    }
}
}
