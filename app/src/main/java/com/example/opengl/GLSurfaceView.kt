package com.example.opengl

import android.content.Context
import android.opengl.GLSurfaceView


open class GLSurfaceView(context: Context) : GLSurfaceView(context) {

    private val renderer: GLRenderer

    init {
        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2)

        renderer = GLRenderer(context)

        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(renderer)
    }
}