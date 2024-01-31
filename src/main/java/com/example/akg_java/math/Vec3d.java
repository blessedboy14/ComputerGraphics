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

    public void setPoints(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static void swap(Vec3d A, Vec3d B) {
        Vec3d temp = new Vec3d(B.x, B.y, B.z);
        B.setPoints(A.x, A.y, A.z);
        A.setPoints(temp.x, temp.y, temp.z);
    }

    public Vec3d subtract(Vec3d other) {
        return new Vec3d(this.x - other.x, this.y - other.y, this.z - other.z);
    }

    public Vec3d add(Vec3d other) {
        return new Vec3d(this.x + other.x, this.y + other.y, this.z + other.z);
    }

    public Vec3d grade(double val) {
        return new Vec3d(this.x * val, this.y * val, this.z * val);
    }

    public void weight() {
        if (this.w != 0) {
            this.x /= w;
            this.y /= w;
            this.z /= w;
        }
    }

    public Vec3d multiply(Matr4x4 matr) {
        double x = this.x * matr.matrix[0][0] + this.y * matr.matrix[1][0] + this.z * matr.matrix[2][0] + this.w * matr.matrix[3][0];
        double y = this.x * matr.matrix[0][1] + this.y * matr.matrix[1][1] + this.z * matr.matrix[2][1] + this.w * matr.matrix[3][1];
        double z = this.x * matr.matrix[0][2] + this.y * matr.matrix[1][2] + this.z * matr.matrix[2][2] + this.w * matr.matrix[3][2];
        double w = this.x * matr.matrix[0][3] + this.y * matr.matrix[1][3] + this.z * matr.matrix[2][3] + this.w * matr.matrix[3][3];
        Vec3d new_vector = new Vec3d(x, y, z, w);
        new_vector.weight();
        return new_vector;
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