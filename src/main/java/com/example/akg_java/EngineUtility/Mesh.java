package com.example.akg_java.EngineUtility;

import com.example.akg_java.math.Face3d;
import com.example.akg_java.math.Triangle;
import com.example.akg_java.math.Vec3d;
import com.example.akg_java.parser.OBJParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Mesh {
    private List<Vec3d> vertexes;
    private List<Triangle> tris;

    public Mesh(List<Vec3d> vertexes, ArrayList<Triangle> tris) {
        this.vertexes = vertexes;
        this.tris = tris;
    }

    public static Mesh loadMesh(String path) throws IOException {
        OBJParser parser = new OBJParser(path);
        parser.parseFile();
        return parser.getMesh();
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

}
