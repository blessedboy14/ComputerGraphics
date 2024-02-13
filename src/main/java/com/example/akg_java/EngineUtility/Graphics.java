package com.example.akg_java.EngineUtility;
import com.example.akg_java.math.Triangle;
import com.example.akg_java.math.Vec3d;

import javax.naming.NameAlreadyBoundException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Comparator;

public class Graphics {
    private BufferedImage buffer;
    private final int width;
    private final int height;

    // light params
    private final double ambient = 0.0f;
    private final double diffuse = 1.f;
    private final double specular = 1.f;
    //

    public Graphics(BufferedImage buffer, int width, int height) {
        this.buffer = buffer;
        this.width = width;
        this.height = height;
    }

    public void clear(int color) {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                buffer.setRGB(i, j, color);
            }
        }
    }

    public void DDAline(int x1, int y1, int x2, int y2, int color) {
        double dx = (x2 - x1);
        double dy = (y2 - y1);
        double steps = Math.max(Math.abs(dx), Math.abs(dy));
        double x = x1;
        double y = y1;
        dx = dx / steps;
        dy = dy / steps;
        for (int i = 0; i < steps; ++i) {
            buffer.setRGB((int)Math.round(x), (int)Math.round(y), color);
            x += dx;
            y += dy;
        }
    }

    public void drawTriangle(Triangle tri, int color) {
        Vec3d[] v = tri.getPoints();
        DDAline((int)v[0].x, (int)v[0].y, (int)v[1].x, (int)v[1].y, color);
        DDAline((int)v[1].x, (int)v[1].y, (int)v[2].x, (int)v[2].y, color);
        DDAline((int)v[2].x, (int)v[2].y, (int)v[0].x, (int)v[0].y, color);
    }

    private Vec3d Barycentric(Vec3d p, Vec3d a, Vec3d b, Vec3d c) {
        Vec3d v0 = Vec3d.vectorOfTwoPoints(a, b);
        Vec3d v1 = Vec3d.vectorOfTwoPoints(a, c);
        Vec3d v2 = Vec3d.vectorOfTwoPoints(a, p);
        double d00 = v0.Dot(v0);
        double d01 = v0.Dot(v1);
        double d11 = v1.Dot(v1);
        double d20 = v2.Dot(v0);
        double d21 = v2.Dot(v1);
        double denom = d00 * d11 - d01 * d01;
        double v = (d11 * d20 - d01 * d21) / denom;
        double w = (d00 * d21 - d01 * d20) / denom;
        double u = 1.0f - v - w;
        return new Vec3d(v, w, u);
    }

    private Vec3d barycentric(Vec3d[] v, Vec3d p) {
        Vec3d u = new Vec3d(v[2].x- v[0].x, v[1].x - v[0].x, v[0].x - p.x)
                .Cross(new Vec3d(v[2].y- v[0].y, v[1].y - v[0].y, v[0].y - p.y));
        if (Math.abs(u.z) < 1) {
            return new Vec3d(-1, -1, -1);
        }
        return new Vec3d(1.f - (u.x+u.y)/u.z, u.y/u.z, u.x/u.z);
    }

