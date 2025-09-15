package com.consolemaster;

import java.io.IOException;

/**
 * Demo application showcasing the Text component with various alignments and styling.
 * Now uses native terminal implementation instead of JLine.
 */
public class TextDemo {

    public static void main(String[] args) {
        try {
            // Create the main screen canvas
            ScreenCanvas screen = new ScreenCanvas(80, 25);

            // Create a composite canvas with FlowLayout
            Composite content = new Composite("content",
                    screen.getWidth() - 10,
                                                         screen.getHeight() - 6,
                                                         new FlowLayout(2, 1));

            // Title
            Text title = new Text("title", 0, 2, "Text Component Demo", Text.Alignment.CENTER);
            title.setForegroundColor(AnsiColor.BRIGHT_WHITE);
            title.setFormats(AnsiFormat.BOLD, AnsiFormat.UNDERLINE);
            content.addChild(title);

            // Left aligned text
            Text leftText = new Text("leftText", 40, 1, "Left Aligned Text", Text.Alignment.LEFT);
            leftText.setForegroundColor(AnsiColor.GREEN);
            content.addChild(leftText);

            // Center aligned text
            Text centerText = new Text("centerText", 40, 1, "Center Aligned Text", Text.Alignment.CENTER);
            centerText.setForegroundColor(AnsiColor.YELLOW);
            centerText.setFormats(AnsiFormat.BOLD);
            content.addChild(centerText);

            // Right aligned text
            Text rightText = new Text("rightText", 40, 1, "Right Aligned Text", Text.Alignment.RIGHT);
            rightText.setForegroundColor(AnsiColor.CYAN);
            content.addChild(rightText);

            // Multi-line text
            Text multiText = new Text("multiText", 50, 4,
                "This is a multi-line text example.\nIt demonstrates word wrapping\nand multiple line handling\nin the Text component.",
                Text.Alignment.LEFT);
            multiText.setForegroundColor(AnsiColor.MAGENTA);
            content.addChild(multiText);

            // Styled text examples
            Text boldText = new Text("boldText", 30, 1, "Bold Text Example", Text.Alignment.LEFT);
            boldText.setFormats(AnsiFormat.BOLD);
            boldText.setForegroundColor(AnsiColor.RED);
            content.addChild(boldText);

            Text italicText = new Text("italicText", 30, 1, "Italic Text Example", Text.Alignment.LEFT);
            italicText.setFormats(AnsiFormat.ITALIC);
            italicText.setForegroundColor(AnsiColor.BLUE);
            content.addChild(italicText);

            screen.setContent(content);

            // Simple render and wait
            screen.render();
            System.out.println("\nText Demo - Press Enter to continue...");
            System.in.read();

            screen.close();

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
