package com.consolemaster;

import java.io.IOException;

/**
 * Demo application showcasing native ANSI styling without JLine dependency.
 * Demonstrates the framework's native terminal capabilities.
 */
public class GraphicsDemo {

    public static void main(String[] args) {
        try {
            // Create the main screen canvas
            ScreenCanvas screen = new ScreenCanvas(80, 25);

            // Create a composite canvas for content
            Composite content = new Composite("content",
                    screen.getWidth() - 10,
                                                         screen.getHeight() - 6,
                                                         new FlowLayout(2, 1));

            // Title
            Text title = new Text("title", 0, 2, "Native ANSI Styling Demo", Text.Alignment.CENTER);
            title.setForegroundColor(AnsiColor.BRIGHT_CYAN);
            title.setFormats(AnsiFormat.BOLD, AnsiFormat.UNDERLINE);
            content.addChild(title);

            // Color demonstrations
            AnsiColor[] colors = {AnsiColor.RED, AnsiColor.GREEN, AnsiColor.BLUE,
                                 AnsiColor.YELLOW, AnsiColor.MAGENTA, AnsiColor.CYAN};

            for (AnsiColor color : colors) {
                Text colorText = new Text("colorText_" + color.name(), 0, 1, color.name() + " Text", Text.Alignment.LEFT);
                colorText.setForegroundColor(color);
                colorText.setFormats(AnsiFormat.BOLD);
                content.addChild(colorText);
            }

            // Format demonstrations
            Text boldText = new Text("boldText", 0, 1, "Bold Text", Text.Alignment.LEFT);
            boldText.setFormats(AnsiFormat.BOLD);
            content.addChild(boldText);

            Text italicText = new Text("italicText", 0, 1, "Italic Text", Text.Alignment.LEFT);
            italicText.setFormats(AnsiFormat.ITALIC);
            content.addChild(italicText);

            Text underlineText = new Text("underlineText", 0, 1, "Underlined Text", Text.Alignment.LEFT);
            underlineText.setFormats(AnsiFormat.UNDERLINE);
            content.addChild(underlineText);

            screen.setContent(content);

            // Simple render and wait
            screen.render();
            System.out.println("\nNative ANSI Demo - Press Enter to continue...");
            System.in.read();

            screen.close();

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
