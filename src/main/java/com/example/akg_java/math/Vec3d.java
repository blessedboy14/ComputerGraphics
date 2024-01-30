package com.example.akg_java.math;

public class Vec3d {
    public double x;
    public double y;
    public double z;
    public double w = 1;
    public Vec3d(double x, double y, double z, double w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }
    public Vec3d(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3d multiply(Matr4x4 matr) {
        double x = this.x * matr.matrix[0][0] + this.y * matr.matrix[1][0] + this.z * matr.matrix[2][0] + this.w * matr.matrix[3][0];
        double y = this.x * matr.matrix[0][1] + this.y * matr.matrix[1][1] + this.z * matr.matrix[2][1] + this.w * matr.matrix[3][1];
        double z = this.x * matr.matrix[0][2] + this.y * matr.matrix[1][2] + this.z * matr.matrix[2][2] + this.w * matr.matrix[3][2];
        double w = this.x * matr.matrix[0][3] + this.y * matr.matrix[1][3] + this.z * matr.matrix[2][3] + this.w * matr.matrix[3][3];
        if (w != 0) {
            x /= w;
            y /= w;
            z /= w;
        }
        return new Vec3d(x, y, z);
    }

    public double sqr() {
        return x*x + y*y + z*z;
    }

    public double Dot(Vec3d other) {
        return x*other.x + y*other.y + z* other.z;
    }

    public Vec3d Cross(Vec3d other) {
        return new Vec3d(this.y*other.z - other.y*this.z, -(this.x*other.z - other.x*this.z),
                this.x*other.y - other.x*this.y);
    }

    public static Vec3d vectorOfTwoPoints(Vec3d left, Vec3d right) {
        return new Vec3d(right.x- left.x, right.y - left.y, right.z - left.z);
    }


}
