package com.consolemaster;

import java.io.IOException;

/**
 * Demo application to showcase the console framework capabilities with ANSI colors and formatting.
 * Creates a sample application with multiple styled canvas elements.
 */
public class ConsoleMasterDemo {

    public static void main(String[] args) {
        try {
            // Create the main screen canvas
            ScreenCanvas screen = new ScreenCanvas(60, 20);

            // Create a composite canvas for the main content
            CompositeCanvas mainContent = new CompositeCanvas(0, 0,
                                                             screen.getWidth(),
                                                             screen.getHeight());

            // Create styled header canvas
            Canvas header = new Canvas(0, 0, screen.getWidth(), 3) {
                @Override
                public void paint(Graphics graphics) {
                    // Set bold yellow text on blue background for title
                    graphics.setForegroundColor(AnsiColor.BRIGHT_YELLOW);
                    graphics.setBackgroundColor(AnsiColor.BLUE);
                    graphics.setFormats(AnsiFormat.BOLD);

                    String title = "=== Console Master Framework Demo ===";
                    int centerX = getWidth() / 2 - title.length() / 2;
                    graphics.fillRect(0, 0, getWidth(), 1, ' '); // Fill background
                    graphics.drawString(centerX, 0, title);

                    // Reset style and draw subtitle
                    graphics.resetStyle();
                    graphics.setForegroundColor(AnsiColor.CYAN);
                    graphics.setFormats(AnsiFormat.ITALIC);
                    String subtitle = "Welcome to the colorful game console!";
                    centerX = getWidth() / 2 - subtitle.length() / 2;
                    graphics.drawString(centerX, 1, subtitle);

                    // Draw border
                    graphics.resetStyle();
                    graphics.setForegroundColor(AnsiColor.WHITE);
                    graphics.drawHorizontalLine(0, getWidth() - 1, 2, '=');
                }
            };

            // Create colorful game area canvas
            Canvas gameArea = new Canvas(2, 4, screen.getWidth() - 20, 12) {
                @Override
                public void paint(Graphics graphics) {
                    // Draw border in green
                    graphics.setForegroundColor(AnsiColor.GREEN);
                    graphics.drawRect(0, 0, getWidth(), getHeight(), '#');

                    // Game title in red bold
                    graphics.setForegroundColor(AnsiColor.BRIGHT_RED);
                    graphics.setFormats(AnsiFormat.BOLD);
                    graphics.drawString(2, 1, "GAME AREA");

                    // Game content with various colors
                    graphics.resetStyle();
                    graphics.setForegroundColor(AnsiColor.WHITE);
                    graphics.drawString(2, 3, "This is where your");

                    graphics.setForegroundColor(AnsiColor.BRIGHT_GREEN);
                    graphics.setFormats(AnsiFormat.BOLD);
                    graphics.drawString(2, 4, "AWESOME GAME");

                    graphics.resetStyle();
                    graphics.setForegroundColor(AnsiColor.WHITE);
                    graphics.drawString(2, 5, "content would be displayed.");

                    // Status indicators with colors
                    graphics.setForegroundColor(AnsiColor.YELLOW);
                    graphics.drawString(2, 7, "Status: ");
                    graphics.setForegroundColor(AnsiColor.BRIGHT_GREEN);
                    graphics.setFormats(AnsiFormat.BOLD);
                    graphics.drawString(10, 7, "READY");

                    // Instructions with underline
                    graphics.resetStyle();
                    graphics.setForegroundColor(AnsiColor.CYAN);
                    graphics.setFormats(AnsiFormat.UNDERLINE);
                    graphics.drawString(2, 9, "Press any key to continue...");
                }
            };

            // Create colorful info panel
            Canvas infoPanel = new Canvas(screen.getWidth() - 18, 4, 18, 12) {
                @Override
                public void paint(Graphics graphics) {
                    // Border in magenta
                    graphics.setForegroundColor(AnsiColor.MAGENTA);
                    graphics.drawRect(0, 0, getWidth(), getHeight(), '|');

                    // Panel title
                    graphics.setForegroundColor(AnsiColor.BRIGHT_MAGENTA);
                    graphics.setFormats(AnsiFormat.BOLD);
                    graphics.drawString(2, 1, "INFO PANEL");

                    // Score in bright yellow
                    graphics.resetStyle();
                    graphics.setForegroundColor(AnsiColor.BRIGHT_YELLOW);
                    graphics.drawString(2, 3, "Score: ");
                    graphics.setFormats(AnsiFormat.BOLD);
                    graphics.drawString(9, 3, "1000");

                    // Level in bright blue
                    graphics.resetStyle();
                    graphics.setForegroundColor(AnsiColor.BRIGHT_BLUE);
                    graphics.drawString(2, 4, "Level: ");
                    graphics.setFormats(AnsiFormat.BOLD);
                    graphics.drawString(9, 4, "5");

                    // Lives in bright red
                    graphics.resetStyle();
                    graphics.setForegroundColor(AnsiColor.BRIGHT_RED);
                    graphics.drawString(2, 5, "Lives: ");
                    graphics.setFormats(AnsiFormat.BOLD);
                    graphics.drawString(9, 5, "3");

                    // Stats section
                    graphics.resetStyle();
                    graphics.setForegroundColor(AnsiColor.WHITE);
                    graphics.setFormats(AnsiFormat.UNDERLINE);
                    graphics.drawString(2, 7, "Stats:");

                    graphics.resetStyle();
                    graphics.setForegroundColor(AnsiColor.GREEN);
                    graphics.drawString(2, 8, "HP: 100");
                    graphics.setForegroundColor(AnsiColor.BLUE);
                    graphics.drawString(2, 9, "MP: 50");
                }
            };

            // Create styled footer
            Canvas footer = new Canvas(0, screen.getHeight() - 3, screen.getWidth(), 3) {
                @Override
                public void paint(Graphics graphics) {
                    // Top border
                    graphics.setForegroundColor(AnsiColor.WHITE);
                    graphics.drawHorizontalLine(0, getWidth() - 1, 0, '-');

                    // Status message with mixed colors
                    graphics.setForegroundColor(AnsiColor.WHITE);
                    graphics.drawString(1, 1, "Status: ");
                    graphics.setForegroundColor(AnsiColor.BRIGHT_GREEN);
                    graphics.setFormats(AnsiFormat.BOLD);
                    graphics.drawString(9, 1, "Ready");

                    graphics.resetStyle();
                    graphics.setForegroundColor(AnsiColor.WHITE);
                    graphics.drawString(16, 1, "| Controls: ");
                    graphics.setForegroundColor(AnsiColor.YELLOW);
                    graphics.setFormats(AnsiFormat.BOLD);
                    graphics.drawString(28, 1, "WASD");
                    graphics.resetStyle();
                    graphics.setForegroundColor(AnsiColor.WHITE);
                    graphics.drawString(33, 1, " to move, ");
                    graphics.setForegroundColor(AnsiColor.YELLOW);
                    graphics.setFormats(AnsiFormat.BOLD);
                    graphics.drawString(43, 1, "SPACE");
                    graphics.resetStyle();
                    graphics.setForegroundColor(AnsiColor.WHITE);
                    graphics.drawString(49, 1, " to action");
                }
            };

            // Add all canvases to the main content
            mainContent.addChild(header);
            mainContent.addChild(gameArea);
            mainContent.addChild(infoPanel);
            mainContent.addChild(footer);

            // Set the content canvas
            screen.setContentCanvas(mainContent);

            // Render the screen
            screen.render();

            // Keep the application running
            System.out.println("\nColorful demo rendered! Check your console for ANSI colors and formatting.");
            System.out.println("Press Enter to exit...");
            System.in.read();

            // Clean up
            screen.close();

        } catch (IOException e) {
            System.err.println("Error running demo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
