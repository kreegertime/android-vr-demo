package com.example.vrtest;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.Bundle;

import com.example.mylibrary.Pitch;
import com.google.vr.sdk.base.AndroidCompat;
import com.google.vr.sdk.base.Eye;
import com.google.vr.sdk.base.GvrActivity;
import com.google.vr.sdk.base.GvrView;
import com.google.vr.sdk.base.HeadTransform;
import com.google.vr.sdk.base.Viewport;

import javax.microedition.khronos.egl.EGLConfig;

public class VrActivity extends GvrActivity implements GvrView.StereoRenderer {

    private static final float Z_NEAR = 0.1f;
    private static final float Z_FAR = 200.0f;

    private final float[] mCamera = new float[16];
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];

    private static final int PITCH_COUNT = 10;
    private Pitch pitch[] = new Pitch[PITCH_COUNT];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_vr);

        GvrView gvrView = (GvrView) findViewById(R.id.gvr_view);
        gvrView.setEGLConfigChooser(8, 8, 8, 8, 16, 8);

        gvrView.setRenderer(this);
        gvrView.setTransitionViewEnabled(true);

        // Enable Cardboard-trigger feedback with Daydream headsets. This is a simple way of supporting
        // Daydream controller input for basic interactions using the existing Cardboard trigger API.
        gvrView.enableCardboardTriggerEmulation();

        if (gvrView.setAsyncReprojectionEnabled(true)) {
            // Async reprojection decouples the app framerate from the display framerate,
            // allowing immersive interaction even at the throttled clockrates set by
            // sustained performance mode.
            AndroidCompat.setSustainedPerformanceMode(this, true);
        }

        setGvrView(gvrView);
    }

    @Override
    public void onNewFrame(HeadTransform headTransform) {
        // Build the camera matrix and apply it to the ModelView.
        // Position the eye behind the origin.
        final float eyeX = 0.0f;
        final float eyeY = 0.0f;
        final float eyeZ = 0.01f;

        // We are looking toward the distance
        final float lookX = 0.0f;
        final float lookY = 0.0f;
        final float lookZ = -5.0f;

        // Set our up vector. This is where our head would be pointing were we holding the camera.
        final float upX = 0.0f;
        final float upY = 1.0f;
        final float upZ = 0.0f;

        // Set the view matrix. This matrix can be said to represent the camera position.
        // NOTE: In OpenGL 1, a ModelView matrix is used, which is a combination of a model and
        // view matrix. In OpenGL 2, we can keep track of these matrices separately if we choose.
        Matrix.setLookAtM(mCamera, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);
    }

    @Override
    public void onDrawEye(Eye eye) {
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        Matrix.multiplyMM(mViewMatrix, 0, eye.getEyeView(), 0, mCamera, 0);

        float perspective[] = eye.getPerspective(Z_NEAR, Z_FAR);
        Matrix.multiplyMM(mMVPMatrix, 0, perspective, 0, mViewMatrix, 0);

        for (int i = 0; i < PITCH_COUNT; i++) {
            pitch[i].draw(mMVPMatrix);
        }

//        // Apply the eye transformation to the camera.
//        Matrix.multiplyMM(view, 0, eye.getEyeView(), 0, camera, 0);
//
//        // Set the position of the light
//        Matrix.multiplyMV(lightPosInEyeSpace, 0, view, 0, LIGHT_POS_IN_WORLD_SPACE, 0);
//
//        // Build the ModelView and ModelViewProjection matrices
//        // for calculating cube position and light.
//        float[] perspective = eye.getPerspective(Z_NEAR, Z_FAR);
//        Matrix.multiplyMM(modelView, 0, view, 0, modelCube, 0);
//        Matrix.multiplyMM(modelViewProjection, 0, perspective, 0, modelView, 0);
//        drawCube();
    }

    @Override
    public void onFinishFrame(Viewport viewport) {}

    @Override
    public void onSurfaceChanged(int i, int i1) {}

    @Override
    public void onSurfaceCreated(EGLConfig eglConfig) {
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);

        for (int i = 0; i < PITCH_COUNT; i++) {
            pitch[i] = new Pitch(getApplicationContext());
            pitch[i].setBallCoords(
                    (float) (Math.random() * 40) - 10,
                    (float) (Math.random() * i * i) + 10,
                    -200.0f);
        }
    }

    @Override
    public void onRendererShutdown() {}
}
