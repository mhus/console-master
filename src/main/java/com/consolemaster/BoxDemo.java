package com.consolemaster;

import java.io.IOException;

/**
 * Demo application showcasing the Box component with native styling.
 * Now uses native terminal implementation instead of JLine.
 */
public class BoxDemo {

    public static void main(String[] args) {
        try {
            // Create the main screen canvas
            ScreenCanvas screen = new ScreenCanvas(80, 25);

            // Create a composite canvas with FlowLayout for multiple boxes
            CompositeCanvas content = new CompositeCanvas(5, 3,
                                                         screen.getWidth() - 10,
                                                         screen.getHeight() - 6,
                                                         new FlowLayout(3, 2));

            // Create boxes with different border styles and colors

            // Simple border box
            Box simpleBox = new Box(0, 0, 20, 5, new SimpleBorder());
            Text simpleText = new Text(0, 0, 0, 0, "Simple Border", Text.Alignment.CENTER);
            simpleText.setForegroundColor(AnsiColor.WHITE);
            simpleBox.setChild(simpleText);
            content.addChild(simpleBox);

            // Thick border box
            Box thickBox = new Box(0, 0, 20, 5, new ThickBorder());
            Text thickText = new Text(0, 0, 0, 0, "Thick Border", Text.Alignment.CENTER);
            thickText.setForegroundColor(AnsiColor.YELLOW);
            thickText.setFormats(AnsiFormat.BOLD);
            thickBox.setChild(thickText);
            content.addChild(thickBox);

            // Colored border box
            SimpleBorder coloredBorder = new SimpleBorder();
            coloredBorder.setBorderColor(AnsiColor.RED);
            coloredBorder.setBorderFormats(AnsiFormat.BOLD);
            Box coloredBox = new Box(0, 0, 20, 5, coloredBorder);
            Text coloredText = new Text(0, 0, 0, 0, "Colored Border", Text.Alignment.CENTER);
            coloredText.setForegroundColor(AnsiColor.RED);
            coloredBox.setChild(coloredText);
            content.addChild(coloredBox);

            // Multi-line content box
            Box multiBox = new Box(0, 0, 25, 6, new SimpleBorder());
            Text multiText = new Text(0, 0, 0, 0, "Multi-line\nContent\nBox", Text.Alignment.CENTER);
            multiText.setForegroundColor(AnsiColor.GREEN);
            multiBox.setChild(multiText);
            content.addChild(multiBox);

            screen.setContentCanvas(content);

            // Simple render and wait
            screen.render();
            System.out.println("\nBox Demo - Press Enter to continue...");
            System.in.read();

            screen.close();

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
