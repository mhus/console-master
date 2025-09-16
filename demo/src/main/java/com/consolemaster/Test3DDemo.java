package com.consolemaster;

import com.consolemaster.graphic3d.*;

/**
 * Test demo for the corrected 3D rendering system.
 */
public class Test3DDemo {
    public static void main(String[] args) {
        try {
            // Create a simple 3D scene
            Graphic3DCanvas canvas3D = new Graphic3DCanvas("3D Test", 80, 30);

            // Position camera
            canvas3D.getCamera().setPosition(new Point3D(0, 0, 5));
            canvas3D.getCamera().lookAt(new Point3D(0, 0, 0));

            // Create a simple cube
            Mesh3D cube = Mesh3D.createCube(4.0);
            canvas3D.addMesh(cube);

            // Set wireframe mode for testing
            canvas3D.setRenderMode(Graphic3DCanvas.RenderMode.WIREFRAME);
            canvas3D.setWireframeChar('*');
            canvas3D.setWireframeColor(AnsiColor.CYAN);

            // Create a simple graphics context for testing
            GeneralGraphics graphics = new GeneralGraphics(80, 30);

            // Render the scene
            canvas3D.paint(graphics);

            // Print result
            Screenshot screenshot = new Screenshot(graphics);
            screenshot.print(false);

            System.out.println("\n3D rendering test completed successfully!");

        } catch (Exception e) {
            System.err.println("Error in 3D rendering: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Simple test graphics implementation for verification.
     */
    static class TestGraphics extends GeneralGraphics {
        private AnsiColor currentColor = AnsiColor.WHITE;

        public TestGraphics(int width, int height) {
            super(width, height);
            clear();
        }

        @Override
        public void drawChar(int x, int y, char character) {
            drawStyledChar(x, y, character, currentColor, AnsiColor.BLACK);
        }

        @Override
        public void setForegroundColor(AnsiColor color) {
            this.currentColor = color;
        }

        @Override
        public void setBackgroundColor(AnsiColor color) {
            // Not implemented for test
        }

        @Override
        public void drawString(int x, int y, String text) {
            drawStyledString(x, y, text, currentColor, AnsiColor.BLACK);
        }

        public void printToConsole() {
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    System.out.print(getStyledChar(x,y).getCharacter());
                }
                System.out.println();
            }
        }
    }
}
