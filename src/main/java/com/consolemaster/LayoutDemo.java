package com.consolemaster;

import org.jline.utils.AttributedStyle;
import java.io.IOException;

/**
 * Demo application showcasing the Layout system with FlowLayout using modern Box and Text components.
 * Creates multiple small boxes with text content that are automatically arranged by the layout manager.
 */
public class LayoutDemo {

    public static void main(String[] args) {
        try {
            // Create the main screen canvas with a smaller minimum size for this demo
            ScreenCanvas screen = new ScreenCanvas(50, 18);

            // Create a composite canvas with FlowLayout
            CompositeCanvas flowContainer = new CompositeCanvas(2, 3,
                                                               screen.getWidth() - 4,
                                                               screen.getHeight() - 5,
                                                               new FlowLayout(1, 1));

            // Create several small boxes with text content to demonstrate flow layout
            AnsiColor[] colors = {AnsiColor.RED, AnsiColor.GREEN, AnsiColor.BLUE, AnsiColor.YELLOW,
                                 AnsiColor.MAGENTA, AnsiColor.CYAN, AnsiColor.BRIGHT_RED, AnsiColor.BRIGHT_GREEN};

            for (int i = 1; i <= 8; i++) {
                final int boxNumber = i;
                final AnsiColor boxColor = colors[(i - 1) % colors.length];

                // Create a Box with SimpleBorder
                Box box = new Box(0, 0, 10, 3, new SimpleBorder()); // Position will be set by layout

                // Create Text content for the box
                Text boxText = new Text(0, 0, 0, 0, "Box " + boxNumber, Text.Alignment.CENTER);
                boxText.setForegroundColor(boxColor);
                boxText.setBold(true);

                box.setChild(boxText);
                flowContainer.addChild(box);
            }

            // Create header using Text component
            Text header = new Text(0, 0, screen.getWidth(), 2,
                                  "Layout Demo - FlowLayout with Box and Text Components\n" +
                                  "=".repeat(screen.getWidth()),
                                  Text.Alignment.CENTER);
            header.setForegroundColor(AnsiColor.BRIGHT_CYAN);
            header.setBold(true);

            // Create info text
            Text infoText = new Text(0, screen.getHeight() - 2, screen.getWidth(), 2,
                                   "Boxes automatically flow to next row when current row is full.\nEach box uses modern Text component with styling.",
                                   Text.Alignment.CENTER);
            infoText.setForegroundColor(AnsiColor.WHITE);
            infoText.setItalic(true);

            // Create main container
            CompositeCanvas mainContent = new CompositeCanvas(0, 0,
                                                             screen.getWidth(),
                                                             screen.getHeight());
            mainContent.addChild(header);
            mainContent.addChild(flowContainer);
            mainContent.addChild(infoText);

            // Set the content canvas
            screen.setContentCanvas(mainContent);

            // Render the screen
            screen.render();

            System.out.println("\nModern Layout Demo rendered! Features:");
            System.out.println("- FlowLayout automatically arranges Box components");
            System.out.println("- Each Box contains a Text component with individual styling");
            System.out.println("- Demonstrates integration of Box, Text, and Layout systems");
            System.out.println("- Modern component-based approach with consistent styling");
            System.out.println("Demo completed successfully.");

            // Clean up
            screen.close();

        } catch (IOException e) {
            System.err.println("Error running Layout demo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
