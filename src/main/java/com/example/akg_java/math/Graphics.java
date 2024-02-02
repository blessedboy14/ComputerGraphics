package com.example.akg_java.math;

import com.sun.prism.paint.Color;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

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

    public void DDALine(int x1, int y1, int x2, int y2, int color, ZBuffer bf, Vec3d[] v) {
        double dx = (x2 - x1);
        double steps = Math.abs(dx);
        double x = x1;
        dx = dx / steps;
        for (int i = 0; i < (int)Math.round(steps); i++) {
            double z = calculateZValue(x, y1, v[0].x, v[0].y, v[0].z, v[1].x, v[1].y, v[1].z, v[2].x, v[2].y, v[2].z);
            if (z < bf.get((int)Math.round(x), y1)) {
                bf.edit((int)Math.round(x), y1, z);
                buffer.setRGB((int)Math.round(x), y1, color);
                x += dx;
            }
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

    private void setPixel(int j, int y, Vec3d[] v, ZBuffer bf, int color) {
        if (j > 0 && j < width && y > 0 && y < height) {
            double z = calculateZValue(j, y, v[0].x, v[0].y, v[0].z, v[1].x, v[1].y, v[1].z, v[2].x, v[2].y, v[2].z);
            if (z < bf.get(j, y)) {
                buffer.setRGB(j, y, color);
                bf.edit(j, y, z);
            }
        }
    }

    public void triangle(Triangle triangle, ZBuffer bf, int color) {
        sortVerticesByY(triangle);
        Vec3d[] v = triangle.getPoints();
        double total_height = v[2].y-v[0].y + 1;
        for (int y=(int)Math.round(v[0].y); y<=(int)Math.round(v[1].y); y++) {
            double segment_height = v[1].y-v[0].y + 1;
            double alpha = (y-v[0].y)/total_height + 0.0001;
            double beta  = (y-v[0].y)/segment_height + 0.0001;
            Vec3d A = v[0].add((v[2].subtract(v[0])).grade(alpha));
            Vec3d B = v[0].add((v[1].subtract(v[0])).grade(beta));
            if (A.x>B.x) {
                Vec3d temp = A;
                A = B;
                B = temp;
            }
            for (int j=(int)Math.round(A.x); j<=(int)Math.round(B.x); j++) {
                setPixel(j, y, v, bf, color);
            }
        }
        for (int y=(int)Math.round(v[1].y); y<=(int)Math.round(v[2].y); y++) {
            double segment_height = v[2].y-v[1].y + 1;
            double alpha = (y-v[0].y)/total_height;
            double beta  = (y-v[1].y)/segment_height; // be careful with divisions by zero
            Vec3d A = v[0].add((v[2].subtract(v[0])).grade(alpha));
            Vec3d B = v[1].add((v[2].subtract(v[1])).grade(beta));
            if (A.x>B.x) {
                Vec3d temp = A;
                A = B;
                B = temp;
            }
            for (int j=(int)Math.round(A.x); j<=(int)Math.round(B.x); j++) {
                setPixel(j, y, v, bf, color);
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

    private static double calculateZValue(double x, double y, double x0, double y0, double z0, double x1, double y1, double z1, double x2, double y2, double z2) {
        double denominator = (y1 - y2) * (x0 - x2) + (x2 - x1) * (y0 - y2);
        double b0 = ((y1 - y2) * (x - x2) + (x2 - x1) * (y - y2)) / denominator;
        double b1 = ((y2 - y0) * (x - x2) + (x0 - x2) * (y - y2)) / denominator;
        double b2 = 1 - b0 - b1;
        double z = b0 * z0 + b1 * z1 + b2 * z2;
        return z;
    }

    // more time-consuming
    public void rasterBarycentric(Triangle input, ZBuffer bf, int width, int height, int color) {//more time-consuming
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
                Vec3d bc_vec = barycentric(v, point);
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
