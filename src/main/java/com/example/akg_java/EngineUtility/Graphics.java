package com.example.akg_java.EngineUtility;
import com.example.akg_java.math.Matr4x4;
import com.example.akg_java.math.Texture;
import com.example.akg_java.math.Triangle;
import com.example.akg_java.math.Vec3d;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;

public class Graphics {
    private final BufferedImage buffer;
    private final int width;
    private final int height;
    private final ZBuffer bf;
    private final Camera cam;

    private boolean isTangentSpace;

    // light params
    private final double ambient = 0.0f;
    private final double diffuse = 0.5f;
    private final double specular = 0.5f;
    private final double shininess = 32.0f;
    //

    private Color reflectedClr = Color.WHITE;

    public Graphics(BufferedImage buffer, int width, int height, ZBuffer bf, Camera cam, boolean isTangentSpace) {
        this.buffer = buffer;
        this.width = width;
        this.height = height;
        this.bf = bf;
        this.cam = cam;
        this.isTangentSpace = isTangentSpace;
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
        Vec3d ac = v[2].subtract(v[0]);
        Vec3d ap = v[0].subtract(p);
        Vec3d ab = v[1].subtract(v[0]);
        double square = (ac.x * ab.y - ac.y * ab.x);
        double u2 = (-ac.x * ap.y + ac.y * ap.x) / square;
        double w = (ab.x * ap.y - ab.y * ap.x) / square;
        return new Vec3d(1- u2 - w, u2, w);
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
        Vec3d[] oldP = tri.getPoints();
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
                        Color pixelColor = phongLightColor(tri.getNormals(), bc_coords, clr, lightDir, oldP);
                        int colorInt = new Color((float)Math.pow(pixelColor.getRed() / 255.0f, 1.0f/2.2f),
                                (float)Math.pow(pixelColor.getGreen()  / 255.0f, 1.0f/2.2f),
                                (float)Math.pow(pixelColor.getBlue()  / 255.0f, 1.0f/2.2f)).getRGB();
                                buffer.setRGB(px, py, colorInt);
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

    private Color phongLightColor(Vec3d[] n, Vec3d bc_coords, Color clr, Vec3d light, Vec3d[] points) {
        Vec3d point = points[0].grade(bc_coords.x)
                .add(points[1].grade(bc_coords.y))
                .add(points[2].grade(bc_coords.z));
        light = cam.getEye().subtract(point).toNormal();
        Vec3d ambient = new Vec3d(clr.getRed(), clr.getGreen(), clr.getBlue());
        Color diffuseClr = phongShadingColor(n, bc_coords, clr, light);
        Vec3d diffuse = new Vec3d(diffuseClr.getRed(), diffuseClr.getGreen(), diffuseClr.getBlue());
/*        Vec3d reflect = reflection(light, getPointNormal(n, bc_coords)).toNormal();*/
        Vec3d h = light.add(light).toNormal();
/*        double sStrength = Math.pow(Math.max(0.0f, cam.getEye().grade(-1).toNormal().Dot(reflect)), shininess);*/
        double sStrength = Math.pow(Math.max(0.0f, h.Dot(getPointNormal(n, bc_coords).toNormal())), shininess);
        clr = Color.WHITE;
        Vec3d specular = new Vec3d(clr.getRed() * sStrength, clr.getGreen() * sStrength, clr.getBlue() * sStrength);
        Vec3d resClr = ambient.grade(this.ambient)
                .add(diffuse.grade(this.diffuse))
                .add(specular.grade(this.specular));
        return new Color((float)(Math.min(255.f, resClr.x))/ 255,
                (float)(Math.min(255.f, resClr.y)) / 255,
                (float)(Math.min(255.f, resClr.z)) / 255);
    }

    public void tryToMakeDiffuseMap(Triangle tri, Matr4x4 matrix, BufferedImage diff, BufferedImage norm, BufferedImage spec,
                                    Vec3d lightDir, Color clr) {
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
                        int pixelColor = applyMaps(diff, norm, spec, bc_coords, t, lightDir, tri.getNormals(), clr,
                                tri.getPoints());
                        buffer.setRGB(px, py, pixelColor);
                    }
                }
            }
        }
    }

    public void tryToApplyMultiple(BufferedImage[] textures, Triangle tri, Matr4x4 matrix, Vec3d light, Color clr) {
        tri = tri.multiplyMatrix2(matrix);
        Vec3d[] v = tri.getPoints();
        int minY = (int) Math.floor(Math.max(0.0f, Collections.min(Arrays.asList(v), Comparator.comparingDouble(Vec3d::getY)).getY()));
        int minX = (int) Math.floor(Math.max(0.0f, Collections.min(Arrays.asList(v), Comparator.comparingDouble(Vec3d::getX)).getX()));
        int maxY = (int) Math.ceil(Math.min(height, Collections.max(Arrays.asList(v), Comparator.comparingDouble(Vec3d::getY)).getY()));
        int maxX = (int) Math.ceil(Math.min(width, Collections.max(Arrays.asList(v), Comparator.comparingDouble(Vec3d::getX)).getX()));
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
                        int pixelColor = applyMaps(textures[0], textures[1], textures[2], bc_coords, t, light,
                                tri.getNormals(), v, clr);
                        buffer.setRGB(px, py, pixelColor);
                    }
                }
            }
        }
    }

    private int[] determineBox(Vec3d[] points) {
        int[] res = new int[4];
        res[0] = (int) Math.floor(Math.max(0.0f, Collections.min(Arrays.asList(points),
                Comparator.comparingDouble(Vec3d::getY)).getY()));
        res[1] = (int) Math.floor(Math.max(0.0f, Collections.min(Arrays.asList(points),
                Comparator.comparingDouble(Vec3d::getX)).getX()));
        res[2] = (int) Math.ceil(Math.min(height, Collections.max(Arrays.asList(points),
                Comparator.comparingDouble(Vec3d::getY)).getY()));
        res[3] = (int) Math.ceil(Math.min(width, Collections.max(Arrays.asList(points),
                Comparator.comparingDouble(Vec3d::getX)).getX()));
        return res;
    }

    public void textureTriangle(Triangle tri, Matr4x4 matr, BufferedImage[] textures, Vec3d light) {
        tri = tri.multiplyMatrix2(matr);
        Vec3d[] points = tri.getPoints();
        int[] box = determineBox(points);
        Vec3d p = Vec3d.zero();
        for (p.x = box[1]; p.x <= box[3]; p.x++) {
            for (p.y = box[0]; p.y <= box[2]; p.y++) {
                Vec3d bc = barycentric(points, p);
                if (!(bc.x < 0 || bc.y < 0 || bc.z < 0)) {
                    p.z = points[0].z * bc.x + points[1].z * bc.y + points[2].z * bc.z;
                    int px = (int)Math.round(p.x);
                    int py = (int)Math.round(p.y);
                    if (p.z < bf.get(px, py)) {
                        bf.edit(px, py, p.z);
                        int pixelColor = universalApply(textures, bc, tri, light);
                        buffer.setRGB(px, py, pixelColor);
                    }
                }
            }
        }
    }

    private Texture[] divideByZ(Texture[] txts, Vec3d[] points) {
        Texture[] newTextures = new Texture[txts.length];
        for (int i = 0; i < txts.length; i++) {
            newTextures[i] = txts[i].divide(points[i].w);
        }
        return newTextures;
    }

    private int universalApply(BufferedImage[] maps, Vec3d bc, Triangle tri, Vec3d light) {
        Texture[] textures = divideByZ(tri.getTextures(), tri.getPoints());
        Vec3d uv = textures[0].grade(bc.x)
                .add(textures[1].grade(bc.y))
                .add(textures[2].grade(bc.z))
                .toVec();
        double z = 1 / uv.z;
        uv = uv.grade(z);
        uv.x = (uv.x < 0 ? (Math.abs(uv.x) % 1.0f) : uv.x % 1.0f);
        uv.y = 1 - (uv.y < 0 ? (Math.abs(uv.y) % 1.0f) : uv.y % 1.0f);
        Vec3d diffuse = diffuseApply(maps[0], uv);
        Vec3d normal = new Vec3d(1, 1, 1);
        double intense = 1;
        if (maps[1] != null) {
            normal = universalNormalApply(maps[1], uv, tri,getPointNormal(tri.getNormals(), bc), light);
            intense = Math.max(0.0f, normal.Dot(light));
        }
        double specClr = 0;
        if (maps[2] != null) {
            specClr = specularApply(maps[2], uv, normal, light);
        }
        diffuse = diffuse.grade(intense + specClr);
        return new Color((float) Math.min(1.0f, (diffuse.x) / 255.0f),
                (float) Math.min(1.0f, (diffuse.y) / 255.0f), (float) Math.min(1.0f, (diffuse.z) / 255.0f)).getRGB();
    }

    private Vec3d diffuseApply(BufferedImage diffuse, Vec3d uv) {
        int x = (int) Math.round((diffuse.getWidth() - 1) * uv.x);
        int y = (int) Math.round((diffuse.getHeight() - 1) * uv.y);
        int diff = diffuse.getRGB(x, y);
        double red = (diff >> 16) & 0xFF;
        double green = (diff >> 8) & 0xFF;
        double blue = diff & 0xFF;
        return new Vec3d(red, green, blue);
    }

    private Vec3d universalNormalApply(BufferedImage normal, Vec3d uv, Triangle tri, Vec3d n, Vec3d light) {
        int x = (int)Math.round((normal.getWidth() - 1) * uv.x);
        int y = (int)Math.round((normal.getHeight() - 1) * uv.y);
        int normal_clr = normal.getRGB(x, y);
        double red;
        double green;
        double blue;
        if (!isTangentSpace) {
/*            red = 1 - ((normal_clr >> 16) & 0xFF) / 255.0f;*/
            red = ((normal_clr >> 16) & 0xFF) / 255.0f;
            green = ((normal_clr >> 8) & 0xFF) / 255.0f;
            blue = (normal_clr & 0xFF) / 255.0f;
        } else {
            red = ((normal_clr >> 16) & 0xFF) / 255.0f;
            green = 1 - ((normal_clr >> 8) & 0xFF) / 255.0f;
            blue = (normal_clr & 0xFF) / 255.0f;
        }
        Vec3d clr = new Vec3d(red, green, blue);
        if (!isTangentSpace) {
            clr = clr.grade(2).subtract(new Vec3d(1, 1, 1)).toNormal();
        } else {
            clr = clr.grade(2).subtract(new Vec3d(1, 1, 1))
                    .saveMultiply(fromTangentToWorld(tri.getPoints(),
                            n, tri.getTexturesAsVec())).toNormal();
        }
        return clr;
    }

