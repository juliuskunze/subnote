package com.mindforge.graphics.math

import com.mindforge.graphics.math.operations.*
import com.mindforge.graphics.*

public trait MathEnvironment {
    companion object {
        public abstract class DefaultFunctionEnv : MathEnvironment {
            override val intReal: Constructor1<InternalReal, Any?> = basicRealInf
            override val addVal: Constructor2<RealBinaryOperation, Real, Real> = AdditionValue
            override val subVal: Constructor2<RealBinaryOperation, Real, Real> = SubtractionValue
            override val mulVal: Constructor2<RealBinaryOperation, Real, Real> = MultiplicationValue
            override val divVal: Constructor2<RealBinaryOperation, Real, Real> = DivisionValue
            override val simplifier: RealSimplifier = object : RealSimplifier {
                override fun simplify(r: Real): Real {
                    return com.mindforge.graphics.math.simplifier.simplify(r)
                }
            }
        }
        val DefaultAccurate = object : DefaultFunctionEnv() {
            override var accuracy: Int = 100
            override var requireExact: Boolean = true
        }
    }

    var accuracy: Int
    var requireExact: Boolean

    val intReal: Constructor1<InternalReal, Any?>
    val addVal: Constructor2<RealBinaryOperation, Real, Real>
    val subVal: Constructor2<RealBinaryOperation, Real, Real>
    val mulVal: Constructor2<RealBinaryOperation, Real, Real>
    val divVal: Constructor2<RealBinaryOperation, Real, Real>

    val simplifier: RealSimplifier
    // TODO: make up more things that should be controlled by the environment
}

public val activeEnvironment: MathEnvironment get() = activeShell.environment