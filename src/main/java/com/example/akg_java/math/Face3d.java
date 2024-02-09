package com.example.akg_java.math;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Face3d {

    private Integer[] gVertexes;
    private Integer[] tVertexes;
    private Integer[] nVectors;

    public Face3d(Integer[] gVertexes) {
        this.gVertexes = gVertexes;
    }

}
