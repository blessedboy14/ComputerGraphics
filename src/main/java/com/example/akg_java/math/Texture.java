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

    public Texture divide(double val) {
        return new Texture(this.u / val, this.v / val, 1.0f / val);
    }

    public Texture grade(double val) { return new Texture(this.u * val, this.v * val, this.w * val); }

    public Texture add(Texture o) { return new Texture(this.u + o.u, this.v + o.v, this.w + o.w); }

    public Vec3d toVec() {
        return new Vec3d(this.u, this.v, this.w);
    }

}
