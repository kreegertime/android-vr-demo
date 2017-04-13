package com.example.mylibrary;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.List;

/**
 * Created by kreeger on 4/13/17.
 */

public class GLObject {

    protected static final int BYTES_PER_FLOAT = 4;
    protected static final int BYTES_PER_SHORT = 2;

    // TODO - would be nice to have this globally.
    protected static int loadShader(int type, String shaderCode){
        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    protected  static FloatBuffer createFloatBuffer(final float[] floats) {
        // (number of coordinate values * 4 bytes per float)
        ByteBuffer bb = ByteBuffer.allocateDirect(floats.length * BYTES_PER_FLOAT);
        bb.order(ByteOrder.nativeOrder());

        FloatBuffer floatBuffer = bb.asFloatBuffer();
        for (int i = 0; i < floats.length; i++) {
            floatBuffer.put(floats[i]);
        }
        floatBuffer.position(0);
        return floatBuffer;
    }

    protected static FloatBuffer createFloatBuffer(List<Float> floatList) {
        // (number of coordinate values * 4 bytes per float)
        ByteBuffer bb = ByteBuffer.allocateDirect(floatList.size() * BYTES_PER_FLOAT);
        bb.order(ByteOrder.nativeOrder());

        FloatBuffer floatBuffer = bb.asFloatBuffer();
        for (Float f : floatList) {
            floatBuffer.put(f);
        }
        floatBuffer.position(0);
        return floatBuffer;
    }

    protected static ShortBuffer createShortBuffer(List<Short> shortList) {
        // (number of coordinate values * 4 bytes per float)
        ByteBuffer bb = ByteBuffer.allocateDirect(shortList.size() * BYTES_PER_SHORT);
        bb.order(ByteOrder.nativeOrder());

        ShortBuffer shortBuffer = bb.asShortBuffer();
        for (Short s : shortList) {
            shortBuffer.put(s);
        }
        shortBuffer.position(0);
        return shortBuffer;
    }

    protected static int loadTexture(final Context context, final int resourceId) {
        final int[] textureHandle = new int[1];
        GLES20.glGenTextures(1, textureHandle, 0);

        final BitmapFactory.Options options = new BitmapFactory.Options();
        //options.inScaled = false;   // No pre-scaling

        final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_NEAREST);
        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

        return textureHandle[0];
    }

    protected static void vec3Normalize(float[] out, float[] a) {
        float x = a[0];
        float y = a[1];
        float z = a[2];

        float len = x * x + y * y + z * z;
        if (len > 0) {
            double len2 = 1 / Math.sqrt((double) len);
            out[0] = a[0] * (float) len2;
            out[1] = a[1] * (float) len2;
            out[2] = a[2] * (float) len2;
        }
    }

    protected static void vec3Scale(float[] out, float[] a, float b) {
        out[0] = a[0] * b;
        out[1] = a[1] * b;
        out[2] = a[2] * b;
    }

    protected static void mat4FromMat4(float[] out, float[] a) {
        out[0] = a[0];
        out[1] = a[1];
        out[2] = a[2];
        out[3] = a[4];
        out[4] = a[5];
        out[5] = a[6];
        out[6] = a[8];
        out[7] = a[9];
        out[8] = a[10];
    }

    protected static void mat3Invert(float[] out, float[] a) {
        float a00 = a[0], a01 = a[1], a02 = a[2],
                a10 = a[3], a11 = a[4], a12 = a[5],
                a20 = a[6], a21 = a[7], a22 = a[8],

                b01 = a22 * a11 - a12 * a21,
                b11 = -a22 * a10 + a12 * a20,
                b21 = a21 * a10 - a11 * a20,

                // Calculate the determinant
                det = a00 * b01 + a01 * b11 + a02 * b21;

        if (det == 0.0f) {
            return;
        }
        det = (float) 1.0 / det;

        out[0] = b01 * det;
        out[1] = (-a22 * a01 + a02 * a21) * det;
        out[2] = (a12 * a01 - a02 * a11) * det;
        out[3] = b11 * det;
        out[4] = (a22 * a00 - a02 * a20) * det;
        out[5] = (-a12 * a00 + a02 * a10) * det;
        out[6] = b21 * det;
        out[7] = (-a21 * a00 + a01 * a20) * det;
        out[8] = (a11 * a00 - a01 * a10) * det;
    }

    protected static void mat3Transpose(float[] out, float[] a) {
        float a01 = a[1], a02 = a[2], a12 = a[5];
        out[1] = a[3];
        out[2] = a[6];
        out[3] = a01;
        out[5] = a[7];
        out[6] = a02;
        out[7] = a12;
    }
}
