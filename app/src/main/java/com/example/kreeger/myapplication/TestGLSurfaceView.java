package com.example.kreeger.myapplication;

import android.content.Context;
import android.opengl.GLSurfaceView;

public class TestGLSurfaceView extends GLSurfaceView {

    private final AtBatRenderer mRenderer;

    public TestGLSurfaceView(Context context) {
        super(context);

        // Use OpenGL ES 2.0
        setEGLContextClientVersion(2);

        mRenderer = new AtBatRenderer(context);
        setRenderer(mRenderer);
    }
}
