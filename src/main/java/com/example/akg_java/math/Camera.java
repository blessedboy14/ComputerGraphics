package com.example.akg_java.math;

public class Camera {
    private Matr4x4 cameraProjection;
    public void setCameraProjection(Matr4x4 cameraProjection) {
        this.cameraProjection = cameraProjection;
    }
    public Matr4x4 getCameraProjection() {
        return this.cameraProjection;
    }

    public Camera(Matr4x4 cameraProjection) {
        this.cameraProjection = cameraProjection;
    }
}