/*    private Vec3d specularApply2(BufferedImage specular, Vec3d uv, Color baseColor, Vec3d normal, Vec3d light) {
        int x = (int)Math.round((specular.getWidth() - 1) * uv.x);
        int y = (int)Math.round((specular.getHeight() - 1) * uv.y);
        int spec_clr = specular.getRGB(x, y);
        double red = ((spec_clr >> 16) & 0xFF);
        double green = ((spec_clr >> 8) & 0xFF);
        double blue = (spec_clr & 0xFF);
        double intensity = (red + green + blue) / 3.0f;
        Vec3d reflection = reflection(light, normal).toNormal();
        double specStrength = Math.pow(
                Math.max(0.0f, cam.getEye().grade(-1).toNormal().Dot(reflection)), intensity);
        return new Vec3d(baseColor.getRed() * specStrength, baseColor.getGreen() * specStrength,
                baseColor.getBlue() * specStrength);
    }*/

    private double specularApply(BufferedImage specular, Vec3d uv, Vec3d normal, Vec3d light) {
        int x = (int)Math.round((specular.getWidth() - 1) * uv.x);
        int y = (int)Math.round((specular.getHeight() - 1) * uv.y);
        int spec_clr = specular.getRGB(x, y);
        double glossiness = 1 - (((spec_clr >> 8) & 0xFF) / 255.0f);
        double a4 = Math.pow(glossiness, 4.0f);
        Vec3d h = light.add(light).toNormal();
        double sStrength = Math.pow(Math.max(0.0f, h.Dot(normal.toNormal())), a4 * 512) * a4 * 10;
/*        Vec3d reflection = reflection(light, normal).toNormal();*/
        return sStrength;
    }

    private int applyMaps(BufferedImage diffuse, BufferedImage normal, BufferedImage spec, Vec3d bc_coords, Vec3d[] t,
                          Vec3d light, Vec3d[] n, Vec3d[] p, Color clr) {
        Vec3d uv = t[0].grade(bc_coords.x)
                .add(t[1].grade(bc_coords.y))
                .add(t[2].grade(bc_coords.z));
        uv.x = (uv.x < 0 ? (Math.abs(uv.x) % 1.0f) : uv.x % 1.0f);
/*        uv.y = (uv.y < 0 ? (Math.abs(uv.y) % 1.0f) : uv.y % 1.0f);*/
        uv.y = 1 - (uv.y < 0 ? (Math.abs(uv.y) % 1.0f) : uv.y % 1.0f);
        double intense = 1;
        if (normal != null) {
            intense = applyNormalMap(normal, uv, light, p, getPointNormal(n, bc_coords), t);
        }
        Vec3d resClr = applyDiffuseMap(diffuse, uv, intense);
        if (spec != null) {
            Vec3d specClr = applySpecularMap(spec, uv, light, n, bc_coords, clr).grade(0.2f);
            resClr = resClr.add(specClr);
        }
        return new Color((float) Math.min(1.0f, (resClr.x) / 255.0f),
                (float) Math.min(1.0f, (resClr.y) / 255.0f), (float) Math.min(1.0f, (resClr.z) / 255.0f)).getRGB();
    }

    private int applyMaps(BufferedImage diffuse, BufferedImage normal, BufferedImage spec, Vec3d bc_coords, Vec3d[] t,
                      Vec3d light, Vec3d[] n, Color clr, Vec3d[] p) {
        Vec3d uv = t[0].grade(bc_coords.x)
                .add(t[1].grade(bc_coords.y))
                .add(t[2].grade(bc_coords.z));
        uv.x = 1 - (uv.x < 0? 1-(Math.abs(uv.x) % 1.0f) : uv.x % 1.0f);
        uv.y = 1 - (uv.y < 0? 1-(Math.abs(uv.y) % 1.0f) : uv.y % 1.0f);
        double intense = 1;
        if (normal != null) {
            intense = applyNormalMap(normal, uv, light);
        }
        Vec3d resClr = applyDiffuseMap(diffuse, uv, intense);
        if (spec != null) {
            Vec3d specClr = applySpecularMap(spec, uv, light, n, bc_coords, clr).grade(0.2f);
            resClr = resClr.add(specClr);
        }
        return new Color((float) Math.min(1.0f, (5 + resClr.x) / 255.0f),
            (float) Math.min(1.0f, (5 + resClr.y) / 255.0f), (float) Math.min(1.0f, (5 + resClr.z) / 255.0f)).getRGB();
    }

    private Vec3d applyDiffuseMap(BufferedImage diffuse, Vec3d uv, double intense) {
        int xCoord = (int) Math.round((diffuse.getWidth() - 1) * uv.x);
        int yCoord = (int) Math.round((diffuse.getHeight() - 1) * uv.y);
        int diff = diffuse.getRGB(xCoord, yCoord);
        double red = Math.min(255.0f, ((diff >> 16) & 0xFF) * intense);
        double green = Math.min(255.0f, ((diff >> 8) & 0xFF) * intense);
        double blue = Math.min(255.0f, (diff & 0xFF) * intense);
        return new Vec3d(red, green, blue);
    }

    private double applyNormalMap(BufferedImage normal, Vec3d uv, Vec3d light) {
        int nCoordX = (int)Math.round((normal.getWidth() - 1) * uv.x);
        int nCoordY = (int)Math.round((normal.getHeight() - 1) * uv.y);
        int normal_clr = normal.getRGB(nCoordX, nCoordY);
        double red = 1 - ((normal_clr >> 16) & 0xFF) / 255.0f;
        double green = ((normal_clr >> 8) & 0xFF) / 255.0f;
        double blue = (normal_clr & 0xFF) / 255.0f;
        Vec3d normal_c = new Vec3d(red, green, blue);
        normal_c = normal_c.grade(2).subtract(new Vec3d(1, 1, 1)).toNormal();
        return Math.max(0.0f, normal_c.Dot(light));
    }

    private double applyNormalMap(BufferedImage normal, Vec3d uv, Vec3d light, Vec3d[] v, Vec3d n, Vec3d[] txt) {
        int nCoordX = (int)Math.round((normal.getWidth() - 1) * uv.x);
        int nCoordY = (int)Math.round((normal.getHeight() - 1) * uv.y);
        int normal_clr = normal.getRGB(nCoordX, nCoordY);
        double red = ((normal_clr >> 16) & 0xFF) / 255.0f;
        double green = 1 - ((normal_clr >> 8) & 0xFF) / 255.0f;
        double blue = (normal_clr & 0xFF) / 255.0f;
        Vec3d normal_c = new Vec3d(red, green, blue);
        normal_c = normal_c.grade(2).subtract(new Vec3d(1, 1, 1))
                .saveMultiply(fromTangentToWorld(v, n, txt)).toNormal();
        return Math.max(0.0f, normal_c.Dot(light));
    }

    private Vec3d applySpecularMap(BufferedImage specular, Vec3d uv, Vec3d light, Vec3d[] n, Vec3d bc_coords, Color clr) {
        int sCoordX = (int)Math.round((specular.getWidth() - 1) * uv.x);
        int sCoordY = (int)Math.round((specular.getHeight() - 1) * uv.y);
        int spec_clr = specular.getRGB(sCoordX, sCoordY);
        double red = ((spec_clr >> 16) & 0xFF) / 255.0f;
        double green = ((spec_clr >> 8) & 0xFF) / 255.0f;
        double blue = (spec_clr & 0xFF) / 255.0f;
        Vec3d reflect = reflection(light, getPointNormal(n, bc_coords)).toNormal();
        double specular_coef = (spec_clr >> 16) & 0xFF;
        double sStrength = Math.pow(Math.max(0.0f, cam.getEye().grade(-1).toNormal().Dot(reflect)), specular_coef);
        Vec3d specul = new Vec3d(clr.getRed() * sStrength, clr.getGreen() * sStrength, clr.getBlue() * sStrength);
        return new Vec3d(specul.x, specul.y, specul.z);
    }

    private Matr4x4 fromTangentToWorld(Vec3d[] v, Vec3d n, Vec3d[] txt) {
        Vec3d edge1 = v[1].subtract(v[0]);
        Vec3d edge2 = v[2].subtract(v[0]);
        Vec3d deltauv1 = txt[1].subtract(txt[0]);
        Vec3d deltauv2 = txt[2].subtract(txt[0]);
        double f = 1.0f / (deltauv1.x * deltauv2.y - deltauv2.x * deltauv1.y);
        Vec3d tangent = new Vec3d(0, 0, 0);
        tangent.x = f * (deltauv2.y * edge1.x - deltauv1.y * edge2.x);
        tangent.y = f * (deltauv2.y * edge1.y - deltauv1.y * edge2.y);
        tangent.z = f * (deltauv2.y * edge1.z - deltauv1.y * edge2.z);
        tangent = tangent.toNormal();
        Vec3d bitangent = new Vec3d(0,0,0);
        bitangent.x = f * (-deltauv2.x * edge1.x + deltauv1.x * edge2.x);
        bitangent.y = f * (-deltauv2.x * edge1.y + deltauv1.x * edge2.y);
        bitangent.z = f * (-deltauv2.x * edge1.z + deltauv1.x * edge2.z);
        bitangent = bitangent.toNormal();
        return new Matr4x4(new double[][] {
                {tangent.x, tangent.y, tangent.z, 0},
                {bitangent.x, bitangent.y, bitangent.z, 0},
                {n.x, n.y, n.z, 0},
                {0, 0, 0, 1}
        });
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
