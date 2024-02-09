package com.example.akg_java.EngineUtility;
import com.example.akg_java.math.Triangle;
import com.example.akg_java.math.Vec3d;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Comparator;

public class Graphics {
    private BufferedImage buffer;
    private final int width;
    private final int height;

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

    public void sortVerticesByY(Triangle input) {
        Comparator<Vec3d> comparator = Comparator.comparingDouble(vert -> vert.y);
        Arrays.sort(input.getPoints(), comparator);
    }

    private Vec3d Barycentric(Vec3d p, Vec3d a, Vec3d b, Vec3d c)
    {
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

    public void rasterBarycentric(Triangle input, ZBuffer bf, int width, int height, int color) {
        Vec3d bboxmin = new Vec3d(width - 1, height - 1, 0);
        Vec3d bboxmax = new Vec3d(0, 0, 0);
        Vec3d clamp = new Vec3d(width - 1, height - 1, 0);
        Vec3d[] v = input.getPoints();
        for (int i = 0; i < 3; i++) {
            bboxmin.x = Math.max(0, Math.min(bboxmin.x, v[i].x));
            bboxmin.y = Math.max(0, Math.min(bboxmin.y, v[i].y));
            bboxmax.x = Math.min(clamp.x, Math.max(bboxmax.x, v[i].x));
            bboxmax.y = Math.min(clamp.y, Math.max(bboxmax.y, v[i].y));
        }
        Vec3d point = new Vec3d(0, 0, 0);
        for(point.x = (int)Math.round(bboxmin.x); point.x <= (int)Math.round(bboxmax.x); point.x++) {
            for(point.y = (int)Math.round(bboxmin.y); point.y <= (int)Math.round(bboxmax.y); point.y++) {
/*                Vec3d bc_vec = barycentric(v, point);*/
                Vec3d bc_vec = Barycentric(point, v[0], v[1], v[2]);
                if ((bc_vec.x < 0 || bc_vec.y < 0  || bc_vec.z < 0)) {
                    continue;
                }
                point.z = 0;
                point.z += v[0].z*bc_vec.x + v[1].z*bc_vec.y + v[2].z*bc_vec.z;
                if (point.z < bf.get((int)Math.round(point.x), (int)Math.round(point.y))) {
                    bf.edit((int)Math.round(point.x), (int)Math.round(point.y), point.z);
                    buffer.setRGB((int)Math.round(point.x), (int)Math.round(point.y), color);
                }
            }
        }
    }

    public BufferedImage getBuffer() {
        return this.buffer;
    }
}
