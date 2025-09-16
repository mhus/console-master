package com.consolemaster.graphic3d;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Mesh3D class.
 */
class Mesh3DTest {

    private Mesh3D mesh;

    @BeforeEach
    void setUp() {
        mesh = new Mesh3D("test_mesh");
    }

    @Test
    void testConstructor() {
        assertEquals("test_mesh", mesh.getName());
        assertTrue(mesh.getVertices().isEmpty());
        assertTrue(mesh.getFaces().isEmpty());
    }

    @Test
    void testAddVertex() {
        Point3D vertex = new Point3D(1.0, 2.0, 3.0);
        int index = mesh.addVertex(vertex);

        assertEquals(0, index);
        assertEquals(1, mesh.getVertices().size());
        assertEquals(vertex, mesh.getVertices().get(0));
    }

    @Test
    void testAddTriangle() {
        // Add vertices first
        mesh.addVertex(new Point3D(0, 0, 0));
        mesh.addVertex(new Point3D(1, 0, 0));
        mesh.addVertex(new Point3D(0, 1, 0));

        mesh.addTriangle(0, 1, 2);

        assertEquals(1, mesh.getFaces().size());
        Mesh3D.Face3D face = mesh.getFaces().get(0);
        assertEquals(0, face.getV1());
        assertEquals(1, face.getV2());
        assertEquals(2, face.getV3());
    }

    @Test
    void testAddQuad() {
        // Add vertices for a quad
        mesh.addVertex(new Point3D(0, 0, 0));
        mesh.addVertex(new Point3D(1, 0, 0));
        mesh.addVertex(new Point3D(1, 1, 0));
        mesh.addVertex(new Point3D(0, 1, 0));

        mesh.addQuad(0, 1, 2, 3);

        // Quad should be split into 2 triangles
        assertEquals(2, mesh.getFaces().size());
    }

    @Test
    void testCreateCube() {
        Mesh3D cube = Mesh3D.createCube(2.0);

        assertEquals("cube", cube.getName());
        assertEquals(8, cube.getVertices().size()); // 8 vertices for a cube
        assertEquals(12, cube.getFaces().size());   // 12 triangular faces (2 per cube face)

        // Check that vertices are at expected positions
        Point3D vertex0 = cube.getVertices().get(0);
        assertEquals(-1.0, vertex0.getX());
        assertEquals(-1.0, vertex0.getY());
        assertEquals(-1.0, vertex0.getZ());
    }

    @Test
    void testCreatePyramid() {
        Mesh3D pyramid = Mesh3D.createPyramid(2.0);

        assertEquals("pyramid", pyramid.getName());
        assertEquals(5, pyramid.getVertices().size()); // 4 base + 1 apex
        assertEquals(6, pyramid.getFaces().size());    // 2 base triangles + 4 side faces
    }

    @Test
    void testTransform() {
        // Create a simple mesh
        mesh.addVertex(new Point3D(1, 0, 0));
        mesh.addTriangle(0, 0, 0); // Simple degenerate triangle for testing

        // Apply translation
        Matrix4x4 translation = Matrix4x4.translation(2, 3, 4);
        Mesh3D transformed = mesh.transform(translation);

        assertEquals("test_mesh_transformed", transformed.getName());
        Point3D transformedVertex = transformed.getVertices().get(0);
        assertEquals(3.0, transformedVertex.getX());
        assertEquals(3.0, transformedVertex.getY());
        assertEquals(4.0, transformedVertex.getZ());
    }

    @Test
    void testFaceNormalCalculation() {
        // Create a triangle in XY plane
        mesh.addVertex(new Point3D(0, 0, 0));
        mesh.addVertex(new Point3D(1, 0, 0));
        mesh.addVertex(new Point3D(0, 1, 0));

        Mesh3D.Face3D face = new Mesh3D.Face3D(0, 1, 2);
        Point3D normal = face.calculateNormal(mesh.getVertices());

        // Normal should point in +Z direction
        assertEquals(0.0, normal.getX(), 0.001);
        assertEquals(0.0, normal.getY(), 0.001);
        assertEquals(1.0, normal.getZ(), 0.001);
    }
}
