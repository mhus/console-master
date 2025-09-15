package com.consolemaster;

import com.consolemaster.graphic3d.Camera3D;
import com.consolemaster.graphic3d.Graphic3DCanvas;
import com.consolemaster.graphic3d.Matrix4x4;
import com.consolemaster.graphic3d.Mesh3D;
import com.consolemaster.graphic3d.Point3D;

/**
 * Demo class showcasing the Graphic3DCanvas capabilities.
 * Creates various 3D objects and demonstrates different rendering modes.
 */
public class Graphic3DDemo {

    public static void main(String[] args) {
        // Create a 3D canvas
        Graphic3DCanvas canvas3D = new Graphic3DCanvas("3D Demo", 80, 40);

        // Configure rendering
        canvas3D.setRenderMode(Graphic3DCanvas.RenderMode.WIREFRAME);
        canvas3D.setWireframeChar('*');
        canvas3D.setWireframeColor(AnsiColor.CYAN);
        canvas3D.setFillChar('#');
        canvas3D.setFillColor(AnsiColor.GREEN);

        // Position the camera
        canvas3D.getCamera().setPosition(new Point3D(0, 0, 5));
        canvas3D.getCamera().lookAt(new Point3D(0, 0, 0));

        // Create and add 3D objects
        createDemoScene(canvas3D);

        System.out.println("3D Demo Scene created successfully!");
        System.out.println("Canvas size: " + canvas3D.getWidth() + "x" + canvas3D.getHeight());
        System.out.println("Number of meshes: " + canvas3D.getMeshes().size());
        System.out.println("Render mode: " + canvas3D.getRenderMode());
    }

    /**
     * Creates a demo scene with various 3D objects.
     */
    private static void createDemoScene(Graphic3DCanvas canvas) {
        // Create a cube at the origin
        Mesh3D cube = Mesh3D.createCube(2.0);
        canvas.addMesh(cube);

        // Create a pyramid to the right
        Mesh3D pyramid = Mesh3D.createPyramid(1.5);
        Matrix4x4 pyramidTransform = Matrix4x4.translation(3, 0, 0);
        Mesh3D transformedPyramid = pyramid.transform(pyramidTransform);
        canvas.addMesh(transformedPyramid);

        // Create a rotated cube to the left
        Mesh3D leftCube = Mesh3D.createCube(1.0);
        Matrix4x4 rotation = Matrix4x4.rotationY(Math.PI / 4).multiply(Matrix4x4.rotationX(Math.PI / 6));
        Matrix4x4 translation = Matrix4x4.translation(-3, 0, 0);
        Matrix4x4 leftCubeTransform = translation.multiply(rotation);
        Mesh3D transformedLeftCube = leftCube.transform(leftCubeTransform);
        canvas.addMesh(transformedLeftCube);
    }

    /**
     * Demonstrates different rendering modes.
     */
    public static void demonstrateRenderModes(Graphic3DCanvas canvas) {
        System.out.println("\n=== Rendering Mode Demonstration ===");

        // Wireframe mode
        canvas.setRenderMode(Graphic3DCanvas.RenderMode.WIREFRAME);
        System.out.println("Wireframe mode: Shows object edges only");

        // Filled mode
        canvas.setRenderMode(Graphic3DCanvas.RenderMode.FILLED);
        System.out.println("Filled mode: Shows solid triangles with depth testing");

        // Both modes
        canvas.setRenderMode(Graphic3DCanvas.RenderMode.BOTH);
        System.out.println("Both mode: Shows filled triangles with wireframe overlay");
    }

    /**
     * Demonstrates camera movement.
     */
    public static void demonstrateCameraMovement(Graphic3DCanvas canvas) {
        System.out.println("\n=== Camera Movement Demonstration ===");

        Camera3D camera = canvas.getCamera();

        // Move camera around
        camera.moveForward(2.0);
        System.out.println("Moved camera forward, new position: " + camera.getPosition());

        camera.moveRight(1.0);
        System.out.println("Moved camera right, new position: " + camera.getPosition());

        camera.moveUp(0.5);
        System.out.println("Moved camera up, new position: " + camera.getPosition());

        // Rotate camera
        camera.rotate(Math.PI / 8, Math.PI / 6, 0);
        System.out.println("Rotated camera, new rotation: " + camera.getRotation());

        // Look at origin
        camera.lookAt(new Point3D(0, 0, 0));
        System.out.println("Camera looking at origin, rotation: " + camera.getRotation());
    }

    /**
     * Creates an animated rotating cube scene.
     */
    public static Graphic3DCanvas createAnimatedScene() {
        Graphic3DCanvas canvas = new Graphic3DCanvas("Animated 3D", 60, 30);
        canvas.setRenderMode(Graphic3DCanvas.RenderMode.WIREFRAME);
        canvas.setWireframeChar('+');
        canvas.setWireframeColor(AnsiColor.YELLOW);

        // Position camera for good view
        canvas.getCamera().setPosition(new Point3D(0, 2, 6));
        canvas.getCamera().lookAt(new Point3D(0, 0, 0));

        return canvas;
    }

    /**
     * Updates the animated scene with rotation.
     */
    public static void updateAnimatedScene(Graphic3DCanvas canvas, double time) {
        canvas.clearMeshes();

        // Create rotating cube
        Mesh3D cube = Mesh3D.createCube(2.0);
        Matrix4x4 rotationY = Matrix4x4.rotationY(time);
        Matrix4x4 rotationX = Matrix4x4.rotationX(time * 0.5);
        Matrix4x4 rotation = rotationY.multiply(rotationX);

        Mesh3D animatedCube = cube.transform(rotation);
        canvas.addMesh(animatedCube);

        // Create orbiting pyramid
        double orbitRadius = 4.0;
        double orbitX = Math.cos(time * 2) * orbitRadius;
        double orbitZ = Math.sin(time * 2) * orbitRadius;

        Mesh3D pyramid = Mesh3D.createPyramid(1.0);
        Matrix4x4 pyramidTransform = Matrix4x4.translation(orbitX, 0, orbitZ);
        Mesh3D orbitingPyramid = pyramid.transform(pyramidTransform);
        canvas.addMesh(orbitingPyramid);
    }
}
