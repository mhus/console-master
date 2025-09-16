package com.consolemaster.graphic3d;

import com.consolemaster.AnsiColor;
import lombok.Data;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import java.util.List;
import java.util.ArrayList;

/**
 * Represents a 3D mesh object consisting of vertices and faces using BigDecimal for precision.
 */
@Data
public class Mesh3D {
    private static final MathContext MATH_CONTEXT = new MathContext(34, RoundingMode.HALF_UP);
    private static final BigDecimal TWO = BigDecimal.valueOf(2);

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
     * Adds a triangular face with color.
     */
    public void addTriangle(int v1, int v2, int v3, AnsiColor color) {
        faces.add(new Face3D(v1, v2, v3, color));
    }

    /**
     * Adds a triangular face with texture.
     */
    public void addTriangle(int v1, int v2, int v3, Texture3D texture) {
        faces.add(new Face3D(v1, v2, v3, AnsiColor.WHITE, texture));
    }

    /**
     * Adds a triangular face with color and texture.
     */
    public void addTriangle(int v1, int v2, int v3, AnsiColor color, Texture3D texture) {
        faces.add(new Face3D(v1, v2, v3, color, texture));
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
    public static Mesh3D createCube(BigDecimal size) {
        Mesh3D cube = new Mesh3D("cube");
        BigDecimal half = size.divide(TWO, MATH_CONTEXT);

        // Add 8 vertices of a cube
        cube.addVertex(new Point3D(half.negate(), half.negate(), half.negate())); // 0
        cube.addVertex(new Point3D(half, half.negate(), half.negate()));  // 1
        cube.addVertex(new Point3D(half, half, half.negate()));   // 2
        cube.addVertex(new Point3D(half.negate(), half, half.negate()));  // 3
        cube.addVertex(new Point3D(half.negate(), half.negate(), half));  // 4
        cube.addVertex(new Point3D(half, half.negate(), half));   // 5
        cube.addVertex(new Point3D(half, half, half));    // 6
        cube.addVertex(new Point3D(half.negate(), half, half));   // 7

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
     * Creates a simple cube mesh (double for compatibility).
     */
    public static Mesh3D createCube(double size) {
        return createCube(BigDecimal.valueOf(size));
    }

    /**
     * Creates a simple pyramid mesh.
     */
    public static Mesh3D createPyramid(BigDecimal size) {
        Mesh3D pyramid = new Mesh3D("pyramid");
        BigDecimal half = size.divide(TWO, MATH_CONTEXT);

        // Base vertices
        pyramid.addVertex(new Point3D(half.negate(), half.negate(), BigDecimal.ZERO)); // 0
        pyramid.addVertex(new Point3D(half, half.negate(), BigDecimal.ZERO));  // 1
        pyramid.addVertex(new Point3D(half, half, BigDecimal.ZERO));   // 2
        pyramid.addVertex(new Point3D(half.negate(), half, BigDecimal.ZERO));  // 3

        // Apex
        pyramid.addVertex(new Point3D(BigDecimal.ZERO, BigDecimal.ZERO, size));      // 4

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
     * Creates a simple pyramid mesh (double for compatibility).
     */
    public static Mesh3D createPyramid(double size) {
        return createPyramid(BigDecimal.valueOf(size));
    }

    /**
     * Creates a colorful cube mesh with different colors for each face.
     */
    public static Mesh3D createColorfulCube(BigDecimal size) {
        Mesh3D cube = new Mesh3D("colorful_cube");
        BigDecimal half = size.divide(TWO, MATH_CONTEXT);

        // Add 8 vertices of a cube
        cube.addVertex(new Point3D(half.negate(), half.negate(), half.negate())); // 0
        cube.addVertex(new Point3D(half, half.negate(), half.negate()));  // 1
        cube.addVertex(new Point3D(half, half, half.negate()));   // 2
        cube.addVertex(new Point3D(half.negate(), half, half.negate()));  // 3
        cube.addVertex(new Point3D(half.negate(), half.negate(), half));  // 4
        cube.addVertex(new Point3D(half, half.negate(), half));   // 5
        cube.addVertex(new Point3D(half, half, half));    // 6
        cube.addVertex(new Point3D(half.negate(), half, half));   // 7

        // Add 12 triangular faces with different colors
        // Front face - Red
        cube.addTriangle(0, 1, 2, AnsiColor.RED);
        cube.addTriangle(0, 2, 3, AnsiColor.RED);

        // Back face - Green
        cube.addTriangle(4, 6, 5, AnsiColor.GREEN);
        cube.addTriangle(4, 7, 6, AnsiColor.GREEN);

        // Left face - Blue
        cube.addTriangle(0, 3, 7, AnsiColor.BLUE);
        cube.addTriangle(0, 7, 4, AnsiColor.BLUE);

        // Right face - Yellow
        cube.addTriangle(1, 5, 6, AnsiColor.YELLOW);
        cube.addTriangle(1, 6, 2, AnsiColor.YELLOW);

        // Top face - Magenta
        cube.addTriangle(3, 2, 6, AnsiColor.MAGENTA);
        cube.addTriangle(3, 6, 7, AnsiColor.MAGENTA);

        // Bottom face - Cyan
        cube.addTriangle(0, 4, 5, AnsiColor.CYAN);
        cube.addTriangle(0, 5, 1, AnsiColor.CYAN);

        return cube;
    }

    /**
     * Creates a colorful cube mesh with different colors for each face (double for compatibility).
     */
    public static Mesh3D createColorfulCube(double size) {
        return createColorfulCube(BigDecimal.valueOf(size));
    }

    /**
     * Creates a textured cube mesh with different textures for each face.
     */
    public static Mesh3D createTexturedCube(BigDecimal size) {
        Mesh3D cube = new Mesh3D("textured_cube");
        BigDecimal half = size.divide(TWO, MATH_CONTEXT);

        // Add 8 vertices of a cube
        cube.addVertex(new Point3D(half.negate(), half.negate(), half.negate())); // 0
        cube.addVertex(new Point3D(half, half.negate(), half.negate()));  // 1
        cube.addVertex(new Point3D(half, half, half.negate()));   // 2
        cube.addVertex(new Point3D(half.negate(), half, half.negate()));  // 3
        cube.addVertex(new Point3D(half.negate(), half.negate(), half));  // 4
        cube.addVertex(new Point3D(half, half.negate(), half));   // 5
        cube.addVertex(new Point3D(half, half, half));    // 6
        cube.addVertex(new Point3D(half.negate(), half, half));   // 7

        // Create different textures for each face
        Texture3D metalTexture = Texture3D.metallic(AnsiColor.WHITE);
        Texture3D woodTexture = Texture3D.wood();
        Texture3D stoneTexture = Texture3D.stone();
        Texture3D fabricTexture = Texture3D.fabric(AnsiColor.BLUE);
        Texture3D glassTexture = Texture3D.glass(AnsiColor.CYAN);
        Texture3D solidTexture = Texture3D.solid(AnsiColor.RED);

        // Add 12 triangular faces with different textures
        // Front face - Metal
        cube.addTriangle(0, 1, 2, metalTexture);
        cube.addTriangle(0, 2, 3, metalTexture);

        // Back face - Wood
        cube.addTriangle(4, 6, 5, woodTexture);
        cube.addTriangle(4, 7, 6, woodTexture);

        // Left face - Stone
        cube.addTriangle(0, 3, 7, stoneTexture);
        cube.addTriangle(0, 7, 4, stoneTexture);

        // Right face - Fabric
        cube.addTriangle(1, 5, 6, fabricTexture);
        cube.addTriangle(1, 6, 2, fabricTexture);

        // Top face - Glass
        cube.addTriangle(3, 2, 6, glassTexture);
        cube.addTriangle(3, 6, 7, glassTexture);

        // Bottom face - Solid
        cube.addTriangle(0, 4, 5, solidTexture);
        cube.addTriangle(0, 5, 1, solidTexture);

        return cube;
    }

    /**
     * Creates a textured cube mesh with different textures for each face (double for compatibility).
     */
    public static Mesh3D createTexturedCube(double size) {
        return createTexturedCube(BigDecimal.valueOf(size));
    }

    /**
     * Creates a pyramid with gradient colors.
     */
    public static Mesh3D createColorfulPyramid(BigDecimal size) {
        Mesh3D pyramid = new Mesh3D("colorful_pyramid");
        BigDecimal half = size.divide(TWO, MATH_CONTEXT);

        // Base vertices
        pyramid.addVertex(new Point3D(half.negate(), half.negate(), BigDecimal.ZERO)); // 0
        pyramid.addVertex(new Point3D(half, half.negate(), BigDecimal.ZERO));  // 1
        pyramid.addVertex(new Point3D(half, half, BigDecimal.ZERO));   // 2
        pyramid.addVertex(new Point3D(half.negate(), half, BigDecimal.ZERO));  // 3

        // Apex
        pyramid.addVertex(new Point3D(BigDecimal.ZERO, BigDecimal.ZERO, size));      // 4

        // Base (2 triangles) - Dark gray
        pyramid.addTriangle(0, 2, 1, AnsiColor.BLACK);
        pyramid.addTriangle(0, 3, 2, AnsiColor.BLACK);

        // Side faces with different colors
        pyramid.addTriangle(0, 1, 4, AnsiColor.RED);
        pyramid.addTriangle(1, 2, 4, AnsiColor.GREEN);
        pyramid.addTriangle(2, 3, 4, AnsiColor.BLUE);
        pyramid.addTriangle(3, 0, 4, AnsiColor.YELLOW);

        return pyramid;
    }

    /**
     * Creates a pyramid with gradient colors (double for compatibility).
     */
    public static Mesh3D createColorfulPyramid(double size) {
        return createColorfulPyramid(BigDecimal.valueOf(size));
    }

    /**
     * Represents a triangular face using vertex indices.
     */
    @Data
    @AllArgsConstructor
    public static class Face3D {
        private int v1, v2, v3;
        private AnsiColor color;
        private Texture3D texture;

        /**
         * Constructor for face with only vertex indices (backwards compatibility).
         */
        public Face3D(int v1, int v2, int v3) {
            this(v1, v2, v3, AnsiColor.WHITE, null);
        }

        /**
         * Constructor for face with color but no texture.
         */
        public Face3D(int v1, int v2, int v3, AnsiColor color) {
            this(v1, v2, v3, color, null);
        }

        /**
         * Checks if this face has a texture applied.
         */
        public boolean hasTexture() {
            return texture != null;
        }

        /**
         * Gets the effective color for rendering, considering texture if present.
         *
         * @param u texture U coordinate (0.0 to 1.0)
         * @param v texture V coordinate (0.0 to 1.0)
         * @param lightIntensity lighting intensity (0.0 to 1.0)
         * @return color to use for rendering
         */
        public AnsiColor getEffectiveColor(double u, double v, double lightIntensity) {
            if (hasTexture()) {
                return texture.getColorAt(u, v, lightIntensity);
            }
            return color;
        }

        /**
         * Gets the effective character for rendering, considering texture if present.
         *
         * @param u texture U coordinate (0.0 to 1.0)
         * @param v texture V coordinate (0.0 to 1.0)
         * @param lightIntensity lighting intensity (0.0 to 1.0)
         * @return character to use for rendering
         */
        public char getEffectiveCharacter(double u, double v, double lightIntensity) {
            if (hasTexture()) {
                return texture.getCharacterAt(u, v, lightIntensity);
            }
            // Default character based on lighting
            if (lightIntensity > 0.8) return '█';
            if (lightIntensity > 0.6) return '▓';
            if (lightIntensity > 0.4) return '▒';
            if (lightIntensity > 0.2) return '░';
            return '·';
        }
    }
}
