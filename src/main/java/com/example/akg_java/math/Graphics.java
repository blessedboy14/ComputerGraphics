package com.example.akg_java.math;

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
/*        if (x1 == x2 && y1 == y2) {
            if (Math.round(x1) >= 0 && Math.round(x1) < width - 1 && Math.round(y1) >= 0 && Math.round(y1) < height - 1) {
                buffer.setRGB(x1, y1, color);
            }
            return;
        }*/
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

    // sweep line algorithm
    public void test(Triangle input, int color) {
        sortVerticesByY(input);
        Vec3d[] vertices = input.getPoints();
        double total_height = vertices[2].y-vertices[0].y;
        for (int i=0; i<total_height; i++) {
            boolean second_half = i>vertices[1].y-vertices[0].y || vertices[1].y==vertices[0].y;
            double segment_height = second_half ? vertices[2].y-vertices[1].y : vertices[1].y-vertices[0].y;
            double alpha = i/total_height;
            double beta = (i - (second_half ? vertices[1].y - vertices[0].y : 0)) / segment_height;
            Vec3d A = vertices[0].add(vertices[2].subtract(vertices[0]).grade(alpha));
            Vec3d B = second_half ? vertices[1].add((vertices[2].subtract(vertices[1])).grade(beta))
                    : vertices[0].add((vertices[1].subtract(vertices[0])).grade(beta));
            if (A.x>B.x) Vec3d.swap(A, B);
            for (int j=(int)Math.round(A.x); j<=B.x; j++) {
                buffer.setRGB(j, (int)Math.round(vertices[0].y+i), color);
            }
        }
    }

    private Vec3d barycentric(Vec3d[] v, Vec3d p) {
        Vec3d u = new Vec3d(v[2].x- v[0].x, v[1].x - v[0].x, v[0].x - p.x)
                .Cross(new Vec3d(v[2].y- v[0].y, v[1].y - v[0].y, v[0].y - p.y));
        if (Math.abs(u.z) < 1) {
            return new Vec3d(-1, -1, -1);
        }
        return new Vec3d(1.f - (u.x+u.y)/u.z, u.y/u.z, u.x/u.z);
    }


    // more time-consuming
    public void rasterBarycentric(Triangle input, int width, int height, int color) {//more time-consuming
        Vec3d bboxmin = new Vec3d(width - 1, height - 1, 0);
        Vec3d bboxmax = new Vec3d(0, 0, 0);
        Vec3d clamp = new Vec3d(width - 1, height - 1, 0);
        Vec3d[] p = input.getPoints();
        for (int i = 0; i < 3; i++) {
            bboxmin.x = Math.max(0, Math.min(bboxmin.x, p[i].x));
            bboxmin.y = Math.max(0, Math.min(bboxmin.y, p[i].y));

            bboxmax.x = Math.min(clamp.x, Math.max(bboxmax.x, p[i].x));
            bboxmax.y = Math.min(clamp.y, Math.max(bboxmax.y, p[i].y));
        }
        Vec3d point = new Vec3d(0, 0, 0);
        for(point.x = bboxmin.x; point.x <= bboxmax.x; point.x++) {
            for(point.y = bboxmin.y; point.y <= bboxmax.y; point.y++) {
                Vec3d bc_vec = barycentric(p, point);
                if (!(bc_vec.x < 0 || bc_vec.y < 0  || bc_vec.z < 0)) {
                    buffer.setRGB((int)Math.round(point.x), (int)Math.round(point.y), color);
                }
            }
        }
    }

    public BufferedImage getBuffer() {
        return this.buffer;
    }
}
