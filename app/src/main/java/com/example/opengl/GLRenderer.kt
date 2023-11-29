package com.example.opengl


import android.app.Activity
import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.GLUtils
import androidx.core.content.ContentProviderCompat.requireContext
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


class GLRenderer(private val context: Context) : GLSurfaceView.Renderer {

    private var textureId: Int = 0

    val vertexShaderCode = """
    attribute vec4 vPosition;
    attribute vec2 aTexCoordinate;
    varying vec2 vTexCoordinate;
    void main() {
        vTexCoordinate = aTexCoordinate;
        gl_Position = vPosition;
    }
"""
    val fragmentShaderCode = """
    precision mediump float;
    varying vec2 vTexCoordinate;
    uniform sampler2D uTexture;
    void main() {
        gl_FragColor = texture2D(uTexture, vTexCoordinate);
    }
"""

    private val COORDS_PER_VERTEX = 2

    private val vertexStride = COORDS_PER_VERTEX * 4

    private val squareCoords = floatArrayOf(
        -0.5f,  0.5f, 0.0f,   // top left
        -0.5f, -0.5f, 0.0f,   // bottom left
        0.5f, -0.5f, 0.0f,   // bottom right
        0.5f,  0.5f, 0.0f    // top right
    )

    private val vertexBuffer: FloatBuffer = ByteBuffer.allocateDirect(squareCoords.size * 4).run {
        order(ByteOrder.nativeOrder())
        asFloatBuffer().apply {
            put(squareCoords)
            position(0)
        }
    }

    private val drawOrder = shortArrayOf(
        0, 1, 2, 0, 2, 3  // order to draw vertices
    )

    private val drawListBuffer: ShortBuffer = ByteBuffer.allocateDirect(drawOrder.size * 2).run {
        order(ByteOrder.nativeOrder())
        asShortBuffer().apply {
            put(drawOrder)
            position(0)
        }
    }

    private val textureCoords = floatArrayOf(
        0.0f, 1.0f,  // top left
        0.0f, 0.0f,  // bottom left
        1.0f, 0.0f,  // bottom right
        1.0f, 1.0f   // top right
    )

    private val textureBuffer: FloatBuffer = ByteBuffer.allocateDirect(textureCoords.size * 4).run {
        order(ByteOrder.nativeOrder())
        asFloatBuffer().apply {
            put(textureCoords)
            position(0)
        }
    }

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)

         textureId = loadTexture(context, R.drawable.erste_etage)
    }



    override fun onDrawFrame(unused: GL10) {
        // Redraw background color
        val program = createShaderProgram()
        GLES20.glUseProgram(program)

        val positionHandle = GLES20.glGetAttribLocation(program, "vPosition")
        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer)

        val texCoordHandle = GLES20.glGetAttribLocation(program, "aTexCoordinate")
        GLES20.glEnableVertexAttribArray(texCoordHandle)
        GLES20.glVertexAttribPointer(texCoordHandle, 2, GLES20.GL_FLOAT, false, 0, textureBuffer)

        val textureHandle = GLES20.glGetUniformLocation(program, "uTexture")
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
        GLES20.glUniform1i(textureHandle, 0)

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.size, GLES20.GL_UNSIGNED_SHORT, drawListBuffer)

        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(texCoordHandle)
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
    }

    private fun createShaderProgram(): Int {
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        return GLES20.glCreateProgram().also { program ->
            GLES20.glAttachShader(program, vertexShader)
            GLES20.glAttachShader(program, fragmentShader)
            GLES20.glLinkProgram(program)
        }
    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        return GLES20.glCreateShader(type).also { shader ->
            GLES20.glShaderSource(shader, shaderCode)
            GLES20.glCompileShader(shader)
        }
    }

    private fun loadTexture(context: Context, resourceId: Int): Int {
        val textureObjectIds = IntArray(1)
        GLES20.glGenTextures(1, textureObjectIds, 0)

        val options = BitmapFactory.Options()
        options.inScaled = false

        val bitmap = BitmapFactory.decodeResource(context.resources, resourceId, options)

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureObjectIds[0])

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)

        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
        bitmap.recycle()

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)

        return textureObjectIds[0]
    }
}