//    public void rasterBarycentric(Triangle input, ZBuffer bf, int width, int height, int color) {
//        Vec3d bboxmin = new Vec3d(width - 1, height - 1, 0);
//        Vec3d bboxmax = new Vec3d(0, 0, 0);
//        Vec3d clamp = new Vec3d(width - 1, height - 1, 0);
//        Vec3d[] v = input.getPoints();
//        for (int i = 0; i < 3; i++) {
//            bboxmin.x = Math.max(0, Math.min(bboxmin.x, v[i].x));
//            bboxmin.y = Math.max(0, Math.min(bboxmin.y, v[i].y));
//            bboxmax.x = Math.min(clamp.x, Math.max(bboxmax.x, v[i].x));
//            bboxmax.y = Math.min(clamp.y, Math.max(bboxmax.y, v[i].y));
//        }
//        Vec3d point = new Vec3d(0, 0, 0);
//        for(point.x = (int)Math.round(bboxmin.x); point.x <= (int)Math.round(bboxmax.x); point.x++) {
//            for(point.y = (int)Math.round(bboxmin.y); point.y <= (int)Math.round(bboxmax.y); point.y++) {
//                Vec3d bc_vec = Barycentric(point, v[0], v[1], v[2]);
//                if ((bc_vec.x < 0 || bc_vec.y < 0  || bc_vec.z < 0)) {
//                    continue;
//                }
//                point.z = 0;
//                point.z += v[0].z*bc_vec.x + v[1].z*bc_vec.y + v[2].z*bc_vec.z;
//                if (point.z < bf.get((int)Math.round(point.x), (int)Math.round(point.y))) {
//                    bf.edit((int)Math.round(point.x), (int)Math.round(point.y), point.z);
//                    buffer.setRGB((int)Math.round(point.x), (int)Math.round(point.y), color);
//                }
//            }
//        }
//    }

    public void phongShading(Triangle tri, ZBuffer bf, java.awt.Color baseColor, Vec3d light) {
        Vec3d[] boxes = calculateBox(tri);
        Vec3d boxMin = boxes[0];
        Vec3d boxMax = boxes[1];
        Vec3d[] v = tri.getPoints();
        Vec3d point = new Vec3d(0, 0, 0);
        for(point.x = (int)Math.round(boxMin.x); point.x <= (int)Math.round(boxMax.x); point.x++) {
            for(point.y = (int)Math.round(boxMin.y); point.y <= (int)Math.round(boxMax.y); point.y++) {
                Vec3d bc_vec = Barycentric(point, v[0], v[1], v[2]);
                if ((bc_vec.x < 0 || bc_vec.y < 0  || bc_vec.z < 0)) {
                    continue;
                }
                point.z = 0;
                point.z += v[0].z*bc_vec.x + v[1].z*bc_vec.y + v[2].z*bc_vec.z;
                if (point.z < bf.get((int)Math.round(point.x), (int)Math.round(point.y))) {
                    int color = interpolate(tri.getNormals(), bc_vec, baseColor, light);
                    bf.edit((int)Math.round(point.x), (int)Math.round(point.y), point.z);
                    buffer.setRGB((int)Math.round(point.x), (int)Math.round(point.y), color);
                }
            }
        }
    }

    public void phongLight(Triangle tri, java.awt.Color base, Vec3d light, ZBuffer bf, Camera camera) {
        Vec3d[] boxes = calculateBox(tri);
        Vec3d boxMin = boxes[0];
        Vec3d boxMax = boxes[1];
        Vec3d[] v = tri.getPoints();
        Vec3d point = new Vec3d(0, 0, 0);
        for(point.x = (int)Math.round(boxMin.x); point.x <= (int)Math.round(boxMax.x); point.x++) {
            for(point.y = (int)Math.round(boxMin.y); point.y <= (int)Math.round(boxMax.y); point.y++) {
                Vec3d bc_vec = Barycentric(point, v[0], v[1], v[2]);
                if ((bc_vec.x < 0 || bc_vec.y < 0  || bc_vec.z < 0)) {
                    continue;
                }
                point.z = 0;
                point.z += v[0].z*bc_vec.x + v[1].z*bc_vec.y + v[2].z*bc_vec.z;
                if (point.z < bf.get((int)Math.round(point.x), (int)Math.round(point.y))) {
                    bf.edit((int)Math.round(point.x), (int)Math.round(point.y), point.z);
                    int[] colors = calculateADS(light, base,
                            tri.getNormals(), bc_vec, camera);
                    buffer.setRGB((int)Math.round(point.x), (int)Math.round(point.y),
                            colors[3]);
                }
            }
        }
    }

    private Vec3d reflection(Vec3d vector, Vec3d normal) {
        return vector.subtract(normal.grade(vector.Dot(normal)*2)).toNormal();
    }

    private int[] calculateADS(Vec3d light, Color base, Vec3d[] n, Vec3d barycentric,
                               Camera camera) {
        int ambient = new java.awt.Color((float) (base.getRed() * this.ambient) / 255,
                (float) (base.getGreen() * this.ambient) / 255,
                (float) (base.getBlue() * this.ambient) / 255).getRGB();
        double diffuse = interpolate(n, barycentric, light);
        Vec3d pointNormal = interpolateNormal(n, barycentric);
        Vec3d reflectVector = reflection(light, pointNormal);
        double specularStrength = Math.max(0.0f, reflectVector.Dot(camera.getEye().toNormal()));
        specularStrength = Math.pow(specularStrength, 10.0f) * this.specular;
        int specular = new java.awt.Color((float) (base.getRed() * specularStrength) / 255,
                (float) (base.getGreen() * specularStrength) / 255,
                (float) (base.getBlue() * specularStrength) / 255).getRGB();
        double resultColor = diffuse * specularStrength;
        int result = new java.awt.Color((float) (base.getRed() * resultColor ) / 255,
                (float) (base.getGreen() * resultColor) / 255,
                (float) (base.getBlue() * resultColor) / 255).getRGB();
        return new int[] {ambient, (int) diffuse, specular, result};
    }

    private int interpolate(Vec3d[] n, Vec3d bc_vec, java.awt.Color base, Vec3d light) {
        Vec3d n_interpolated = n[0].grade(bc_vec.z).add(n[1].grade(bc_vec.x)).add(n[2].grade(bc_vec.y)).toNormal();
        double intense = Math.max(0.0f, n_interpolated.Dot(light)) * this.diffuse;
        return new java.awt.Color((float) (base.getRed() * intense) / 255,
                (float) (base.getGreen() * intense) / 255,
                (float) (base.getBlue() * intense) / 255).getRGB();
    }

    private double interpolate(Vec3d[] n, Vec3d bc_vec, Vec3d light) {
        Vec3d n_interpolated = n[0].grade(bc_vec.z).add(n[1].grade(bc_vec.x)).add(n[2].grade(bc_vec.y)).toNormal();
        return Math.max(0.0f, n_interpolated.Dot(light)) * this.diffuse;
    }

    private Vec3d interpolateNormal(Vec3d[] n, Vec3d bc_vec) {
        return n[0].grade(bc_vec.z).add(n[1].grade(bc_vec.x)).add(n[2].grade(bc_vec.y)).toNormal();
    }

    private Vec3d[] calculateBox(Triangle tri) {
        Vec3d bboxmin = new Vec3d(width - 1, height - 1, 0);
        Vec3d bboxmax = new Vec3d(0, 0, 0);
        Vec3d clamp = new Vec3d(width - 1, height - 1, 0);
        Vec3d[] v = tri.getPoints();
        for (int i = 0; i < 3; i++) {
            bboxmin.x = Math.max(0, Math.min(bboxmin.x, v[i].x));
            bboxmin.y = Math.max(0, Math.min(bboxmin.y, v[i].y));
            bboxmax.x = Math.min(clamp.x, Math.max(bboxmax.x, v[i].x));
            bboxmax.y = Math.min(clamp.y, Math.max(bboxmax.y, v[i].y));
        }
        return new Vec3d[] {bboxmin, bboxmax};
    }

    public BufferedImage getBuffer() {
        return this.buffer;
    }
}
