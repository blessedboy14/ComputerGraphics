package com.example.akg_java.math;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Comparator;

public class Graphics {
    private BufferedImage buffer;
    private int width;
    private int height;

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

    public void fillBottomFlat(Vec3d first, Vec3d second, Vec3d third, int color) {
        double side1 = (second.x - first.x) / (second.y - first.y);// FILL BOTTOM FLAT TRIANGLE
        double side2 = (third.x - first.x) / (third.y - first.y);
        double curx1 = first.x;
        double curx2 = first.x;
        for (int scanLine = (int)first.y; scanLine <= second.y; scanLine++) {
            DDAline((int)curx1, scanLine, (int)curx2, scanLine, color);
            curx1 += side1;
            curx2 += side2;
        }
    }

    public void drawTriangle(Triangle tri, int color) {
        Vec3d[] v = tri.getPoints();
        DDAline((int)v[0].x, (int)v[0].y, (int)v[1].x, (int)v[1].y, color);
        DDAline((int)v[1].x, (int)v[1].y, (int)v[2].x, (int)v[2].y, color);
        DDAline((int)v[2].x, (int)v[2].y, (int)v[0].x, (int)v[0].y, color);
    }

    public void fillTopFlat(Vec3d first, Vec3d second, Vec3d third, int color) {
        double side1 = (third.x - first.x) / (third.y - first.y);// FILL TOP FLAT TRIANGLE
        double side2 = (third.x - second.x) / (third.y - second.y);
        double curx1 = third.x;
        double curx2 = third.x;
        for (int scanLine = (int)third.y; scanLine > third.y; scanLine--) {
            DDAline((int)curx1, scanLine, (int)curx2, scanLine, color);
            curx1 -= side1;
            curx2 -= side2;
        }
    }

    public void sortVerticesByY(Triangle input) {
        Comparator<Vec3d> comparator = Comparator.comparingDouble(vert -> vert.y);
        Arrays.sort(input.getPoints(), comparator);
    }

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
/*            DDAline((int)Math.round(A.x), (int)Math.round(A.y), (int)Math.round(B.x), (int)Math.round(B.y), color);*/
            for (int j=(int)Math.round(A.x); j<=B.x; j++) {
                buffer.setRGB(j, (int)Math.round(vertices[0].y+i), color); // attention, due to int casts t0.y+i != A.y
            }
        }
    }

    public void rasterize(Triangle input, int color) {
        sortVerticesByY(input);
        Vec3d[] vertices = input.getPoints();
        if (vertices[1].y == vertices[2].y) {
            fillBottomFlat(vertices[0], vertices[1], vertices[2], color);
        } else if (vertices[0].y == vertices[1].y) {
            fillTopFlat(vertices[0], vertices[1], vertices[2], color);
        } else {
            Vec3d divider = new Vec3d((vertices[0].x + vertices[2].x) / 2,
                    vertices[1].y, 0);
            fillBottomFlat(vertices[0], vertices[1], divider, color);
            fillTopFlat(vertices[1], divider, vertices[2], color);
        }
    }

    public BufferedImage getBuffer() {
        return this.buffer;
    }
}
