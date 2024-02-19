package com.example.akg_java.math;

public class Texture {
    public double u;
    public double v;
    public double w;

    public Texture(double u, double v, double w) {
        this.u = u;
        this.v = v;
        this.w = w;
    }

    public Texture(double u, double v) {
        this.u = u;
        this.v = v;
        this.w = 0;
    }

    public Texture(double u) {
        this.u = u;
        this.v = 0;
        this.w = 0;
    }

}
