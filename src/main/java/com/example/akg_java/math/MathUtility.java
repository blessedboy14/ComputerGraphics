package com.example.akg_java.math;

import com.example.akg_java.EngineUtility.Mesh;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class MathUtility {

    public static List<Triangle> generateSphereMesh(float radius, Vec3d center, int latDivisions, int longDivisions) {
        Vec3d base = new Vec3d(0, 0, radius);
        double latAngle = Math.PI / latDivisions;
        double longAngle = 2 * Math.PI / longDivisions;
        List<Vec3d> vertices = new ArrayList<>();
        for (int i = 1; i < latDivisions; i++) {
            Vec3d latBase = base.multiply(Matr4x4.rotationXRad(latAngle * i));
            for (int j = 0; j < longDivisions; j++) {
                Vec3d temp = latBase.multiply(Matr4x4.rotationZRad(longAngle * j))
                        .multiply(Matr4x4.translation(center.x, center.y, center.z));
                vertices.add(temp);
            }
        }

        int northPole = vertices.size();
        vertices.add(base.multiply(Matr4x4.translation(center.x, center.y, center.z)));
        int southPole = vertices.size();
        vertices.add(base.grade(-1).multiply(Matr4x4.translation(center.x, center.y, center.z)));

        final BiFunction<Integer, Integer, Integer> calcIdx = (latI, longI) -> (latI * longDivisions + longI);
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < latDivisions - 2; i++) {
            for (int j = 0; j < longDivisions - 1; j++) {
                indices.add(calcIdx.apply(i, j));
                indices.add( calcIdx.apply( i + 1,j ) );
                indices.add( calcIdx.apply( i,j + 1 ) );
                indices.add( calcIdx.apply( i,j + 1 ) );
                indices.add( calcIdx.apply( i + 1,j ) );
                indices.add( calcIdx.apply( i + 1,j + 1 ) );
            }
            indices.add( calcIdx.apply( i,longDivisions - 1 ) );
            indices.add( calcIdx.apply( i + 1,longDivisions - 1 ) );
            indices.add( calcIdx.apply( i,0 ) );
            indices.add( calcIdx.apply( i,0 ) );
            indices.add( calcIdx.apply( i + 1,longDivisions - 1 ) );
            indices.add( calcIdx.apply( i + 1,0 ) );
        }
        for (int i = 0; i < longDivisions - 1; i++) {
            indices.add(northPole);
            indices.add(calcIdx.apply(0, i));
            indices.add(calcIdx.apply(0, i + 1));

            indices.add( calcIdx.apply( latDivisions - 2,i + 1 ) );
            indices.add( calcIdx.apply( latDivisions - 2,i ) );
            indices.add(southPole);
        }
        indices.add( northPole );
        indices.add( calcIdx.apply( 0,longDivisions - 1 ) );
        indices.add( calcIdx.apply( 0,0 ) );
        indices.add( calcIdx.apply( latDivisions - 2,0 ) );
        indices.add( calcIdx.apply( latDivisions - 2,longDivisions - 1 ) );
        indices.add( southPole );
        return assembleFigures(vertices, indices);
    }

    private static List<Triangle> assembleFigures(List<Vec3d> vertices, List<Integer> indices) {
        List<Triangle> triangles = new ArrayList<>();
        for (int i = 0; i < indices.size(); i+= 3) {
            triangles.add(new Triangle(vertices.get(indices.get(i)),
                    vertices.get(indices.get(i + 1)),
                    vertices.get(indices.get(i + 2))));
        }
        return triangles;
    }

}
