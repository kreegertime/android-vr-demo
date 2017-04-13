package com.example.mylibrary;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.SystemClock;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Pitch extends GLObject {

    private static final String VERTEX_SHADER_SRC =
        "attribute vec3 aVertexPosition;" +
        "attribute vec3 aVertexNormal;" +
        "attribute vec2 aTextureCoord;" +

        "uniform mat4 uMVMatrix;" +
        "uniform mat4 uPMatrix;" +
        "uniform mat3 uNMatrix;" +

        "uniform vec3 uAmbientColor;" +

        "uniform vec3 uLightingDirection;" +
        "uniform vec3 uDirectionalColor;" +

        "varying vec2 vTextureCoord;" +
        "varying vec3 vLightWeighting;" +

        "void main(void) {" +
            "gl_Position = uPMatrix * uMVMatrix * vec4(aVertexPosition, 1.0);" +
            "vTextureCoord = aTextureCoord;" +
            "vec3 transformedNormal = uNMatrix * aVertexNormal;" +
            "float directionalLightWeighting = max(dot(transformedNormal, uLightingDirection), 0.0);" +
            "vLightWeighting = uAmbientColor + uDirectionalColor * directionalLightWeighting;" +
        "}";

    private static final String FRAGMENT_SHADER_SRC =
        "precision mediump float;" +
        "varying vec2 vTextureCoord;" +
        "varying vec3 vLightWeighting;" +

        "uniform sampler2D uSampler;" +

        "void main(void) {" +
            "vec4 textureColor = texture2D(uSampler, vec2(vTextureCoord.s, vTextureCoord.t));" +
            "gl_FragColor = vec4(textureColor.rgb * vLightWeighting, textureColor.a);" +
        "}";

    private float[] mvMatrix;
    private float[] ballRotationMatrix;

    private int shaderProgram;

    private int vertexPositionAttribute;
    private int textureCoordAttribute;
    private int vertexNormalAttribute;

    private int pMatrixUniform;
    private int mvMatrixUniform;
    private int nMatrixUniform;
    private int samplerUniform;
    private int ambientColorUniform;
    private int lightingDirectionUniform;
    private int directionalColorUniform;

    private int ballVertexNormalBuffer;
    private int ballVertexTextureCoordBuffer;
    private int ballVertexPositionBuffer;
    private int ballVertexIndexBuffer;

    private int ballTexture;

    private float origBallX;
    private float origBallY;
    private float origBallZ;

    private float ballX;
    private float ballY;
    private float ballZ;

    private float ballDegRotate = 25f;
    private float ballSpeed = 0.5f;

    public Pitch(Context context) {
        mvMatrix = new float[16];
        ballRotationMatrix = new float[16];
        Matrix.setIdentityM(ballRotationMatrix, 0);

        origBallX = 0f;
        origBallY = 20f;
        origBallZ = -300f;
        ballX = origBallX;
        ballY = origBallY;
        ballZ = origBallZ;

        ballDegRotate = 25f;
        ballSpeed = 5.0f;

        initShaders();
        initBuffers();
        ballTexture = loadTexture(context, R.mipmap.baseball2);
    }

    public void setBallCoords(float x, float y, float z) {
      ballX = x;
      ballY = y;
      ballZ = z;

      origBallX = x;
      origBallY = y;
      origBallZ = z;
    }

    public void setBallSpeed(float ballSpeed) {
      this.ballSpeed = ballSpeed;
    }

    public void draw(float[] mvpMatrix) {
        animate();

        GLES20.glUseProgram(shaderProgram);

        GLES20.glUniform3f(ambientColorUniform, 0.2f, 0.2f, 0.2f);

        float lightingDirection[] = new float[] { -1.0f, -1.0f, -1.0f };
        float adjustedLD[] = new float[] { 0.0f, 0.0f, 0.0f };
        vec3Normalize(adjustedLD, lightingDirection);
        vec3Scale(adjustedLD, adjustedLD, -1);

        GLES20.glUniform3fv(lightingDirectionUniform, 1, adjustedLD, 0);
        GLES20.glUniform3f(directionalColorUniform, 0.8f, 0.8f, 0.8f);

        Matrix.setIdentityM(mvMatrix, 0);
        Matrix.translateM(mvMatrix, 0, ballX, ballY, ballZ);

        Matrix.multiplyMM(mvMatrix, 0, mvMatrix, 0, ballRotationMatrix, 0);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, ballTexture);
        GLES20.glUniform1i(samplerUniform, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, ballVertexPositionBuffer);
        GLES20.glVertexAttribPointer(vertexPositionAttribute, Sphere.VERTEX_POSITION_DATA_SIZE, GLES20.GL_FLOAT, false, 0, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, ballVertexTextureCoordBuffer);
        GLES20.glVertexAttribPointer(textureCoordAttribute, Sphere.TEXTURE_COORD_DATA_SIZE, GLES20.GL_FLOAT, false, 0, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, ballVertexNormalBuffer);
        GLES20.glVertexAttribPointer(vertexNormalAttribute, Sphere.NORMAL_DATA_SIZE, GLES20.GL_FLOAT, false, 0, 0);

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ballVertexIndexBuffer);

        GLES20.glUniformMatrix4fv(pMatrixUniform, 1, false, mvpMatrix, 0);
        GLES20.glUniformMatrix4fv(mvMatrixUniform, 1, false, mvMatrix, 0);

        float mvMatrix3[] = new float[9];
        mat4FromMat4(mvMatrix3, mvMatrix);

        float normalMatrix[] = new float[9];
        mat3Invert(normalMatrix, mvMatrix3);
        mat3Transpose(normalMatrix, normalMatrix);

        GLES20.glUniformMatrix3fv(nMatrixUniform, 1, false, normalMatrix, 0);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, Sphere.instance().indexData.size(), GLES20.GL_UNSIGNED_SHORT, 0);
    }

    private void animate() {
        float radians = (float) Math.toRadians(ballDegRotate);
        long time = SystemClock.uptimeMillis() % 4000L;
        float angle = 1.90f * ((int) time);
        Matrix.setRotateM(ballRotationMatrix, 0, angle, 1, 2, 1);
        //Logger.getLogger("Rotation").log(Level.INFO, "Rotation: " + ballRotationMatrix);
      //let axis = vec3.fromValues(1, 2, 1);
      //let radians = WebGL.degToRad(this.ballDegRot);
      //mat4.rotate(this.ballRotationMatrix, this.ballRotationMatrix, radians, axis);

      ballX += 0.1;
      ballY -= 0.3;
      ballZ += ballSpeed;

      if (ballZ > 200) {
        ballX = origBallX;
        ballY = origBallY;
        ballZ = origBallZ;
      }
    }

    private void initShaders() {
        shaderProgram = GLES20.glCreateProgram();

        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER_SRC);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER_SRC);
        GLES20.glAttachShader(shaderProgram, vertexShader);
        GLES20.glAttachShader(shaderProgram, fragmentShader);
        GLES20.glLinkProgram(shaderProgram);

        vertexPositionAttribute = GLES20.glGetAttribLocation(shaderProgram, "aVertexPosition");
        GLES20.glEnableVertexAttribArray(vertexPositionAttribute);

        textureCoordAttribute = GLES20.glGetAttribLocation(shaderProgram, "aTextureCoord");
        GLES20.glEnableVertexAttribArray(textureCoordAttribute);

        vertexNormalAttribute = GLES20.glGetAttribLocation(shaderProgram, "aVertexNormal");
        GLES20.glEnableVertexAttribArray(vertexNormalAttribute);

        pMatrixUniform = GLES20.glGetUniformLocation(shaderProgram, "uPMatrix");
        mvMatrixUniform = GLES20.glGetUniformLocation(shaderProgram, "uMVMatrix");
        nMatrixUniform = GLES20.glGetUniformLocation(shaderProgram, "uNMatrix");
        samplerUniform = GLES20.glGetUniformLocation(shaderProgram, "uSampler");
        ambientColorUniform = GLES20.glGetUniformLocation(shaderProgram, "uAmbientColor");
        lightingDirectionUniform = GLES20.glGetUniformLocation(shaderProgram, "uLightingDirection");
        directionalColorUniform = GLES20.glGetUniformLocation(shaderProgram, "uDirectionalColor");
    }

    private void initBuffers() {
        Sphere sphere = Sphere.instance();

        final int buffers[] = new int[4];
        GLES20.glGenBuffers(4, buffers, 0);

        FloatBuffer floatBuffer;

        floatBuffer = createFloatBuffer(sphere.normalData);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
        GLES20.glBufferData(
                GLES20.GL_ARRAY_BUFFER,
                floatBuffer.capacity() * BYTES_PER_FLOAT,
                floatBuffer,
                GLES20.GL_STATIC_DRAW);
        ballVertexNormalBuffer = buffers[0];

        floatBuffer = createFloatBuffer(sphere.textureCoordData);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[1]);
        GLES20.glBufferData(
                GLES20.GL_ARRAY_BUFFER,
                floatBuffer.capacity() * BYTES_PER_FLOAT,
                floatBuffer,
                GLES20.GL_STATIC_DRAW);
        ballVertexTextureCoordBuffer = buffers[1];

        floatBuffer = createFloatBuffer(sphere.vertexPositionData);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[2]);
        GLES20.glBufferData(
                GLES20.GL_ARRAY_BUFFER,
                floatBuffer.capacity() * BYTES_PER_FLOAT,
                floatBuffer,
                GLES20.GL_STATIC_DRAW);
        ballVertexPositionBuffer = buffers[2];

        ShortBuffer shortBuffer = createShortBuffer(sphere.indexData);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, buffers[3]);
        GLES20.glBufferData(
                GLES20.GL_ELEMENT_ARRAY_BUFFER,
                shortBuffer.capacity() * BYTES_PER_SHORT,
                shortBuffer,
                GLES20.GL_STATIC_DRAW);
        ballVertexIndexBuffer = buffers[3];

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
    }
}
