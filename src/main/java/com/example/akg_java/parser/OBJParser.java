package com.example.akg_java.parser;

import com.example.akg_java.math.Face3d;
import com.example.akg_java.math.Mesh;
import com.example.akg_java.math.Triangle;
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
    private List<Triangle> tris = new ArrayList<>();

    public OBJParser(String fileName) {
        this.fileName = fileName;
    }

    public void parseFile() throws IOException {
        vertexes_g.clear();
        tris.clear();
        BufferedReader reader = new BufferedReader(new FileReader(this.fileName));
        while (reader.ready()) {
            String line = reader.readLine();
            String[] parts = line.split(" +");
            switch (parts[0]) {
                case "v": {
                    if (line.charAt(1) == ' ') {
                        Double[] coords = Arrays.stream(Arrays.copyOfRange(parts, 1, parts.length))
                                .map(Double::parseDouble)
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
            tris.addAll(parseIntsToTriangle(g_vertexes));
        } else if (data[0].contains("/") && data[0].split("/").length == 3) {
            Integer[] g_vertexes = Arrays.stream(data)
                    .map(vertex -> Integer.parseInt(vertex.split("/")[0]))
                    .toArray(Integer[]::new);
            tris.addAll(parseIntsToTriangle(g_vertexes));
        } else if (data[0].contains("/")) {
            Integer[] g_vertexes = Arrays.stream(data)
                    .map(vertex -> Integer.parseInt(vertex.split("/")[0]))
                    .toArray(Integer[]::new);
            tris.addAll(parseIntsToTriangle(g_vertexes));
        } else {
            Integer[] g_vertexes = Arrays.stream(data)
                    .map(Integer::parseInt)
                    .toArray(Integer[]::new);
            tris.addAll(parseIntsToTriangle(g_vertexes));
        }
    }

    private List<Triangle> parseIntsToTriangle(Integer[] ints) {
        List<Triangle> tris = new ArrayList<>();
        if (ints.length > 3) {
            Vec3d main = vertexes_g.get(ints[0]-1);
            for (int i = 1; i < ints.length - 1; i++) {
                tris.add(new Triangle(main, vertexes_g.get(ints[i] - 1),
                        vertexes_g.get(ints[i+1] - 1)));
            }
        } else {
            tris.add(new Triangle(vertexes_g.get(ints[0] - 1), vertexes_g.get(ints[1] - 1),
                    vertexes_g.get(ints[2] - 1)));
        }
        return tris;
    }

    public Mesh alternativeGet() {
        return new Mesh(this.vertexes_g, (ArrayList<Triangle>) this.tris);
    }
}
