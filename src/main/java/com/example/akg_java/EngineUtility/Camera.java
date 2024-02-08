package com.example.akg_java.EngineUtility;

import com.example.akg_java.math.Matr4x4;
import com.example.akg_java.math.Vec3d;

public class Camera {
    private Matr4x4 cameraView;
    private Vec3d position;
    public static float CAMERA_DISTANCE = 200.0f;

    private Vec3d lookAt;

    public Camera(Matr4x4 m) {
        this.cameraView = m;
        this.position = new Vec3d(0, 0, CAMERA_DISTANCE);
    }

    public void updateDistance() {
        this.cameraView.matrix[3][2] = CAMERA_DISTANCE;
        this.position.z = CAMERA_DISTANCE;
    }

    public Matr4x4 getCameraView() {
        return cameraView;
    }

    public Vec3d getPosition () {
        return this.position;
    }

    public void setPosition(Vec3d pos) {
        this.position = pos;
    }

    public void setCameraView(Matr4x4 newView) {
        this.cameraView = newView;
    }

    public void rotate(double pitch, double yaw) {
        this.setCameraView(Matr4x4.arcBallCamera(CAMERA_DISTANCE, pitch, yaw));
    }

    public Vec3d getLookAt() {
        return lookAt;
    }

    public void lookAt(Vec3d lookAt) {
        this.lookAt = lookAt;
    }

}
