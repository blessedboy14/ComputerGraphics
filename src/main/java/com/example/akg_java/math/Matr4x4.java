package com.example.akg_java.math;

import com.example.akg_java.EngineUtility.Camera;

import javax.vecmath.Quat4f;

public class Matr4x4 {
    public double[][] matrix;

    public Matr4x4(double[][] input) {
        this.matrix = input;
    }

    public Matr4x4 multiply(Matr4x4 other) {
        double[][] matrix = new double[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                matrix[i][j] = this.matrix[i][0] * other.matrix[0][j] + this.matrix[i][1] * other.matrix[1][j]
                        + this.matrix[i][2] * other.matrix[2][j] + this.matrix[i][3] * other.matrix[3][j];
            }
        }
        return new Matr4x4(matrix);
    }

    public static Matr4x4 identity() {
        return new Matr4x4(new double[][]{
                {1, 0, 0, 0},
                {0, 1, 0, 0},
                {0, 0, 1, 0},
                {0, 0, 0, 1},
        });
    }

    public static Matr4x4 translation(double x, double y, double z) {
        return new Matr4x4(new double[][]{
                {1, 0, 0, 0},
                {0, 1, 0, 0},
                {0, 0, 1, 0},
                {x, y, z, 1},
        });
    }

    public static Matr4x4 rotationX(double angle) {
        double rad = angle / 180 * Math.PI;
        return new Matr4x4(new double[][]{
                {1, 0, 0, 0},
                {0, Math.cos(rad), Math.sin(rad), 0},
                {0, -Math.sin(rad), Math.cos(rad), 0},
                {0, 0, 0, 1}
        });
    }

    public static Matr4x4 rotationY(double angle) {
        double rad = angle / 180 * Math.PI;
        return new Matr4x4(new double[][]{
                {Math.cos(rad), 0, -Math.sin(rad), 0},
                {0, 1, 0, 0},
                {Math.sin(rad), 0, Math.cos(rad), 0},
                {0, 0, 0, 1}
        });
    }

    public static Matr4x4 rotationZ(double angle) {
        double rad = angle / 180 * Math.PI;
        return new Matr4x4(new double[][]{
                {Math.cos(rad), Math.sin(rad), 0, 0},
                {-Math.sin(rad), Math.cos(rad), 0, 0},
                {0, 0, 1, 0},
                {0, 0, 0, 1}
        });
    }

    public static Matr4x4 scaleMatrix(double x, double y, double z) {
        return new Matr4x4(new double[][]{
                {x, 0, 0, 0},
                {0, y, 0, 0},
                {0, 0, z, 0},
                {0, 0, 0, 1},
        });
    }

    public static Matr4x4 projection(double fovDeg, double aspectRatio, double zNear, double zFar) {
        double fovRad = 1.0 / Math.tan(fovDeg * 0.5 / 180 * Math.PI);
        return new Matr4x4(new double[][]{
                {aspectRatio * fovRad, 0, 0, 0},
                {0, fovRad, 0, 0},
                {0, 0, zFar / (zFar - zNear), 1},
                {0, 0, (-zFar * zNear) / (zFar - zNear), 0}
        });
    }

    public static Matr4x4 screen(double width, double height) {
        return new Matr4x4(new double[][]{
                {0.5 * width, 0, 0, 0},
                {0, -0.5 * height, 0, 0},
                {0, 0, 1, 0},
                {0.5 * width, 0.5 * height, 0, 1}
        });
    }

    public static Matr4x4 camera(double distance) {
        return new Matr4x4(new double[][]{
                {1, 0, 0, 0},
                {0, 1, 0, 0},
                {0, 0, 1, 0},
                {0, 0, -distance, 1}
        });
    }

    public static Matr4x4 getCameraMatrix(Matr4x4 cameraModel) {
        double[][] model = cameraModel.matrix;
        Vec3d xAxis = new Vec3d(model[0][0], model[0][1], model[0][2]);
        Vec3d yAxis = new Vec3d(model[1][0], model[1][1], model[1][2]);
        Vec3d zAxis = new Vec3d(model[2][0], model[2][1], model[2][2]);
        Vec3d eye = new Vec3d(model[3][0], model[3][1], model[3][2]);
        double aSqr = xAxis.sqr();
        double bSqr = yAxis.sqr();
        double cSqr = zAxis.sqr();
        return new Matr4x4(new double[][]{
                {xAxis.x / aSqr, yAxis.x / bSqr, zAxis.x / cSqr, 0},
                {xAxis.y / aSqr, yAxis.y / bSqr, zAxis.y / cSqr, 0},
                {xAxis.z / aSqr, yAxis.z / bSqr, zAxis.z / cSqr, 0},
                {-eye.Dot(xAxis) / aSqr, -eye.Dot(yAxis) / bSqr, -eye.Dot(zAxis) / cSqr, 1}
        });
    }

    public static Matr4x4 exampleCamera() {
        return new Matr4x4(new double[][]{
                {1, 0, 0, 0},
                {0, 1, 0, 0},
                {0, 0, 1, 0},
                {0, 0, -5, 1}
        });
    }

    public static Matr4x4 getCameraMatrix(Vec3d eye, Vec3d target, Vec3d up) {
        Vec3d zAxis = target.subtract(eye).toNormal();
        Vec3d xAxis = up.Cross(zAxis).toNormal();
        Vec3d yAxis = up;
        return new Matr4x4(new double[][]{
                {xAxis.x, yAxis.x, zAxis.x, 0},
                {xAxis.y, yAxis.y, zAxis.y, 0},
                {xAxis.z, yAxis.z, zAxis.z, 0},
                {-eye.Dot(xAxis), -eye.Dot(yAxis), -eye.Dot(zAxis), 1}
        });
    }

    public static Matr4x4 cameraViewMatrix(Vec3d eye, double pitch, double yaw) {
        double radPitch = pitch * Math.PI / 180;
        double radYaw = yaw * Math.PI / 180;
        double cosPitch = Math.cos(radPitch);
        double sinPitch = Math.sin(radPitch);
        double cosYaw = Math.cos(radYaw);
        double sinYaw = Math.sin(radYaw);
        Vec3d xAxis = new Vec3d(cosYaw, 0, -sinYaw);
        Vec3d yAxis = new Vec3d(sinYaw * sinPitch, cosPitch, cosYaw * sinPitch);
        Vec3d zAxis = new Vec3d(sinYaw * cosPitch, -sinPitch, cosPitch * cosYaw);
        return new Matr4x4(new double[][]{
                {xAxis.x, yAxis.x, zAxis.x, 0},
                {xAxis.y, yAxis.y, zAxis.y, 0},
                {xAxis.z, yAxis.z, zAxis.z, 0},
                {-xAxis.Dot(eye), -yAxis.Dot(eye), -zAxis.Dot(eye), 1}
        });
    }

    public static Matr4x4 arcBallCamera(double distance, double xRotate, double yRotate) {
        Vec3d position = new Vec3d(0, 0, -distance);
        Vec3d target = new Vec3d(0, 0, 0);
        Matr4x4 rotationX = Matr4x4.rotationX(-xRotate);
        position = position.subtract(target).multiply(rotationX);
        Matr4x4 rotationY = Matr4x4.rotationY(-yRotate);
        Vec3d finalPos = (position.subtract(target).multiply(rotationY));
        return Matr4x4.getCameraMatrix(finalPos, new Vec3d(0, 0, 0), new Vec3d(0, 1, 0));
    }
}
