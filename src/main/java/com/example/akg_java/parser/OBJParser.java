package com.example.akg_java.parser;

import com.example.akg_java.math.Face3d;
import com.example.akg_java.math.Mesh;
import com.example.akg_java.math.Vec3d;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OBJParser {
    private String fileName;
    private List<Vec3d> vertexes_g = new ArrayList<>();
    private List<Face3d> faces = new ArrayList<>();

    public OBJParser(String fileName) {
        this.fileName = fileName;
    }

    public void parseFile() throws IOException {
        vertexes_g.clear();
        faces.clear();
        BufferedReader reader = new BufferedReader(new FileReader(this.fileName));
        while (reader.ready()) {
            String line = reader.readLine();
            String[] parts = line.split(" +");
            switch (parts[0]) {
                case "v": {
                    if (line.charAt(1) == ' ') {
                        Double[] coords = Arrays.stream(Arrays.copyOfRange(parts, 1, parts.length))
                                .map(coord -> Double.parseDouble(coord))
                                .toArray(Double[]::new);
                        if (coords.length > 3) {
                            vertexes_g.add(new Vec3d(coords[0], coords[1], coords[2], coords[3]));
                        } else {
                            vertexes_g.add(new Vec3d(coords[0], coords[1], coords[2]));
                        }
                    }
                    break;
                }
                case "f": {
                    parseFace(Arrays.copyOfRange(parts, 1, parts.length));
                    break;
                }
            }
        }
    }

    private void parseFace(String[] data) {
        if (data[0].contains("//")) {
            Integer[] g_vertexes = Arrays.stream(data)
                    .map(vertex -> Integer.parseInt(vertex.split("//")[0]))
                    .toArray(Integer[]::new);
            faces.add(new Face3d(g_vertexes));
        } else if (data[0].contains("/") && data[0].split("/").length == 3) {
            Integer[] g_vertexes = Arrays.stream(data)
                    .map(vertex -> Integer.parseInt(vertex.split("/")[0]))
                    .toArray(Integer[]::new);
            faces.add(new Face3d(g_vertexes));
        } else if (data[0].contains("/")) {
            Integer[] g_vertexes = Arrays.stream(data)
                    .map(vertex -> Integer.parseInt(vertex.split("/")[0]))
                    .toArray(Integer[]::new);
            faces.add(new Face3d(g_vertexes));
        } else {
            Integer[] g_vertexes = Arrays.stream(data)
                    .map(Integer::parseInt)
                    .toArray(Integer[]::new);
            faces.add(new Face3d(g_vertexes));
        }
    }

    public Mesh getMesh() {
        return new Mesh(this.vertexes_g, this.faces);
    }
}
