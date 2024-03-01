package com.example.akg_java.math;

public class Triangle {
    private Vec3d[] points = new Vec3d[3];
    private Vec3d[] normals = new Vec3d[3];

    private String tag;

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return this.tag;
    }

    private Texture[] textures = new Texture[3];
    public Triangle(Vec3d v1, Vec3d v2, Vec3d v3) {
        points[0] = v1;
        points[1] = v2;
        points[2] = v3;
    }

    public Triangle(Vec3d v1, Vec3d v2, Vec3d v3, Vec3d i, Vec3d j, Vec3d k) {
        points[0] = v1;
        points[1] = v2;
        points[2] = v3;
        normals[0] = i;
        normals[1] = j;
        normals[2] = k;
    }

    public Triangle(Vec3d v1, Vec3d v2, Vec3d v3, Vec3d[] normals, Texture[] textures) {
        points[0] = v1;
        points[1] = v2;
        points[2] = v3;
        this.normals = normals;
        this.textures = textures;
    }

    public Vec3d[] getNormals() {
        return normals;
    }

    public Vec3d[] getPoints() {
        return points;
    }

    public Texture[] getTextures() { return textures; }

    public Vec3d[] getTexturesAsVec() {
        return new Vec3d[] {
                new Vec3d(textures[0].u, textures[0].v, textures[0].w),
                new Vec3d(textures[1].u, textures[1].v, textures[1].w),
                new Vec3d(textures[2].u, textures[2].v, textures[2].w),
        };
    }

    public Triangle multiplyMatrix(Matr4x4 matrix) {
        Vec3d p0 = this.points[0].multiply(matrix);
        Vec3d p1 = this.points[1].multiply(matrix);
        Vec3d p2 = this.points[2].multiply(matrix);
        p0.weight();p1.weight();p2.weight();
        return new Triangle(p0, p1, p2,
                this.normals[0], this.normals[1], this.normals[2]);
    }

    public Triangle multiplyMatrix2(Matr4x4 matrix) {
        Vec3d p0 = this.points[0].multiply(matrix);
        Vec3d p1 = this.points[1].multiply(matrix);
        Vec3d p2 = this.points[2].multiply(matrix);
        p0.weight();p1.weight();p2.weight();
        return new Triangle(p0, p1, p2,
                new Vec3d[]{this.normals[0], this.normals[1], this.normals[2]},
                new Texture[]{
                        this.textures[0],
                        this.textures[1],
                        this.textures[2]
                }
        );
    }

    public Triangle saveMultiply(Matr4x4 matrix) {
        return new Triangle(this.points[0].saveMultiply(matrix), this.points[1].saveMultiply(matrix), this.points[2].saveMultiply(matrix));
    }

    public Triangle multiplyCamera(Matr4x4 matr4x4) {
        return new Triangle(this.points[0], this.points[1], this.points[2],
                this.normals[0].multiply(matr4x4).toNormal(),
                this.normals[1].multiply(matr4x4).toNormal(),
                this.normals[2].multiply(matr4x4).toNormal());
    }

    public void setPoints(Vec3d[] points) {
        this.points = points;
    }

    public Vec3d getNormal() {
        return this.points[2].subtract(this.points[0]).Cross(this.points[1].subtract(this.points[0])).toNormal();
    }
}
