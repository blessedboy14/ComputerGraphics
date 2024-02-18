package com.example.akg_java.math;

public class Triangle {
    private Vec3d[] points = new Vec3d[3];
    private Vec3d[] normals = new Vec3d[3];
    public Triangle(Vec3d v1, Vec3d v2, Vec3d v3) {
        points[0] = v1;
        points[1] = v2;
        points[2] = v3;
    }

    public Triangle(Vec3d v1, Vec3d v2, Vec3d v3, Vec3d i, Vec3d j, Vec3d k) {
        points[0] = v1;
        points[1] = v2;
        points[2] = v3;
        normals[0] = i;
        normals[1] = j;
        normals[2] = k;
    }

    public Vec3d[] getNormals() {
        return normals;
    }

    public Vec3d[] getPoints() {
        return points;
    }

    public Triangle multiplyMatrix(Matr4x4 matrix) {
        return new Triangle(this.points[0].multiply(matrix), this.points[1].multiply(matrix), this.points[2].multiply(matrix),
                this.normals[0], this.normals[1], this.normals[2]);
    }

    public Triangle multiplyCamera(Matr4x4 matr4x4) {
        return new Triangle(this.points[0], this.points[1], this.points[2],
                this.normals[0].multiply(matr4x4).toNormal(),
                this.normals[1].multiply(matr4x4).toNormal(),
                this.normals[2].multiply(matr4x4).toNormal());
    }

    public void setPoints(Vec3d[] points) {
        this.points = points;
    }

    public Vec3d getNormal() {
        return this.points[2].subtract(this.points[0]).Cross(this.points[1].subtract(this.points[0])).toNormal();
    }
}
