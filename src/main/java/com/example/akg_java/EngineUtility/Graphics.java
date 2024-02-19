package com.example.akg_java.EngineUtility;
import com.example.akg_java.math.Matr4x4;
import com.example.akg_java.math.Texture;
import com.example.akg_java.math.Triangle;
import com.example.akg_java.math.Vec3d;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

public class Graphics {
    private final BufferedImage buffer;
    private final int width;
    private final int height;
    private final ZBuffer bf;
    private final Camera cam;

    // light params
    private final double ambient = 0.0f;
    private final double diffuse = 0.5f;
    private final double specular = 0.5f;
    private final double shininess = 6.0f;
    //

    public Graphics(BufferedImage buffer, int width, int height, ZBuffer bf, Camera cam) {
        this.buffer = buffer;
        this.width = width;
        this.height = height;
        this.bf = bf;
        this.cam = cam;
    }

    public void clear(int color) {
        for (int i = 0; i < width + 1; i++) {
            for (int j = 0; j < height + 1; j++) {
                buffer.setRGB(i, j, color);
            }
        }
    }

    private void BresenhamLine(int x1, int y1, int x2, int y2, int color) {
        boolean steep = false;
        if (Math.abs(x1-x2)<Math.abs(y1-y2)) {
            int temp = x1;
            x1 = y1;
            y1 = temp;
            temp = x2;
            x2 = y2;
            y2 = temp;
            steep = true;
        }
        if (x1>x2) {
            int temp = x1;
            x1 = x2;
            x2 = temp;
            temp = y1;
            y1 = y2;
            y2 = temp;
        }
        int dx = x2-x1;
        int dy = y2-y1;
        int derror = Math.abs(dy)*2;
        int error = 0;
        int y = y1;
        for (int x=x1; x<=x2; x++) {
            if (steep) {
                buffer.setRGB(y, x, color);
            } else {
                buffer.setRGB(x, y, color);
            }
            error += derror;
            if (error > dx) {
                y += (y2 > y1 ? 1 : -1);
                error -= dx*2;
            }
        }
    }

    private void DDAline(int x1, int y1, int x2, int y2, int color) {
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
        BresenhamLine((int)v[0].x, (int)v[0].y, (int)v[1].x, (int)v[1].y, color);
        BresenhamLine((int)v[1].x, (int)v[1].y, (int)v[2].x, (int)v[2].y, color);
        BresenhamLine((int)v[2].x, (int)v[2].y, (int)v[0].x, (int)v[0].y, color);
    }

    private Vec3d barycentric(Vec3d[] v, Vec3d p) {
        Vec3d u = new Vec3d(v[2].x- v[0].x, v[1].x - v[0].x, v[0].x - p.x)
                .Cross(new Vec3d(v[2].y- v[0].y, v[1].y - v[0].y, v[0].y - p.y));
        if (Math.abs(u.z) < 1) {
            return new Vec3d(-1, -1, -1);
        }
        return new Vec3d(1.f - (u.x+u.y)/u.z, u.y/u.z, u.x/u.z);
    }

    private Color calculateFromIntense(Color base, double intense) {
        float red = (float) (base.getRed() * intense);
        float green = (float) (base.getGreen() * intense);
        float blue = (float) (base.getBlue() * intense);
        return new Color(red / 255, green / 255, blue / 255);
    }

    private Color calculateColor(Triangle tri, Color base, Vec3d lightDir) {
        Vec3d[] v = tri.getPoints();
        Vec3d normal = v[2].subtract(v[0]).Cross(v[1].subtract(v[0])).toNormal();
        double intense = Math.max(0.0f, normal.Dot(lightDir.grade(-1)));
        return calculateFromIntense(base, intense);
    }

