package com.example.akg_java.parser;

<<<<<<< HEAD
import com.example.akg_java.math.Mesh;
=======
import com.example.akg_java.EngineUtility.Mesh;
>>>>>>> master
import com.example.akg_java.math.Triangle;
import com.example.akg_java.math.Vec3d;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OBJParser {

    private final String fileName;
    private final List<Vec3d> vertexes_g = new ArrayList<>();
    private final List<Triangle> tris = new ArrayList<>();

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
                case "v" -> {
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
                }
                case "f" -> {
                    parseFace(Arrays.copyOfRange(parts, 1, parts.length));
                }
            }
        }
    }

    private void parseFace(String[] data) {
        Integer[] gVertexes;
        if (data[0].contains("//")) {
            gVertexes = Arrays.stream(data)
                    .map(vertex -> Integer.parseInt(vertex.split("//")[0]))
                    .toArray(Integer[]::new);
        } else if (data[0].contains("/")) {
            gVertexes = Arrays.stream(data)
                    .map(vertex -> Integer.parseInt(vertex.split("/")[0]))
                    .toArray(Integer[]::new);
        } else {
            gVertexes = Arrays.stream(data)
                    .map(Integer::parseInt)
                    .toArray(Integer[]::new);

        }
        tris.addAll(parseIntsToTriangle(gVertexes));
    }

    private List<Triangle> parseIntsToTriangle(Integer[] ints) {
        List<Triangle> tris = new ArrayList<>();
        if (ints.length > 3) {
            Vec3d main = vertexes_g.get(ints[0] - 1);
            for (int i = 1; i < ints.length - 1; i++) {
                tris.add(new Triangle(main, vertexes_g.get(ints[i] - 1),
                        vertexes_g.get(ints[i + 1] - 1)));
            }
        } else {
            tris.add(new Triangle(vertexes_g.get(ints[0] - 1), vertexes_g.get(ints[1] - 1),
                    vertexes_g.get(ints[2] - 1)));
        }
        return tris;
    }

    public Mesh getMesh() {
        return new Mesh(this.vertexes_g, (ArrayList<Triangle>) this.tris);
    }
}
