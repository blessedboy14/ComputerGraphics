package com.example.akg_java.math;

import java.util.Arrays;

public class ZBuffer {

    private final double[][] bf;
    private final int width;

    public ZBuffer(int width, int height) {
        this.width = width;
        bf = new double[width][height];
        for (int i = 0; i < width; i++) {
            Arrays.fill(bf[i], Float.POSITIVE_INFINITY);
        }
    }

    public void drop() {
        for (int i = 0; i < width; i++) {
            Arrays.fill(bf[i], Float.POSITIVE_INFINITY);
        }
    }

    public void edit(int x, int y, double val) {
        this.bf[x][y] = val;
    }

    public double get(int x, int y) {
        return this.bf[x][y];
    }

    public double[][] getBf() {
        return this.bf;
    }
}
