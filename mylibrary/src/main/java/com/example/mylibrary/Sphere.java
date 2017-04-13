package com.example.mylibrary;

import java.util.ArrayList;
import java.util.List;

public class Sphere {

    public static final short LATITUDE_BANDS = 60;
    public static final short LONGITUDE_BANDS = 60;
    public static final int RADIUS = 2;

    public static final int NORMAL_DATA_SIZE = 3;
    public static final int TEXTURE_COORD_DATA_SIZE = 2;
    public static final int VERTEX_POSITION_DATA_SIZE = 3;
    public static final int INDEX_DATA_SIZE = 1;

    public final List<Float> normalData;
    public final List<Float> textureCoordData;
    public final List<Float> vertexPositionData;
    public final List<Short> indexData;

    private static Sphere instance = null;

    public static Sphere instance() {
        if (instance == null) {
            instance = new Sphere();
        }
        return instance;
    }

    private Sphere() {
        normalData = new ArrayList<>();
        textureCoordData = new ArrayList<>();
        vertexPositionData = new ArrayList<>();
        indexData = new ArrayList<>();

        for (int latNum = 0; latNum <= LATITUDE_BANDS; latNum++) {
            double theta = (double) latNum * Math.PI / LATITUDE_BANDS;
            double sinTheta = Math.sin(theta);
            double cosTheta = Math.cos(theta);

            for (int longNum = 0; longNum <= LONGITUDE_BANDS; longNum++) {
                double phi = (double) longNum * 2 * Math.PI / LONGITUDE_BANDS;
                double sinPhi = Math.sin(phi);
                double cosPhi = Math.cos(phi);

                double x = cosPhi * sinTheta;
                double y = cosTheta;
                double z = sinPhi * sinTheta;
                double u = 1 - ((double)longNum / LONGITUDE_BANDS);
                double v = 1 - ((double)latNum / LATITUDE_BANDS);

                normalData.add((float) x);
                normalData.add((float) y);
                normalData.add((float) z);

                textureCoordData.add((float) u);
                textureCoordData.add((float) v);

                vertexPositionData.add((float) (RADIUS * x));
                vertexPositionData.add((float) (RADIUS * y));
                vertexPositionData.add((float) (RADIUS * z));
            }
        }

        for (short latNum = 0; latNum < LATITUDE_BANDS; latNum++) {
            for (short longNum = 0; longNum < LONGITUDE_BANDS; longNum++) {
                short first = (short) ((latNum * (LONGITUDE_BANDS + 1)) + longNum);
                short second = (short) (first + LONGITUDE_BANDS + 1);

                indexData.add(first);
                indexData.add(second);
                indexData.add((short) (first + 1));

                indexData.add(second);
                indexData.add((short) (second + 1));
                indexData.add((short) (first + 1));
            }
        }
    }
}
