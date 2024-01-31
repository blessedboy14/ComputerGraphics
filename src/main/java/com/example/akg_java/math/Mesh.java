package com.example.akg_java.math;

import com.example.akg_java.parser.OBJParser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Mesh {
    private List<Vec3d> vertexes;
    private List<Face3d> faces;
    private List<Triangle> tris;

    public Mesh(List<Vec3d> vertexes, List<Face3d> faces) {
        this.vertexes = vertexes;
        this.faces = faces;
    }

    public Mesh(List<Vec3d> vertexes, ArrayList<Triangle> tris) {
        this.vertexes = vertexes;
        this.tris = tris;
    }

    public static Mesh loadMesh(String path) throws IOException {
        OBJParser parser = new OBJParser(path);
        parser.parseFile();
/*        return parser.getMesh();*/
        return parser.alternativeGet();
    }

    public List<Triangle> getTris() {
        return tris;
    }

    public void setTris(List<Triangle> tris) {
        this.tris = tris;
    }

    public List<Vec3d> getVertexes() {
        return vertexes;
    }

    public void setVertexes(List<Vec3d> vertexes) {
        this.vertexes = vertexes;
    }

    public List<Face3d> getFaces() {
        return faces;
    }

    public void setFaces(List<Face3d> faces) {
        this.faces = faces;
    }
}
