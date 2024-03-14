package com.example.akg_java.EngineUtility;

import com.example.akg_java.math.Vec3d;

public class Ray {

    public Vec3d pos;
    public Vec3d direction;
    public float tMin;
    public float tMax;

    public Ray() {
        pos = Vec3d.zero();
        direction = new Vec3d(0, 0, -1);
    }

    public Ray(Vec3d pos, Vec3d dir) {
        this.pos = pos;
        this.direction = dir;
    }

}
