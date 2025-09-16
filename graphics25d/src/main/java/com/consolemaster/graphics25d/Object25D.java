package com.consolemaster.graphics25d;

import com.consolemaster.AnsiColor;
import com.consolemaster.StyledChar;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a 2.5D object with position, faces, and textures.
 * Objects are composed of faces, each with their own texture and color.
 * In 2.5D space, objects can only be rotated in 90-degree increments.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Object25D {

    /**
     * Represents a face of a 2.5D object with vertices, texture, and color.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Face25D {
        private List<Point25D> vertices = new ArrayList<>();
        private StyledChar[][] texture;
        private AnsiColor color = AnsiColor.WHITE;
        private char fillChar = '#';

        public Face25D(List<Point25D> vertices, AnsiColor color) {
            this.vertices = new ArrayList<>(vertices);
            this.color = color;
        }

        public Face25D(List<Point25D> vertices, char fillChar, AnsiColor color) {
            this.vertices = new ArrayList<>(vertices);
            this.fillChar = fillChar;
            this.color = color;
        }
    }

    /**
     * 90-degree rotation directions for 2.5D objects.
     */
    public enum Rotation90 {
        NONE(0),        // No rotation
        CW_90(1),       // 90 degrees clockwise
        CW_180(2),      // 180 degrees
        CW_270(3);      // 270 degrees clockwise (or 90 counter-clockwise)

        private final int steps;

        Rotation90(int steps) {
            this.steps = steps;
        }

        public int getSteps() {
            return steps;
        }

        /**
         * Combines two rotations.
         */
        public Rotation90 combine(Rotation90 other) {
            int totalSteps = (this.steps + other.steps) % 4;
            return fromSteps(totalSteps);
        }

        public static Rotation90 fromSteps(int steps) {
            return switch (steps % 4) {
                case 0 -> NONE;
                case 1 -> CW_90;
                case 2 -> CW_180;
                case 3 -> CW_270;
                default -> NONE;
            };
        }
    }

    private Point25D position = new Point25D(0, 0, 0);
    private List<Face25D> faces = new ArrayList<>();
    private Rotation90 rotation = Rotation90.NONE;

    /**
     * Adds a face to this object.
     */
    public void addFace(Face25D face) {
        faces.add(face);
    }

    /**
     * Creates a simple cube object at the specified position with the given size.
     */
    public static Object25D createCube(Point25D position, double size, AnsiColor color) {
        Object25D cube = new Object25D();
        cube.setPosition(position);

        double half = size / 2.0;

        // Define the 8 vertices of a cube relative to center
        Point25D[] vertices = {
            new Point25D(-half, -half, -half), // 0: bottom-left-back
            new Point25D(half, -half, -half),  // 1: bottom-right-back
            new Point25D(half, half, -half),   // 2: top-right-back
            new Point25D(-half, half, -half),  // 3: top-left-back
            new Point25D(-half, -half, half),  // 4: bottom-left-front
            new Point25D(half, -half, half),   // 5: bottom-right-front
            new Point25D(half, half, half),    // 6: top-right-front
            new Point25D(-half, half, half)    // 7: top-left-front
        };

        // Create the 6 faces of the cube
        // Front face
        cube.addFace(new Face25D(List.of(vertices[4], vertices[5], vertices[6], vertices[7]), color));
        // Back face
        cube.addFace(new Face25D(List.of(vertices[1], vertices[0], vertices[3], vertices[2]), color));
        // Left face
        cube.addFace(new Face25D(List.of(vertices[0], vertices[4], vertices[7], vertices[3]), color));
        // Right face
        cube.addFace(new Face25D(List.of(vertices[5], vertices[1], vertices[2], vertices[6]), color));
        // Top face
        cube.addFace(new Face25D(List.of(vertices[3], vertices[7], vertices[6], vertices[2]), color));
        // Bottom face
        cube.addFace(new Face25D(List.of(vertices[0], vertices[1], vertices[5], vertices[4]), color));

        return cube;
    }

    /**
     * Creates a simple pyramid object at the specified position with the given size.
     */
    public static Object25D createPyramid(Point25D position, double size, AnsiColor color) {
        Object25D pyramid = new Object25D();
        pyramid.setPosition(position);

        double half = size / 2.0;

        // Define the 5 vertices of a pyramid
        Point25D[] vertices = {
            new Point25D(-half, -half, -half), // 0: bottom-left
            new Point25D(half, -half, -half),  // 1: bottom-right
            new Point25D(half, -half, half),   // 2: bottom-front-right
            new Point25D(-half, -half, half),  // 3: bottom-front-left
            new Point25D(0, half, 0)           // 4: top apex
        };

        // Create the 5 faces of the pyramid
        // Bottom face
        pyramid.addFace(new Face25D(List.of(vertices[0], vertices[1], vertices[2], vertices[3]), color));
        // Front face
        pyramid.addFace(new Face25D(List.of(vertices[3], vertices[2], vertices[4]), color));
        // Right face
        pyramid.addFace(new Face25D(List.of(vertices[2], vertices[1], vertices[4]), color));
        // Back face
        pyramid.addFace(new Face25D(List.of(vertices[1], vertices[0], vertices[4]), color));
        // Left face
        pyramid.addFace(new Face25D(List.of(vertices[0], vertices[3], vertices[4]), color));

        return pyramid;
    }

    /**
     * Creates a textured cube with different characters on each face.
     */
    public static Object25D createTexturedCube(Point25D position, double size) {
        Object25D cube = createCube(position, size, AnsiColor.WHITE);

        // Set different characters for each face to create texture variety
        char[] faceChars = {'█', '▓', '▒', '░', '▪', '▫'};
        AnsiColor[] faceColors = {
            AnsiColor.RED, AnsiColor.GREEN, AnsiColor.BLUE,
            AnsiColor.YELLOW, AnsiColor.MAGENTA, AnsiColor.CYAN
        };

        for (int i = 0; i < cube.getFaces().size() && i < faceChars.length; i++) {
            Face25D face = cube.getFaces().get(i);
            face.setFillChar(faceChars[i]);
            face.setColor(faceColors[i]);
        }

        return cube;
    }

    /**
     * Transforms this object by translating its position.
     */
    public Object25D translate(Point25D delta) {
        Object25D transformed = new Object25D();
        transformed.setPosition(position.add(delta));
        transformed.setRotation(rotation);

        // Copy faces (they remain relative to the object's position)
        for (Face25D face : faces) {
            Face25D newFace = new Face25D();
            newFace.setVertices(new ArrayList<>(face.getVertices()));
            newFace.setTexture(face.getTexture());
            newFace.setColor(face.getColor());
            newFace.setFillChar(face.getFillChar());
            transformed.addFace(newFace);
        }

        return transformed;
    }

    /**
     * Rotates this object by 90 degrees clockwise around the Y-axis.
     */
    public Object25D rotateY90() {
        Object25D rotated = new Object25D();
        rotated.setPosition(position);
        rotated.setRotation(rotation.combine(Rotation90.CW_90));

        // Rotate each face's vertices by 90 degrees around Y-axis
        for (Face25D face : faces) {
            Face25D newFace = new Face25D();
            List<Point25D> rotatedVertices = new ArrayList<>();

            for (Point25D vertex : face.getVertices()) {
                // 90-degree rotation around Y-axis: (x, y, z) -> (z, y, -x)
                Point25D rotatedVertex = new Point25D(vertex.getZ(), vertex.getY(), -vertex.getX());
                rotatedVertices.add(rotatedVertex);
            }

            newFace.setVertices(rotatedVertices);
            newFace.setTexture(face.getTexture());
            newFace.setColor(face.getColor());
            newFace.setFillChar(face.getFillChar());
            rotated.addFace(newFace);
        }

        return rotated;
    }

    /**
     * Rotates this object to the specified 90-degree rotation.
     */
    public Object25D rotateTo(Rotation90 targetRotation) {
        Object25D result = this;
        Rotation90 currentRot = rotation;

        // Calculate how many 90-degree steps we need
        int stepsNeeded = (targetRotation.getSteps() - currentRot.getSteps() + 4) % 4;

        // Apply the rotation steps
        for (int i = 0; i < stepsNeeded; i++) {
            result = result.rotateY90();
        }

        return result;
    }

    /**
     * Gets the world position of a vertex by adding object position to relative vertex position
     * and applying the object's rotation.
     */
    public Point25D getWorldVertex(Point25D relativeVertex) {
        Point25D rotatedVertex = relativeVertex;

        // Apply 90-degree rotations based on current rotation
        for (int i = 0; i < rotation.getSteps(); i++) {
            // 90-degree rotation around Y-axis: (x, y, z) -> (z, y, -x)
            rotatedVertex = new Point25D(rotatedVertex.getZ(), rotatedVertex.getY(), -rotatedVertex.getX());
        }

        return position.add(rotatedVertex);
    }

    @Override
    public String toString() {
        return String.format("Object25D[pos=%s, faces=%d, rot=%s]", position, faces.size(), rotation);
    }
}
