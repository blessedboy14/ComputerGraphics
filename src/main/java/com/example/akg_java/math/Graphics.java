package com.example.akg_java.math;

import java.awt.image.BufferedImage;

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
/*        do {
            if (Math.round(x) >= 0 && Math.round(x) < width - 1 && Math.round(y) >= 0 && Math.round(y) < height - 1) {
                buffer.setRGB((int) Math.round(x), (int) Math.round(y), color);
            }
            x += dx;
            y += dy;
        } while ((x != x2 || x1 == x2) && (y != y2 || y1 == y2));*/
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

/*    public void rasterize(Vec3d first, Vec3d second, Vec3d third, int color) {
        Vec3d[] points = Arrays.stream(triangle.getPoints())
                .sorted((vec1, vec2) -> (int) Math.ceil(vec1.y - vec2.y))
                .map(vec -> new Vec3d((int) vec.getX(), (int) vec.y, (int) vec.z))
                .toArray(Vec3d[]::new);
        if (vertices[1].y == vertices[2].y) {
            fillBottomFlat(vertices[0], vertices[1], vertices[2], color);
        } else if (vertices[0].y == vertices[1].y) {
            fillTopFlat(vertices[0], vertices[1], vertices[2], color);
        } else {
            Vec3d divider = new Vec3d(vertices[0].x +
                    (vertices[1].y - vertices[0].y) / (vertices[2].y - vertices[0].y) * (vertices[2].x - vertices[0].x),
                    vertices[1].y, 0);
            fillBottomFlat(vertices[0], vertices[1], divider, color);
            fillTopFlat(vertices[1], divider, vertices[2], color);
        }
    }*/

    public BufferedImage getBuffer() {
        return this.buffer;
    }
}
