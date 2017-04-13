package com.example.mylibrary;


import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.FloatBuffer;
import java.util.List;

public class Field extends GLObject {

    private static final String FIELD_VERTEX_SHADER_SRC =
        "attribute vec3 aVertexPosition;" +
        "attribute vec4 aVertexColor;" +

        "uniform mat4 uMVMatrix;" +
        "uniform mat4 uPMatrix;" +

        "varying vec4 vColor;" +

        "void main(void) {" +
            "gl_Position = uPMatrix * uMVMatrix * vec4(aVertexPosition * 100.0, 1.0);" +
            "vColor = aVertexColor;" +
        "}";

    private static final String FIELD_FRAGMENT_SHADER_SRC =
        "precision mediump float;" +
        "varying vec4 vColor;" +

        "void main(void) {" +
            "gl_FragColor = vColor;" +
        "}";

    public static final int FIELD_VERTICES_SIZE = 3;
    public static final int FIELD_VERTICES_ITEMS = 4;

    public static final int FIELD_COLORS_SIZE = 4;
    public static final int FIELD_COLORS_ITEMS = 4;

    private float[] mvMatrix;
    private int shaderProgram;
    private int vertexPositionAttribute;
    private int vertexColorAttribute;
    private int pMatrixUniform;
    private int mvMatrixUniform;
    private int fieldVertexPositionBuffer;
    private int fieldVertexColorBuffer;

    public Field() {
        mvMatrix = new float[16];
        Matrix.setIdentityM(mvMatrix, 0);
        Matrix.translateM(mvMatrix, 0, 0.0f, -10.0f, -100.0f);

        initShaders();
        initBuffers();
    }

    public void draw(float[] mvpMatrix) {
        GLES20.glUseProgram(shaderProgram);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, fieldVertexPositionBuffer);
        GLES20.glVertexAttribPointer(vertexPositionAttribute, FIELD_VERTICES_SIZE, GLES20.GL_FLOAT, false, 0, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, fieldVertexColorBuffer);
        GLES20.glVertexAttribPointer(vertexColorAttribute, FIELD_COLORS_SIZE, GLES20.GL_FLOAT, false, 0, 0);

        GLES20.glUniformMatrix4fv(pMatrixUniform, 1, false, mvpMatrix, 0);
        GLES20.glUniformMatrix4fv(mvMatrixUniform, 1, false, mvMatrix, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, FIELD_VERTICES_ITEMS);
    }

    private void initShaders() {
        shaderProgram = GLES20.glCreateProgram();

        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, FIELD_VERTEX_SHADER_SRC);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, FIELD_FRAGMENT_SHADER_SRC);
        GLES20.glAttachShader(shaderProgram, vertexShader);
        GLES20.glAttachShader(shaderProgram, fragmentShader);
        GLES20.glLinkProgram(shaderProgram);

        vertexPositionAttribute = GLES20.glGetAttribLocation(shaderProgram, "aVertexPosition");
        GLES20.glEnableVertexAttribArray(vertexPositionAttribute);

        vertexColorAttribute = GLES20.glGetAttribLocation(shaderProgram, "aVertexColor");
        GLES20.glEnableVertexAttribArray(vertexColorAttribute);

        mvMatrixUniform = GLES20.glGetUniformLocation(shaderProgram, "uMVMatrix");
        pMatrixUniform = GLES20.glGetUniformLocation(shaderProgram, "uPMatrix");
    }

    private void initBuffers() {
        final float[] VERTICES = new float[] {
                1.0f,  0.0f,  0.0f,
                0.0f,  0.0f,  1.0f,
                0.0f,  0.0f, -1.0f,
                -1.0f,  0.0f,  0.0f
        };

        final float[] COLORS = new float[] {
                1.0f, 0.0f, 0.0f, 1.0f,
                0.0f, 1.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 1.0f, 1.0f,
                0.0f, 0.0f, 0.0f, 1.0f
        };

        final int buffers[] = new int[2];
        GLES20.glGenBuffers(2, buffers, 0);

        FloatBuffer floatBuffer;

        floatBuffer = createFloatBuffer(VERTICES);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
        GLES20.glBufferData(
                GLES20.GL_ARRAY_BUFFER,
                floatBuffer.capacity() * BYTES_PER_FLOAT,
                floatBuffer,
                GLES20.GL_STATIC_DRAW);
        fieldVertexPositionBuffer = buffers[0];

        floatBuffer = createFloatBuffer(COLORS);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[1]);
        GLES20.glBufferData(
                GLES20.GL_ARRAY_BUFFER,
                floatBuffer.capacity() * BYTES_PER_FLOAT,
                floatBuffer,
                GLES20.GL_STATIC_DRAW);
        fieldVertexColorBuffer = buffers[1];

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
    }
}
