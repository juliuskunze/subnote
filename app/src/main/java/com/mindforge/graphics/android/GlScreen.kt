package com.mindforge.graphics.android

import android.opengl.GLSurfaceView
import android.opengl.GLSurfaceView.Renderer
import javax.microedition.khronos.opengles.GL10
import javax.microedition.khronos.egl.EGLConfig
import android.opengl.GLES20
import android.app.Activity
import com.mindforge.graphics.*
import com.mindforge.graphics.math.*
import com.mindforge.graphics.*
import android.view.MotionEvent
import com.mindforge.graphics.interaction.*
import android.view.KeyEvent

class GlScreen (activity: Activity, onReady: (GlScreen) -> Unit) : GLSurfaceView(activity), Screen {
    init {
        setEGLContextClientVersion(2)
        setEGLConfigChooser(8, 8, 8, 8, 16, 0)
    }
    init {
        setRenderer(object : Renderer {
            override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
                GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
                GLES20.glEnable(GLES20.GL_BLEND)
                GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)
                onReady(this@GlScreen)
            }
            override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
                GLES20.glViewport(0, 0, width, height)
            }
            override fun onDrawFrame(gl: GL10?) {
                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
                glContent.draw(Transforms2.scale(2f / this@GlScreen.getWidth().toFloat(), 2f / this@GlScreen.getHeight().toFloat()))
            }
        })
        //setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY)
        this.setFocusable(true);
        this.setFocusableInTouchMode(true);

    }
    private var glContent: GlComposed = GlComposed(composed(observableIterable(listOf())), this)
    override var content: Composed<*>
        get() = glContent.original
        set(value) {
            glContent = value as? GlComposed ?: GlComposed(value, this)
        }

    override val shape: Rectangle get() = rectangle(vector(this@GlScreen.getWidth(), this@GlScreen.getHeight()))

    val fontShader = GlShader(
            vertexShaderCode = """
                uniform mat4 u_Matrix;
                attribute vec4 a_Position;
                attribute vec2 a_TexCoord;
                varying vec2 v_TexCoord;
                varying vec2 v_TexCoord2;
                void main() {
                    v_TexCoord = a_TexCoord;
                    v_TexCoord2 = 64.0*a_TexCoord;
                    gl_Position = u_Matrix * a_Position;
                }
            """,
            fragmentShaderCode = """
                #extension GL_OES_standard_derivatives : require
                precision mediump float;
                uniform vec4 u_Color;
                varying vec2 v_TexCoord;
                varying highp vec2 v_TexCoord2;
                uniform sampler2D u_Texture;
                void main() {
                    float distance = texture2D(u_Texture, v_TexCoord).a;
                    float smoothing = length(vec2(dFdx(v_TexCoord2.x), dFdy(v_TexCoord2.x))) +
                                      length(vec2(dFdx(v_TexCoord2.y), dFdy(v_TexCoord2.y)));
                    float alpha = smoothstep(0.5 - smoothing, 0.5 + smoothing, distance);
                    gl_FragColor = vec4(u_Color.rgb, alpha);
                }
            """)


    val flatShader = GlShader(
            vertexShaderCode = """
                uniform mat4 u_Matrix;
                attribute vec4 a_Position;
                void main() {
                    gl_Position = u_Matrix * a_Position;
                }
            """,
            fragmentShaderCode = """
                precision mediump float;
                uniform vec4 u_Color;
                void main() {
                    gl_FragColor = u_Color;
                }
            """)

     override fun onTouchEvent(event : MotionEvent) : Boolean {
        val location = Transforms2.scale(1, -1)(vector(event.getX(), event.getY()) - shape.halfSize)

        when(event.getAction()) {
            MotionEvent.ACTION_DOWN -> { pointer.move(location) ; touch.press() }
            MotionEvent.ACTION_UP -> { pointer.move(location) ; touch.release() }
            MotionEvent.ACTION_MOVE -> pointer.move(location)
        }

        return true
    }

    class AndroidKey(val command: Command) : Key {
        override val definition: KeyDefinition = keyDefinition(command)
        override var isPressed: Boolean = false
        override val pressed = trigger<Key>()
        override val released = trigger<Key>()

        fun press() {
            isPressed = true
            pressed(this)
        }

        fun release() {
            isPressed = false
            released(this)
        }
    }

    class TouchPointer : Pointer {
        override val moved = trigger<Pointer>()
        override var location: Vector2 = zeroVector2

        fun move(location : Vector2) {
            this.location = location
            moved(this)
        }
    }

    val touch = AndroidKey(Commands.Touch.touch)
    val pointer = TouchPointer()
    val pointerKeys : PointerKeys = pointerKeys(pointer, listOf(touch))

    val keyboard = observableList<Key>()

    override fun onKeyDown(keyCode: Int, event : KeyEvent) : Boolean {
        val key = AndroidKey(Commands.Keyboard.character(event.getKeyCharacterMap()!!.getDisplayLabel(keyCode)))
        keyboard.add(key)
        key.press()
        key.release()
        keyboard.remove(key)
        return true
    }
}



