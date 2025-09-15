package com.consolemaster;

import java.io.IOException;

/**
 * Demo application showcasing the FlowLayout system using modern Box and Text components.
 * Now uses native terminal implementation instead of JLine.
 */
public class LayoutDemo {

    public static void main(String[] args) {
        try {
            // Create the main screen canvas
            ScreenCanvas screen = new ScreenCanvas(80, 25);

            // Create a composite canvas with FlowLayout
            Composite flowContainer = new Composite("flowContainer",
                    screen.getWidth() - 10,
                                                               screen.getHeight() - 6,
                                                               new FlowLayout(2, 2));

            // Create multiple text components with different styling
            for (int i = 1; i <= 8; i++) {
                Box box = new Box("box" + i, 12, 3, new DefaultBorder());
                Text text = new Text("text" + i, 0, 0, "Item " + i, Text.Alignment.CENTER);

                // Apply different colors
                AnsiColor color = switch (i % 4) {
                    case 0 -> AnsiColor.RED;
                    case 1 -> AnsiColor.GREEN;
                    case 2 -> AnsiColor.BLUE;
                    case 3 -> AnsiColor.YELLOW;
                    default -> AnsiColor.WHITE;
                };

                text.setForegroundColor(color);
                text.setFormats(AnsiFormat.BOLD);
                box.setChild(text);
                flowContainer.addChild(box);
            }

            screen.setContent(flowContainer);

            // Simple render and wait
            screen.render();
            System.out.println("\nPress Enter to continue...");
            System.in.read();

            screen.close();

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
