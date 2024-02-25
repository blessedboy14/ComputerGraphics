package com.example.akg_java.parser;

import com.example.akg_java.EngineUtility.Mesh;
import com.example.akg_java.math.Texture;
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
    private List<Texture> textures = new ArrayList<>();
    private List<Vec3d> normals = new ArrayList<>();
    private List<Triangle> tris = new ArrayList<>();
    private List<String> tags = new ArrayList<>();
    private String prev_tag = "";
    private String cur_tag = "";

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
                case "usemtl":
                {
                    if (parts[1].startsWith("Mat")) {
                        cur_tag = parts[1];
                        prev_tag = cur_tag;
                        tags.add(cur_tag);
                    }
                    break;
                }
                case "#":
                {
                    if (parts.length > 1 && parts[1].equals("object")) {
                        cur_tag = parts[2];
                        prev_tag = cur_tag;
                        tags.add(cur_tag);
                    }
                    break;
                }
                case "vt": {
                    Double[] coords = Arrays.stream(Arrays.copyOfRange(parts, 1, parts.length))
                            .map(Double::parseDouble)
                            .toArray(Double[]::new);
                    if (coords.length == 3) {
                        textures.add(new Texture(coords[0], coords[1], coords[2]));
                    } else {
                        textures.add(new Texture(coords[0], coords[1]));
                    }
                    break;
                }
                case "vn": {
                    Double[] coords = Arrays.stream(Arrays.copyOfRange(parts, 1, parts.length))
                            .map(Double::parseDouble)
                            .toArray(Double[]::new);
                    normals.add(new Vec3d(coords[0], coords[1], coords[2]));
                    break;
                }
                case "v": {
                        Double[] coords = Arrays.stream(Arrays.copyOfRange(parts, 1, parts.length))
                                .map(Double::parseDouble)
                                .toArray(Double[]::new);
                        if (coords.length > 3) {
                            vertexes_g.add(new Vec3d(coords[0], coords[1], coords[2], coords[3]));
                        } else {
                            vertexes_g.add(new Vec3d(coords[0], coords[1], coords[2]));
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
            Integer[] normals = Arrays.stream(data)
                    .map(vertex -> Integer.parseInt(vertex.split("//")[1]))
                    .toArray(Integer[]::new);
            tris.addAll(parseIntsToTriangle(g_vertexes, normals));
        } else if (data[0].contains("/") && data[0].split("/").length == 3) {
            Integer[] g_vertexes = Arrays.stream(data)
                    .map(vertex -> Integer.parseInt(vertex.split("/")[0]))
                    .toArray(Integer[]::new);
            Integer[] textures = Arrays.stream(data)
                    .map(vertex -> Integer.parseInt(vertex.split("/")[1]))
                    .toArray(Integer[]::new);
            Integer[] normals = Arrays.stream(data)
                    .map(vertex -> Integer.parseInt(vertex.split("/")[2]))
                    .toArray(Integer[]::new);
            tris.addAll(parseIntsToTriangle(g_vertexes, textures, normals));
/*            tris.addAll(parseIntsToTriangle(g_vertexes, normals));*/
        } else if (data[0].contains("/")) {
            Integer[] g_vertexes = Arrays.stream(data)
                    .map(vertex -> Integer.parseInt(vertex.split("/")[0]))
                    .toArray(Integer[]::new);
            Integer[] textures = Arrays.stream(data)
                    .map(vertex -> Integer.parseInt(vertex.split("/")[1]))
                    .toArray(Integer[]::new);
            tris.addAll(parseIntsToTriangle(g_vertexes, textures, null));
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

    private List<Triangle> parseIntsToTriangle(Integer[] vertices, Integer[] normalz) {
        if (vertices.length != normalz.length) {
            throw new RuntimeException("error with obj file, vertices num doesn't meet normals num");
        }
        List<Triangle> tris = new ArrayList<>();
        if (vertices.length > 3) {
            Vec3d main = vertexes_g.get(vertices[0]-1);
            Vec3d normal_main = normals.get(normalz[0] - 1);
            for (int i = 1; i < vertices.length - 1; i++) {
                tris.add(new Triangle(main, vertexes_g.get(vertices[i] - 1),
                        vertexes_g.get(vertices[i+1] - 1),
                        normal_main, normals.get(normalz[i] - 1), normals.get(normalz[i+1] - 1)));
            }
        } else {
            tris.add(new Triangle(vertexes_g.get(vertices[0] - 1), vertexes_g.get(vertices[1] - 1),
                    vertexes_g.get(vertices[2] - 1), normals.get(normalz[0] - 1),
                    normals.get(normalz[1] - 1), normals.get(normalz[2] - 1)));
        }
        for (Triangle tri: tris) {
            tri.setTag(cur_tag);
        }
        return tris;
    }

    private List<Triangle> parseIntsToTriangle(Integer[] vertices, Integer[] textures, Integer[] normalz){
        if (vertices.length != textures.length) {
            throw new RuntimeException("error with obj file, vertices num doesn't meet normals num");
        }
        List<Triangle> tris = new ArrayList<>();
        if (normalz == null) {
            normalz = new Integer[]{1, 1, 1};
        }
        if (vertices.length > 3) {
            Vec3d main = vertexes_g.get(vertices[0]-1);
            Vec3d normal_main = normals.get(normalz[0] - 1);
            Texture txt_main = this.textures.get(textures[0]-1);
            for (int i = 1; i < vertices.length - 1; i++) {
                tris.add(new Triangle(main, vertexes_g.get(vertices[i] - 1),
                        vertexes_g.get(vertices[i+1] - 1),
                        new Vec3d[]{normal_main,
                                normals.get(normalz[i] - 1),
                                normals.get(normalz[i+1] - 1)},
                        new Texture[]{txt_main,
                                this.textures.get(textures[i]-1),
                                this.textures.get(textures[i+1]-1)}));
            }
        } else {
            tris.add(new Triangle(vertexes_g.get(vertices[0] - 1), vertexes_g.get(vertices[1] - 1),
                    vertexes_g.get(vertices[2] - 1),new Vec3d[]{normals.get(normalz[0] - 1),
                    normals.get(normalz[1] - 1),
                    normals.get(normalz[2] - 1)},
                    new Texture[]{this.textures.get(textures[0] - 1),
                            this.textures.get(textures[1]-1),
                            this.textures.get(textures[2]-1)}));
        }
        for (Triangle tri: tris) {
            tri.setTag(cur_tag);
        }
        return tris;
    }

    public Mesh getMesh() {
        return new Mesh(this.tris);
    }
}
