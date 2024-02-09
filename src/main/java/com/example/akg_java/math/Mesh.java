package com.example.akg_java.math;

import com.example.akg_java.parser.OBJParser;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
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
}
