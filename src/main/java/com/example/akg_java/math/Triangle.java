package com.example.akg_java.math;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Triangle {

    private Vec3d[] points = new Vec3d[3];
    private Vec3d normal;

    public Triangle(Vec3d v1, Vec3d v2, Vec3d v3) {
        points[0] = v1;
        points[1] = v2;
        points[2] = v3;
    }

    public Vec3d[] getPoints() {
        return points;
    }

    public Triangle multiplyMatrix(Matr4x4 matrix) {
        return new Triangle(this.points[0].multiply(matrix), this.points[1].multiply(matrix), this.points[2].multiply(matrix));
    }
}
