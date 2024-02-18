package com.example.akg_java.EngineUtility;

import java.util.Arrays;

public class ZBuffer {
    private double[][] bf;
    private final int width;
    public ZBuffer(int width, int height) {
        this.width = width;
        bf = new double[width+1][height+1];
        for (int i = 0; i < width+1; i++) {
            Arrays.fill(bf[i], Float.POSITIVE_INFINITY);
        }
    }

    public void drop() {
        for (int i = 0; i < width+1; i++) {
            Arrays.fill(bf[i], Float.POSITIVE_INFINITY);
        }
    }

    public void edit(int x, int y, double val) {
        this.bf[x][y] = val;
    }

    public double get(int x, int y) {
        return this.bf[x][y];
    }

}
