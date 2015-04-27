package com.mindforge.graphics.math.simplifier

import com.mindforge.graphics.math.*

fun simplify(r: Real): Real {
    // TODO: do it, yeah
    var r: Real = r
    var r_back: Real
    do {
        r_back = r
        // TODO: transform shit
        r = r.calculate()
    } while (r_back != r)
    return r
}