package net.pureal.android.backend

import android.opengl.GLES20

class GlShader(val vertexShaderCode: String, val fragmentShaderCode: String) {

    private var createdProgram : Int? = null

    fun useProgram() : Int {
        val program = createdProgram ?: createProgram()
        GLES20.glUseProgram(program)
        return program
    }

    private fun createProgram(): Int {
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)
        val program = GLES20.glCreateProgram()
        GLES20.glAttachShader(program, vertexShader)
        GLES20.glAttachShader(program, fragmentShader)
        GLES20.glLinkProgram(program)
        createdProgram = program
        return program
    }
    private fun loadShader(shaderType: Int, shaderCode: String): Int {
        val shader = GLES20.glCreateShader(shaderType)
        GLES20.glShaderSource(shader, shaderCode)
        GLES20.glCompileShader(shader)
        return shader;
    }
}