package com.consolemaster;

import org.jline.utils.AttributedStyle;
import java.io.IOException;

/**
 * Demo application showcasing the Text canvas with various formatting and alignment options.
 * Demonstrates multi-line text, different alignments, colors, and formatting styles.
 */
public class TextDemo {

    public static void main(String[] args) {
        try {
            // Create the main screen canvas
            ScreenCanvas screen = new ScreenCanvas(80, 30);

            // Create main container
            CompositeCanvas mainContent = new CompositeCanvas(0, 0,
                                                             screen.getWidth(),
                                                             screen.getHeight());

            // Create title
            Text title = new Text(0, 0, screen.getWidth(), 2,
                                 "Text Canvas Demo - Formatting and Alignment Showcase",
                                 Text.Alignment.CENTER);
            title.setForegroundColor(AnsiColor.BRIGHT_CYAN);
            title.setBold(true);

            // Create separator line
            Text separator = new Text(0, 2, screen.getWidth(), 1,
                                    "=".repeat(screen.getWidth()),
                                    Text.Alignment.LEFT);
            separator.setForegroundColor(AnsiColor.WHITE);

            // Left-aligned text box
            Box leftBox = new Box(2, 4, 25, 8, new SimpleBorder());
            Text leftText = new Text(0, 0, 0, 0,
                                   "LEFT ALIGNED\n\nThis text is left-aligned.\nMultiple lines are supported.\nWord wrapping works automatically when lines are too long for the width.",
                                   Text.Alignment.LEFT);
            leftText.setForegroundColor(AnsiColor.GREEN);
            leftBox.setChild(leftText);

            // Center-aligned text box
            Box centerBox = new Box(29, 4, 25, 8, new SimpleBorder());
            Text centerText = new Text(0, 0, 0, 0,
                                     "CENTER ALIGNED\n\nThis text is\ncentered in\neach line.",
                                     Text.Alignment.CENTER);
            centerText.setForegroundColor(AnsiColor.YELLOW);
            centerText.setBold(true);
            centerBox.setChild(centerText);

            // Right-aligned text box
            Box rightBox = new Box(56, 4, 22, 8, new SimpleBorder());
            Text rightText = new Text(0, 0, 0, 0,
                                    "RIGHT ALIGNED\n\nThis text is\nright-aligned\nin each line.",
                                    Text.Alignment.RIGHT);
            rightText.setForegroundColor(AnsiColor.MAGENTA);
            rightText.setItalic(true);
            rightBox.setChild(rightText);

            // Styled text examples
            Text styledTitle = new Text(2, 14, 76, 1,
                                      "Text Styling Examples:",
                                      Text.Alignment.LEFT);
            styledTitle.setForegroundColor(AnsiColor.BRIGHT_WHITE);
            styledTitle.setUnderline(true);

            // Bold text
            Text boldText = new Text(2, 16, 35, 2,
                                   "BOLD TEXT\nThis text is rendered in bold.",
                                   Text.Alignment.LEFT);
            boldText.setForegroundColor(AnsiColor.BRIGHT_RED);
            boldText.setBold(true);

            // Italic text
            Text italicText = new Text(40, 16, 35, 2,
                                     "ITALIC TEXT\nThis text is rendered in italic.",
                                     Text.Alignment.LEFT);
            italicText.setForegroundColor(AnsiColor.BRIGHT_BLUE);
            italicText.setItalic(true);

            // Underlined text
            Text underlineText = new Text(2, 19, 35, 2,
                                        "UNDERLINED TEXT\nThis text is underlined.",
                                        Text.Alignment.LEFT);
            underlineText.setForegroundColor(AnsiColor.BRIGHT_GREEN);
            underlineText.setUnderline(true);

            // Multiple formats
            Text multiFormatText = new Text(40, 19, 35, 2,
                                          "MULTIPLE FORMATS\nBold, italic, and underlined!",
                                          Text.Alignment.LEFT);
            multiFormatText.setForegroundColor(AnsiColor.BRIGHT_YELLOW);
            multiFormatText.setFormats(AnsiFormat.BOLD, AnsiFormat.ITALIC, AnsiFormat.UNDERLINE);

            // Background color example
            Text backgroundText = new Text(2, 22, 76, 3,
                                         "BACKGROUND COLOR EXAMPLE\n\nThis text has a colored background to make it stand out from other content.",
                                         Text.Alignment.CENTER);
            backgroundText.setForegroundColor(AnsiColor.WHITE);
            backgroundText.setBackgroundColor(AnsiColor.BLUE);
            backgroundText.setBold(true);

            // Word wrap demonstration
            Text wordWrapText = new Text(2, 26, 76, 3,
                                       "WORD WRAP DEMONSTRATION: This is a very long line of text that will automatically wrap to the next line when it exceeds the width of the text area. Word wrapping can be enabled or disabled as needed.",
                                       Text.Alignment.LEFT);
            wordWrapText.setForegroundColor(AnsiColor.CYAN);

            // Add all components to main content
            mainContent.addChild(title);
            mainContent.addChild(separator);
            mainContent.addChild(leftBox);
            mainContent.addChild(centerBox);
            mainContent.addChild(rightBox);
            mainContent.addChild(styledTitle);
            mainContent.addChild(boldText);
            mainContent.addChild(italicText);
            mainContent.addChild(underlineText);
            mainContent.addChild(multiFormatText);
            mainContent.addChild(backgroundText);
            mainContent.addChild(wordWrapText);

            // Set the content canvas
            screen.setContentCanvas(mainContent);

            // Render the screen
            screen.render();

            System.out.println("\nText Canvas Demo rendered! Features demonstrated:");
            System.out.println("- Text Alignment: LEFT, CENTER, RIGHT");
            System.out.println("- Multi-line text support with automatic line breaks");
            System.out.println("- Word wrapping for long text lines");
            System.out.println("- Text formatting: BOLD, ITALIC, UNDERLINE");
            System.out.println("- Multiple format combinations");
            System.out.println("- Foreground and background colors");
            System.out.println("- Integration with Box components and borders");
            System.out.println("Demo completed successfully.");

            // Clean up
            screen.close();

        } catch (IOException e) {
            System.err.println("Error running Text demo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
