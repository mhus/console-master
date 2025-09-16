package com.consolemaster.graphic3d;

import com.consolemaster.AnsiColor;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;

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
    void testAddVertexWithBigDecimal() {
        Point3D vertex = new Point3D(
            BigDecimal.valueOf(1.123456789),
            BigDecimal.valueOf(2.234567890),
            BigDecimal.valueOf(3.345678901)
        );
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

        // Add triangle
        mesh.addTriangle(0, 1, 2);

        assertEquals(1, mesh.getFaces().size());
        Mesh3D.Face3D face = mesh.getFaces().get(0);
        assertEquals(0, face.getV1());
        assertEquals(1, face.getV2());
        assertEquals(2, face.getV3());
        assertEquals(AnsiColor.WHITE, face.getColor());
        assertNull(face.getTexture());
    }

    @Test
    void testAddTriangleWithColor() {
        // Add vertices
        mesh.addVertex(new Point3D(0, 0, 0));
        mesh.addVertex(new Point3D(1, 0, 0));
        mesh.addVertex(new Point3D(0, 1, 0));

        // Add colored triangle
        mesh.addTriangle(0, 1, 2, AnsiColor.RED);

        assertEquals(1, mesh.getFaces().size());
        Mesh3D.Face3D face = mesh.getFaces().get(0);
        assertEquals(0, face.getV1());
        assertEquals(1, face.getV2());
        assertEquals(2, face.getV3());
        assertEquals(AnsiColor.RED, face.getColor());
        assertNull(face.getTexture());
    }

    @Test
    void testAddTriangleWithTexture() {
        // Add vertices
        mesh.addVertex(new Point3D(0, 0, 0));
        mesh.addVertex(new Point3D(1, 0, 0));
        mesh.addVertex(new Point3D(0, 1, 0));

        // Create texture and add textured triangle
        Texture3D texture = Texture3D.solid(AnsiColor.BLUE);
        mesh.addTriangle(0, 1, 2, texture);

        assertEquals(1, mesh.getFaces().size());
        Mesh3D.Face3D face = mesh.getFaces().get(0);
        assertEquals(0, face.getV1());
        assertEquals(1, face.getV2());
        assertEquals(2, face.getV3());
        assertEquals(AnsiColor.WHITE, face.getColor());
        assertEquals(texture, face.getTexture());
    }

    @Test
    void testAddTriangleWithColorAndTexture() {
        // Add vertices
        mesh.addVertex(new Point3D(0, 0, 0));
        mesh.addVertex(new Point3D(1, 0, 0));
        mesh.addVertex(new Point3D(0, 1, 0));

        // Create texture and add triangle with both color and texture
        Texture3D texture = Texture3D.metallic(AnsiColor.YELLOW);
        mesh.addTriangle(0, 1, 2, AnsiColor.GREEN, texture);

        assertEquals(1, mesh.getFaces().size());
        Mesh3D.Face3D face = mesh.getFaces().get(0);
        assertEquals(0, face.getV1());
        assertEquals(1, face.getV2());
        assertEquals(2, face.getV3());
        assertEquals(AnsiColor.GREEN, face.getColor());
        assertEquals(texture, face.getTexture());
    }

    @Test
    void testAddQuad() {
        // Add vertices for a quad
        mesh.addVertex(new Point3D(0, 0, 0));
        mesh.addVertex(new Point3D(1, 0, 0));
        mesh.addVertex(new Point3D(1, 1, 0));
        mesh.addVertex(new Point3D(0, 1, 0));

        // Add quad (should create 2 triangles)
        mesh.addQuad(0, 1, 2, 3);

        assertEquals(2, mesh.getFaces().size());

        // First triangle: 0, 1, 2
        Mesh3D.Face3D face1 = mesh.getFaces().get(0);
        assertEquals(0, face1.getV1());
        assertEquals(1, face1.getV2());
        assertEquals(2, face1.getV3());

        // Second triangle: 0, 2, 3
        Mesh3D.Face3D face2 = mesh.getFaces().get(1);
        assertEquals(0, face2.getV1());
        assertEquals(2, face2.getV2());
        assertEquals(3, face2.getV3());
    }

    @Test
    void testTransform() {
        // Create a simple mesh
        mesh.addVertex(new Point3D(1, 0, 0));
        mesh.addVertex(new Point3D(0, 1, 0));
        mesh.addVertex(new Point3D(0, 0, 1));
        mesh.addTriangle(0, 1, 2);

        // Create a translation matrix
        Matrix4x4 translation = Matrix4x4.translation(1, 2, 3);

        // Transform the mesh
        Mesh3D transformedMesh = mesh.transform(translation);

        // Verify transformation
        assertNotEquals(mesh, transformedMesh);
        assertEquals("test_mesh_transformed", transformedMesh.getName());
        assertEquals(3, transformedMesh.getVertices().size());
        assertEquals(1, transformedMesh.getFaces().size());

        // Check if vertices were translated
        Point3D vertex0 = transformedMesh.getVertices().get(0);
        assertEquals(2.0, vertex0.getXAsDouble(), 0.001); // 1 + 1
        assertEquals(2.0, vertex0.getYAsDouble(), 0.001); // 0 + 2
        assertEquals(3.0, vertex0.getZAsDouble(), 0.001); // 0 + 3
    }

    @Test
    void testCreateCubeDouble() {
        Mesh3D cube = Mesh3D.createCube(2.0);

        assertEquals("cube", cube.getName());
        assertEquals(8, cube.getVertices().size()); // Cube has 8 vertices
        assertEquals(12, cube.getFaces().size());   // Cube has 12 triangular faces (2 per face)

        // Check if vertices are at expected positions (cube with size 2.0)
        boolean foundVertex = false;
        for (Point3D vertex : cube.getVertices()) {
            double x = vertex.getXAsDouble();
            double y = vertex.getYAsDouble();
            double z = vertex.getZAsDouble();

            // All coordinates should be either -1.0 or 1.0 (half of size 2.0)
            assertTrue(Math.abs(Math.abs(x) - 1.0) < 0.001);
            assertTrue(Math.abs(Math.abs(y) - 1.0) < 0.001);
            assertTrue(Math.abs(Math.abs(z) - 1.0) < 0.001);

            if (Math.abs(x + 1.0) < 0.001 && Math.abs(y + 1.0) < 0.001 && Math.abs(z + 1.0) < 0.001) {
                foundVertex = true;
            }
        }
        assertTrue(foundVertex, "Expected vertex (-1, -1, -1) not found");
    }

    @Test
    @Disabled
    void testCreateCubeBigDecimal() {
        BigDecimal size = BigDecimal.valueOf(3.123456789);
        Mesh3D cube = Mesh3D.createCube(size);

        assertEquals("cube", cube.getName());
        assertEquals(8, cube.getVertices().size());
        assertEquals(12, cube.getFaces().size());

        // Check precision - vertices should be at ±(size/2)
        BigDecimal expectedHalf = size.divide(BigDecimal.valueOf(2), BigDecimal.ROUND_HALF_UP);
        boolean foundPreciseVertex = false;

        for (Point3D vertex : cube.getVertices()) {
            BigDecimal x = vertex.getX();
            BigDecimal y = vertex.getY();
            BigDecimal z = vertex.getZ();

            // Check if this is the (-half, -half, -half) vertex
            if (x.negate().equals(expectedHalf) &&
                y.negate().equals(expectedHalf) &&
                z.negate().equals(expectedHalf)) {
                foundPreciseVertex = true;
            }
        }
        assertTrue(foundPreciseVertex, "Expected precise vertex not found");
    }

    @Test
    void testCreatePyramidDouble() {
        Mesh3D pyramid = Mesh3D.createPyramid(2.0);

        assertEquals("pyramid", pyramid.getName());
        assertEquals(5, pyramid.getVertices().size()); // 4 base vertices + 1 apex
        assertEquals(6, pyramid.getFaces().size());    // 2 base triangles + 4 side triangles

        // Check apex vertex (should be at (0, 0, size))
        boolean foundApex = false;
        for (Point3D vertex : pyramid.getVertices()) {
            if (Math.abs(vertex.getXAsDouble()) < 0.001 &&
                Math.abs(vertex.getYAsDouble()) < 0.001 &&
                Math.abs(vertex.getZAsDouble() - 2.0) < 0.001) {
                foundApex = true;
                break;
            }
        }
        assertTrue(foundApex, "Pyramid apex not found at expected position");
    }

    @Test
    void testCreatePyramidBigDecimal() {
        BigDecimal size = BigDecimal.valueOf(1.987654321);
        Mesh3D pyramid = Mesh3D.createPyramid(size);

        assertEquals("pyramid", pyramid.getName());
        assertEquals(5, pyramid.getVertices().size());
        assertEquals(6, pyramid.getFaces().size());

        // Check if apex is at precise position
        boolean foundPreciseApex = false;
        for (Point3D vertex : pyramid.getVertices()) {
            if (vertex.getX().equals(BigDecimal.ZERO) &&
                vertex.getY().equals(BigDecimal.ZERO) &&
                vertex.getZ().equals(size)) {
                foundPreciseApex = true;
                break;
            }
        }
        assertTrue(foundPreciseApex, "Pyramid apex not found at precise position");
    }

    @Test
    void testCreateColorfulCube() {
        Mesh3D colorfulCube = Mesh3D.createColorfulCube(1.5);

        assertEquals("colorful_cube", colorfulCube.getName());
        assertEquals(8, colorfulCube.getVertices().size());
        assertEquals(12, colorfulCube.getFaces().size());

        // Check if faces have different colors
        boolean foundRedFace = false;
        boolean foundGreenFace = false;
        boolean foundBlueFace = false;

        for (Mesh3D.Face3D face : colorfulCube.getFaces()) {
            if (face.getColor() == AnsiColor.RED) foundRedFace = true;
            if (face.getColor() == AnsiColor.GREEN) foundGreenFace = true;
            if (face.getColor() == AnsiColor.BLUE) foundBlueFace = true;
        }

        assertTrue(foundRedFace, "Red face not found");
        assertTrue(foundGreenFace, "Green face not found");
        assertTrue(foundBlueFace, "Blue face not found");
    }

    @Test
    void testCreateTexturedCube() {
        Mesh3D texturedCube = Mesh3D.createTexturedCube(1.0);

        assertEquals("textured_cube", texturedCube.getName());
        assertEquals(8, texturedCube.getVertices().size());
        assertEquals(12, texturedCube.getFaces().size());

        // Check if faces have textures
        boolean foundTexturedFace = false;
        for (Mesh3D.Face3D face : texturedCube.getFaces()) {
            if (face.hasTexture()) {
                foundTexturedFace = true;
                break;
            }
        }
        assertTrue(foundTexturedFace, "No textured face found");
    }

    @Test
    void testCreateColorfulPyramid() {
        Mesh3D colorfulPyramid = Mesh3D.createColorfulPyramid(2.5);

        assertEquals("colorful_pyramid", colorfulPyramid.getName());
        assertEquals(5, colorfulPyramid.getVertices().size());
        assertEquals(6, colorfulPyramid.getFaces().size());

        // Check if pyramid has colored faces
        boolean foundColoredFace = false;
        for (Mesh3D.Face3D face : colorfulPyramid.getFaces()) {
            if (face.getColor() != AnsiColor.WHITE && face.getColor() != AnsiColor.BLACK) {
                foundColoredFace = true;
                break;
            }
        }
        assertTrue(foundColoredFace, "No colored face found in pyramid");
    }

    @Test
    void testFace3DConstructors() {
        // Test default constructor
        Mesh3D.Face3D face1 = new Mesh3D.Face3D(0, 1, 2);
        assertEquals(0, face1.getV1());
        assertEquals(1, face1.getV2());
        assertEquals(2, face1.getV3());
        assertEquals(AnsiColor.WHITE, face1.getColor());
        assertNull(face1.getTexture());

        // Test constructor with color
        Mesh3D.Face3D face2 = new Mesh3D.Face3D(0, 1, 2, AnsiColor.RED);
        assertEquals(AnsiColor.RED, face2.getColor());
        assertNull(face2.getTexture());

        // Test full constructor
        Texture3D texture = Texture3D.solid(AnsiColor.BLUE);
        Mesh3D.Face3D face3 = new Mesh3D.Face3D(0, 1, 2, AnsiColor.GREEN, texture);
        assertEquals(AnsiColor.GREEN, face3.getColor());
        assertEquals(texture, face3.getTexture());
        assertTrue(face3.hasTexture());
    }

    @Test
    void testFace3DEffectiveColorAndCharacter() {
        Texture3D texture = Texture3D.solid(AnsiColor.CYAN);
        Mesh3D.Face3D texturedFace = new Mesh3D.Face3D(0, 1, 2, AnsiColor.RED, texture);
        Mesh3D.Face3D plainFace = new Mesh3D.Face3D(0, 1, 2, AnsiColor.YELLOW);

        // Test effective color - textured face should use texture color
        AnsiColor texturedColor = texturedFace.getEffectiveColor(0.5, 0.5, 0.8);
        AnsiColor plainColor = plainFace.getEffectiveColor(0.5, 0.5, 0.8);

        assertNotNull(texturedColor);
        assertEquals(AnsiColor.YELLOW, plainColor);

        // Test effective character
        char texturedChar = texturedFace.getEffectiveCharacter(0.5, 0.5, 0.8);
        char plainChar = plainFace.getEffectiveCharacter(0.5, 0.5, 0.8);

        assertTrue(texturedChar != ' '); // Texture should provide a character
        assertTrue(plainChar != ' ');    // High light intensity should provide a character
    }

    @Test
    void testMultipleVerticesAndFaces() {
        // Add multiple vertices
        for (int i = 0; i < 10; i++) {
            Point3D vertex = new Point3D(i, i * 2, i * 3);
            int index = mesh.addVertex(vertex);
            assertEquals(i, index);
        }

        assertEquals(10, mesh.getVertices().size());

        // Add multiple triangles
        mesh.addTriangle(0, 1, 2);
        mesh.addTriangle(3, 4, 5);
        mesh.addTriangle(6, 7, 8);

        assertEquals(3, mesh.getFaces().size());
    }
}
