package com.example.akg_java.EngineUtility;

import com.example.akg_java.math.Matr4x4;
import com.example.akg_java.math.Vec3d;
import org.joml.Quaternionf;

public class Camera {
    private Matr4x4 cameraView;
    private Vec3d position;

    private Vec3d target = new Vec3d(0, 0, 0);
    private final Vec3d up = new Vec3d(0, 1, 0);
    private Vec3d eye = new Vec3d(0, 0, CAMERA_DISTANCE);
    public static float CAMERA_DISTANCE = 10.0f;
    public double new_y = 0;

    public Camera(Matr4x4 m) {
        this.cameraView = m;
        this.position = new Vec3d(0, 0, -CAMERA_DISTANCE);
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

    public void rotateCamera(double theta, double phi) {
        eye.x = CAMERA_DISTANCE * Math.sin(theta)  * Math.cos(phi);
        eye.z = CAMERA_DISTANCE * Math.sin(theta) * Math.sin(phi);
        eye.y = CAMERA_DISTANCE * Math.cos(theta) + new_y;
        this.setCameraView(Matr4x4.getCameraMatrix(eye, target, up));
    }

    public Vec3d getTarget() {
        return target;
    }

    public void setTarget(Vec3d lookAt) {
        this.target = lookAt;
    }

}
