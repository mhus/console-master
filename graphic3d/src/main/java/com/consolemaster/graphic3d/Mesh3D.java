package com.consolemaster.graphic3d;

import lombok.Data;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.ArrayList;

/**
 * Represents a 3D mesh object consisting of vertices and faces.
 */
@Data
public class Mesh3D {
    private final List<Point3D> vertices;
    private final List<Face3D> faces;
    private String name;

    public Mesh3D(String name) {
        this.name = name;
        this.vertices = new ArrayList<>();
        this.faces = new ArrayList<>();
    }

    /**
     * Adds a vertex to the mesh and returns its index.
     */
    public int addVertex(Point3D vertex) {
        vertices.add(vertex);
        return vertices.size() - 1;
    }

    /**
     * Adds a triangular face using vertex indices.
     */
    public void addTriangle(int v1, int v2, int v3) {
        faces.add(new Face3D(v1, v2, v3));
    }

    /**
     * Adds a quad face using vertex indices.
     */
    public void addQuad(int v1, int v2, int v3, int v4) {
        // Split quad into two triangles
        faces.add(new Face3D(v1, v2, v3));
        faces.add(new Face3D(v1, v3, v4));
    }

    /**
     * Transforms all vertices in the mesh using the given matrix.
     */
    public Mesh3D transform(Matrix4x4 transformation) {
        Mesh3D transformed = new Mesh3D(name + "_transformed");

        // Transform all vertices
        for (Point3D vertex : vertices) {
            transformed.addVertex(transformation.transform(vertex));
        }

        // Copy all faces
        transformed.faces.addAll(faces);

        return transformed;
    }

    /**
     * Creates a simple cube mesh.
     */
    public static Mesh3D createCube(double size) {
        Mesh3D cube = new Mesh3D("cube");
        double half = size / 2.0;

        // Add 8 vertices of a cube
        cube.addVertex(new Point3D(-half, -half, -half)); // 0
        cube.addVertex(new Point3D(half, -half, -half));  // 1
        cube.addVertex(new Point3D(half, half, -half));   // 2
        cube.addVertex(new Point3D(-half, half, -half));  // 3
        cube.addVertex(new Point3D(-half, -half, half));  // 4
        cube.addVertex(new Point3D(half, -half, half));   // 5
        cube.addVertex(new Point3D(half, half, half));    // 6
        cube.addVertex(new Point3D(-half, half, half));   // 7

        // Add 12 triangular faces (2 per cube face)
        // Front face
        cube.addTriangle(0, 1, 2);
        cube.addTriangle(0, 2, 3);

        // Back face
        cube.addTriangle(4, 6, 5);
        cube.addTriangle(4, 7, 6);

        // Left face
        cube.addTriangle(0, 3, 7);
        cube.addTriangle(0, 7, 4);

        // Right face
        cube.addTriangle(1, 5, 6);
        cube.addTriangle(1, 6, 2);

        // Top face
        cube.addTriangle(3, 2, 6);
        cube.addTriangle(3, 6, 7);

        // Bottom face
        cube.addTriangle(0, 4, 5);
        cube.addTriangle(0, 5, 1);

        return cube;
    }

    /**
     * Creates a simple pyramid mesh.
     */
    public static Mesh3D createPyramid(double size) {
        Mesh3D pyramid = new Mesh3D("pyramid");
        double half = size / 2.0;

        // Base vertices
        pyramid.addVertex(new Point3D(-half, -half, 0)); // 0
        pyramid.addVertex(new Point3D(half, -half, 0));  // 1
        pyramid.addVertex(new Point3D(half, half, 0));   // 2
        pyramid.addVertex(new Point3D(-half, half, 0));  // 3

        // Apex
        pyramid.addVertex(new Point3D(0, 0, size));      // 4

        // Base (2 triangles)
        pyramid.addTriangle(0, 2, 1);
        pyramid.addTriangle(0, 3, 2);

        // Side faces
        pyramid.addTriangle(0, 1, 4);
        pyramid.addTriangle(1, 2, 4);
        pyramid.addTriangle(2, 3, 4);
        pyramid.addTriangle(3, 0, 4);

        return pyramid;
    }

    /**
     * Represents a triangular face using vertex indices.
     */
    @Data
    @AllArgsConstructor
    public static class Face3D {
        private int v1, v2, v3;

        /**
         * Calculates the normal vector of this face.
         */
        public Point3D calculateNormal(List<Point3D> vertices) {
            Point3D p1 = vertices.get(v1);
            Point3D p2 = vertices.get(v2);
            Point3D p3 = vertices.get(v3);

            Point3D edge1 = p2.subtract(p1);
            Point3D edge2 = p3.subtract(p1);

            // Cross product
            double nx = edge1.getY() * edge2.getZ() - edge1.getZ() * edge2.getY();
            double ny = edge1.getZ() * edge2.getX() - edge1.getX() * edge2.getZ();
            double nz = edge1.getX() * edge2.getY() - edge1.getY() * edge2.getX();

            return new Point3D(nx, ny, nz).normalize();
        }
    }
}
