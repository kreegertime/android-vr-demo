package com.example.kreeger.myapplication;


public class Pyramid {
    public static float[] VERTICES = {
            // Front face
            0.0f,  1.0f,  0.0f,
            -1.0f, -1.0f,  1.0f,
            1.0f, -1.0f,  1.0f,

            // Right face
            0.0f,  1.0f,  0.0f,
            1.0f, -1.0f,  1.0f,
            1.0f, -1.0f, -1.0f,

            // Back face
            0.0f,  1.0f,  0.0f,
            1.0f, -1.0f, -1.0f,
            -1.0f, -1.0f, -1.0f,

            // Left face
            0.0f,  1.0f,  0.0f,
            -1.0f, -1.0f, -1.0f,
            -1.0f, -1.0f,  1.0f
    };

    public static float[] COLORS = {
            // Front face
            1.0f, 0.0f, 0.0f, 1.0f,
            0.0f, 1.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f, 1.0f,

            // Right face
            1.0f, 0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f, 1.0f,
            0.0f, 1.0f, 0.0f, 1.0f,

            // Back face
            1.0f, 0.0f, 0.0f, 1.0f,
            0.0f, 1.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f, 1.0f,

            // Left face
            1.0f, 0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f, 1.0f,
            0.0f, 1.0f, 0.0f, 1.0f
    };

    public static int VERTICES_SIZE = 3;
    public static int VERTICES_ITEMS = 12;

    public static int COLOR_SIZE = 4;
    public static int COLOR_ITEMS = 12;
}
