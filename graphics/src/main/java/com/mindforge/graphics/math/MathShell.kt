package com.mindforge.graphics.math

import com.mindforge.graphics.*

public trait MathShell {
    public companion object : Constructor<MathShell>, Constructor1<MathShell, MathEnvironment> {
        override fun invoke(e: MathEnvironment): MathShell = object : MathShell {
            override var environment: MathEnvironment = e
        }
        override fun invoke(): MathShell = invoke(MathEnvironment.DefaultAccurate)
    }
    var environment: MathEnvironment
    fun activate() {
        activeShell = this
    }
    fun isActive(): Boolean = this == activeShell
    // TODO: do more things this Shell is good for
}

var activeShell: MathShell = MathShell.invoke()