    public void rasterize(Triangle tri, Matr4x4 resMatrix, Color clr, Vec3d lightDir) {
/*        Color pixelColor = calculateColor(tri, clr, lightDir);*/
        tri = tri.multiplyMatrix(resMatrix);
        Vec3d[] v = tri.getPoints();
        int minY = (int) Math.round(Math.max(0.0f, Collections.min(Arrays.asList(v), Comparator.comparingDouble(Vec3d::getY)).getY()));
        int minX = (int) Math.round(Math.max(0.0f, Collections.min(Arrays.asList(v), Comparator.comparingDouble(Vec3d::getX)).getX()));
        int maxY = (int) Math.round(Math.min(height, Collections.max(Arrays.asList(v), Comparator.comparingDouble(Vec3d::getY)).getY()));
        int maxX = (int) Math.round(Math.min(width, Collections.max(Arrays.asList(v), Comparator.comparingDouble(Vec3d::getX)).getX()));
        Vec3d p = new Vec3d(0, 0, 0);
        for (p.x = minX; p.x <= maxX; p.x++) {
            for (p.y = minY; p.y <= maxY; p.y++) {
                Vec3d bc_coords = barycentric(v, p);
                if (!(bc_coords.x < 0 || bc_coords.y < 0 || bc_coords.z < 0)) {
                    p.z = v[0].z * bc_coords.x + v[1].z * bc_coords.y + v[2].z * bc_coords.z;
                    int px = (int)Math.round(p.x);
                    int py = (int)Math.round(p.y);
                    if (p.z < bf.get(px, py)) {
                        bf.edit(px, py, p.z);
/*                        Color pixelColor = phongShadingColor(tri.getNormals(), bc_coords, clr, lightDir);*/
                        Color pixelColor = phongLightColor(tri.getNormals(), bc_coords, clr, lightDir);
                        buffer.setRGB(px, py, pixelColor.getRGB());
                    }
                }
            }
        }
    }

    private Color phongShadingColor(Vec3d[] n, Vec3d bc_coords, Color clr, Vec3d light) {
        Vec3d interpolatedNormal = n[0].grade(bc_coords.x)
                .add(n[1].grade(bc_coords.y))
                .add(n[2].grade(bc_coords.z)).toNormal();
        double intense = Math.max(0.0f, interpolatedNormal.Dot(light));
        return calculateFromIntense(clr, intense);
    }

    private Color phongLightColor(Vec3d[] n, Vec3d bc_coords, Color clr, Vec3d light) {
        Vec3d ambient = new Vec3d(clr.getRed(), clr.getGreen(), clr.getBlue());
        Color diffuseClr = phongShadingColor(n, bc_coords, clr, light);
        Vec3d diffuse = new Vec3d(diffuseClr.getRed(), diffuseClr.getGreen(), diffuseClr.getBlue());
        Vec3d reflect = reflection(light, getPointNormal(n, bc_coords));
        double sStrength = Math.pow(Math.max(0.0f, cam.getEye().grade(-1).toNormal().Dot(reflect)), shininess);
        Vec3d specular = new Vec3d(clr.getRed() * sStrength, clr.getGreen() * sStrength, clr.getBlue() * sStrength);
        Vec3d resClr = ambient.grade(this.ambient)
                .add(diffuse.grade(this.diffuse))
                .add(specular.grade(this.specular));
        return new Color((float)(Math.min(255.f, resClr.x))/ 255,
                (float)(Math.min(255.f, resClr.y)) / 255,
                (float)(Math.min(255.f, resClr.z)) / 255);
    }

