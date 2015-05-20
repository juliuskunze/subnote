package com.mindforge.graphics.android

import android.opengl.GLES20
import com.mindforge.graphics.*
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import java.util.Collections
import java.util.concurrent.CopyOnWriteArrayList


abstract class GlElement(val original: Element<*>, val screen: GlScreen) : Element<Any?> {
    override val shape: GlShape get() = glShape(original.shape)
    override val changed: Observable<Unit> get() = original.changed
    override val content: Any? get() = original.content;
    abstract fun draw(parentTransform: GlTransform)

    protected fun onChanged() {
        screen.requestRender()
    }

    private val changedObserver = changed.addObserver { onChanged() }

    open fun detach() {
        changedObserver.stop()
        isDetached = true
    }

    var isDetached = false
        private set
}

class GlTransformedElement(val originalTransformedElement: TransformedElement<*>, screen: GlScreen) : TransformedElement<Any?> {
    override val element = screen.glElement(originalTransformedElement.element)
    override var transform: GlTransform = glTransform(originalTransformedElement.transform)
    override val transformChanged = originalTransformedElement.transformChanged

    //TODO: introduce delegate for this pattern
    init {
        originalTransformedElement.transformChanged addObserver {
            transform = glTransform(originalTransformedElement.transform)
        }
    }
}

class GlComposed(val originalComposed: Composed<*>, screen: GlScreen) : GlElement(originalComposed, screen), Composed<Any?> {
    val glElementList = CopyOnWriteArrayList(originalComposed.elements.mapObservable { GlTransformedElement(it, screen) }.toList())
    private trait DetachableObservableIterable<T> : ObservableIterable<T> {
        fun detach()
    }
    override val elements = object : DetachableObservableIterable<TransformedElement<*>> {
        override fun iterator() = glElementList.iterator()
        override val added = trigger<TransformedElement<*>>()
        override val removed = trigger<TransformedElement<*>>()

        override val addedAt = trigger<IndexedValue<TransformedElement<*>>>()
        override val removedAt = trigger<IndexedValue<TransformedElement<*>>>()

        val addedObserver = originalComposed.elements.added addObserver { addedElement ->
            val glElement = GlTransformedElement(addedElement, screen)
            glElementList.add(glElement)
            added(glElement)
            onChanged()
        }
        val removedObserver = originalComposed.elements.removed addObserver { removedElement ->
            val glElement = (glElementList.singleOrNull { element -> element.element.original === removedElement.element })
            if (glElement != null) {
                glElementList.remove(glElement)
                removed(glElement)
            } else {
                throw IllegalStateException("Failed to remove OpenGL element for $removedElement")
            }
            onChanged()
        }

        val addedAtObserver = originalComposed.elements.addedAt addObserver { added ->
            val glElement = GlTransformedElement(added.value, screen)
            glElementList.add(added.index, glElement)
            addedAt(IndexedValue(added.index, glElement))
            onChanged()
        }

        val removedAtObserver = originalComposed.elements.removedAt addObserver { removed ->
            val glElement = glElementList.remove(removed.index)
            removedAt(IndexedValue(removed.index, glElement))
            onChanged()
        }

        override fun detach() {
            addedObserver.stop()
            removedObserver.stop()
            addedAtObserver.stop()
            removedAtObserver.stop()
        }
    }
    override fun detach() {
        elements.detach()
        super<GlElement>.detach()
    }

    override val shape = glShape(super<Composed>.shape)

    override fun draw(parentTransform: GlTransform) = glElementList.reverse() forEach { it.element.draw(it.transform before parentTransform) }
}

open class GlColoredElement(val originalColoredElement: ColoredElement<*>, screen: GlScreen) : GlElement(originalColoredElement, screen), ColoredElement<Any?> {
    override val shape: GlShape  get() = glShape
    override val fill: Fill get() = originalColoredElement.fill

    open val shader = screen.flatShader
    override fun draw(parentTransform: GlTransform) {
        val program = shader.useProgram()

        val matrixHandle = GLES20.glGetUniformLocation(program, "u_Matrix");
        if (matrixHandle != -1) {
            val m = parentTransform.matrix.values
            GLES20.glUniformMatrix4fv(matrixHandle, 1, false, m, 0);
        }

        val positionHandle = GLES20.glGetAttribLocation(program, "a_Position")
        if (positionHandle != -1) {
            GLES20.glEnableVertexAttribArray(positionHandle)
            GLES20.glVertexAttribPointer(positionHandle, 2, GLES20.GL_FLOAT, false, 0, vertexBuffer)
        }

        val texCoordHandle = GLES20.glGetAttribLocation(program, "a_TexCoord")
        if (texCoordHandle != -1) {
            GLES20.glEnableVertexAttribArray(texCoordHandle)
            GLES20.glVertexAttribPointer(texCoordHandle, 2, GLES20.GL_FLOAT, false, 0, uvBuffer)
        }

        val textureName = shape.textureName
        if (textureName != null) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureName)
            val samplerHandle = GLES20.glGetUniformLocation(program, "u_Texture")
            GLES20.glUniform1i(samplerHandle, 0)
        }


        val colorHandle = GLES20.glGetUniformLocation(program, "u_Color")
        val color = fill.colorAt(vector(0, 0))
        GLES20.glUniform4fv(colorHandle, 1, floatArray(
                color.r.toFloat(),
                color.g.toFloat(),
                color.b.toFloat(),
                color.a.toFloat()), 0)

        GLES20.glDrawElements(
                shape.glVertexMode, shape.drawOrder.size(),
                GLES20.GL_UNSIGNED_SHORT, drawListBuffer)

        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(texCoordHandle)
    }

    private var glShape: GlShape = glShape(original.shape)
    private var vertexBuffer = buildVertexBuffer()
    private var uvBuffer = buildUvBuffer()
    private var drawListBuffer = buildDrawListBuffer()
    fun buildVertexBuffer(): FloatBuffer {
        // 4 bytes per float
        val byteBuffer = ByteBuffer.allocateDirect(shape.vertexCoordinates.size * 4)
        byteBuffer order ByteOrder.nativeOrder()
        val floatBuffer = byteBuffer.asFloatBuffer()
        floatBuffer put shape.vertexCoordinates
        floatBuffer position 0
        return floatBuffer
    }

    fun buildUvBuffer(): FloatBuffer {
        // 4 bytes per float
        val byteBuffer = ByteBuffer.allocateDirect(shape.textureCoordinates.size * 4)
        byteBuffer order ByteOrder.nativeOrder()
        val floatBuffer = byteBuffer.asFloatBuffer()
        floatBuffer put shape.textureCoordinates
        floatBuffer position 0
        return floatBuffer
    }

    fun buildDrawListBuffer(): ShortBuffer {
        // 2 bytes per short
        val byteBuffer = ByteBuffer.allocateDirect(shape.drawOrder.size * 2)
        byteBuffer order ByteOrder.nativeOrder()
        val shortBuffer = byteBuffer.asShortBuffer()
        shortBuffer put shape.drawOrder
        shortBuffer position 0
        return shortBuffer
    }

    init {
        changed addObserver {
            //TODO synchronize with GL Thread!!!
            glShape = glShape(original.shape)
            vertexBuffer = buildVertexBuffer()
            uvBuffer = buildUvBuffer()
            drawListBuffer = buildDrawListBuffer()
        }
    }
}
