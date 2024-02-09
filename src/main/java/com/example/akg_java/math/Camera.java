package com.example.akg_java.math;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Camera {
    private Matr4x4 cameraProjection;

    public Camera(Matr4x4 cameraProjection) {
        this.cameraProjection = cameraProjection;
    }
}