    public void tryToMakeDiffuseMap(Triangle tri, Matr4x4 matrix, BufferedImage diff, BufferedImage norm, BufferedImage spec,
                                    Color clr, Vec3d lightDir) {
        tri = tri.multiplyMatrix2(matrix);
        Vec3d[] v = tri.getPoints();
        int minY = (int) Math.round(Math.max(0.0f, Collections.min(Arrays.asList(v), Comparator.comparingDouble(Vec3d::getY)).getY()));
        int minX = (int) Math.round(Math.max(0.0f, Collections.min(Arrays.asList(v), Comparator.comparingDouble(Vec3d::getX)).getX()));
        int maxY = (int) Math.round(Math.min(height, Collections.max(Arrays.asList(v), Comparator.comparingDouble(Vec3d::getY)).getY()));
        int maxX = (int) Math.round(Math.min(width, Collections.max(Arrays.asList(v), Comparator.comparingDouble(Vec3d::getX)).getX()));
        Vec3d p = new Vec3d(0, 0, 0);
        for (p.x = minX; p.x <= maxX; p.x++) {
            for (p.y = minY; p.y <= maxY; p.y++) {
                Vec3d bc_coords = barycentric(v, p);
                if (!(bc_coords.x < 0 || bc_coords.y < 0 || bc_coords.z < 0)) {
                    p.z = v[0].z * bc_coords.x + v[1].z * bc_coords.y + v[2].z * bc_coords.z;
                    int px = (int)Math.round(p.x);
                    int py = (int)Math.round(p.y);
                    if (p.z < bf.get(px, py)) {
                        bf.edit(px, py, p.z);
                        Vec3d[] t = tri.getTexturesAsVec();
                        int pixelColor = applyMaps(diff, norm, spec, bc_coords, t, lightDir);
                        buffer.setRGB(px, py, pixelColor);
                    }
                }
            }
        }
    }

    private int applyMaps(BufferedImage diffuse, BufferedImage normals, BufferedImage spec, Vec3d bc_coords, Vec3d[] t,
                          Vec3d light) {
        Vec3d uv = t[0].grade(bc_coords.x).add(t[1].grade(bc_coords.y)).add(t[2].grade(bc_coords.z));
        int xCoord = (int) Math.round(diffuse.getWidth() * uv.x);
        int yCoord = (int) Math.round(diffuse.getHeight() * (1-uv.y));
        int normal_clr = normals.getRGB(xCoord, yCoord);
        double red = ((normal_clr >> 16) & 0xFF) / 255.0f;
        double green = ((normal_clr >> 8) & 0xFF) / 255.0f;
        double blue = (normal_clr & 0xFF) / 255.0f;
        Vec3d normal_c = new Vec3d(red, green, blue);
        normal_c = normal_c.grade(2).subtract(new Vec3d(1, 1, 1)).toNormal();
        double intense = Math.max(0.0f, normal_c.Dot(light));
        int diff = diffuse.getRGB(xCoord, yCoord);
        red = Math.min(255.0f, ((diff >> 16) & 0xFF) * intense);
        green = Math.min(255.0f, ((diff >> 8) & 0xFF) * intense);
        blue = Math.min(255.0f, (diff & 0xFF) * intense);
        Vec3d resclr = new Vec3d(red, green, blue);
        int spec_clr = spec.getRGB(xCoord, yCoord);
        double specular_coef = Math.pow(((spec_clr >> 16) & 0xFF) / 255.0f, shininess);
        Vec3d spec_color = new Vec3d(255, 255, 255);
        resclr = resclr.add(spec_color.grade(specular_coef));
        return new Color((float) Math.min(1.0f, resclr.x / 255.0f),
            (float) Math.min(1.0f, resclr.y / 255.0f), (float) Math.min(1.0f, resclr.z / 255.0f)).getRGB();
/*        return new Color((float) red / 255.0f, (float) green / 255.0f, (float) blue / 255.0f).getRGB();*/
    }

    private Vec3d reflection(Vec3d vector, Vec3d normal) {
        return vector.subtract(normal.grade(vector.Dot(normal)*2)).toNormal();
    }

    private Vec3d getPointNormal(Vec3d[] n, Vec3d bc_coords) {
        return n[0].grade(bc_coords.x).add(n[1].grade(bc_coords.y)).add(n[2].grade(bc_coords.z)).toNormal();
    }

    public BufferedImage getBuffer() {
        return this.buffer;
    }
}
