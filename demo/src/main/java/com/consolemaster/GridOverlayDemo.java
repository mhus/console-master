package com.consolemaster;

import java.io.IOException;

/**
 * Demo application showcasing the DisplayGridOverlayCanvas functionality.
 * This demo displays a grid overlay with crosses every 10 characters to help
 * with layout visualization and debugging.
 */
public class GridOverlayDemo {

    public static void main(String[] args) {
        try {
            // Create the main screen canvas
            ScreenCanvas screen = new ScreenCanvas("Grid Overlay Demo", 80, 24);

            // Create a composite to hold our content
            Composite mainComposite = new Composite("Main", 0, 0, screen.getWidth(), screen.getHeight());

            // Add some background content to make the grid more visible
            Text titleText = new Text("Title", 76, 1, "Grid Overlay Demo - Shows crosses every 10 characters");
            titleText.setBold(true);
            titleText.setForegroundColor(AnsiColor.BRIGHT_CYAN);

            Text instructionText = new Text("Instructions", 76, 3,
                "This overlay shows a grid with crosses (+) at every 10th character position.\n" +
                "Horizontal lines (-) and vertical lines (|) connect the crosses.\n" +
                "Use this for layout debugging and positioning reference.");

            // Create some sample content at specific grid positions
            Text sample1 = new Text("Sample1", 20, 1, "Position (10,10)");
            sample1.setForegroundColor(AnsiColor.BRIGHT_GREEN);

            Text sample2 = new Text("Sample2", 20, 1, "Position (30,15)");
            sample2.setForegroundColor(AnsiColor.BRIGHT_YELLOW);

            Text sample3 = new Text("Sample3", 25, 1, "Position (50,20)");
            sample3.setForegroundColor(AnsiColor.BRIGHT_MAGENTA);

            // Create the grid overlay canvas
            DisplayGridOverlayCanvas gridOverlay = new DisplayGridOverlayCanvas("GridOverlay");

            // Add all components to the composite
            mainComposite.addChild(titleText);
            mainComposite.addChild(instructionText);
            mainComposite.addChild(sample1);
            mainComposite.addChild(sample2);
            mainComposite.addChild(sample3);

            // Add the grid overlay last so it appears on top
            mainComposite.addChild(gridOverlay);

            // Add the composite to the screen
            screen.addChild(mainComposite);

            // Create and start the process loop
            ProcessLoop processLoop = new ProcessLoop(screen);

            System.out.println("Starting Grid Overlay Demo...");
            System.out.println("Press 'q' to quit");

            processLoop.start();

        } catch (IOException e) {
            System.err.println("Failed to initialize demo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
