package com.consolemaster;

import org.jline.utils.AttributedStyle;
import java.io.IOException;

/**
 * Enhanced demo application showcasing JLine's AttributedStyle integration.
 * Demonstrates the improved console framework with efficient ANSI handling.
 */
public class JLineDemo {

    public static void main(String[] args) {
        try {
            // Create the main screen canvas
            ScreenCanvas screen = new ScreenCanvas(60, 20);

            // Create a composite canvas for the main content
            CompositeCanvas mainContent = new CompositeCanvas(0, 0,
                                                             screen.getWidth(),
                                                             screen.getHeight());

            // Create styled header canvas using JLine's AttributedStyle
            Canvas header = new Canvas(0, 0, screen.getWidth(), 3) {
                @Override
                public void paint(Graphics graphics) {
                    // Legacy paint method for compatibility
                    graphics.drawString(0, 0, "=== Console Master Framework Demo ===");
                    graphics.drawString(0, 1, "Welcome to the JLine-powered console!");
                    graphics.drawHorizontalLine(0, getWidth() - 1, 2, '=');
                }

                @Override
                public void paint(JLineGraphics graphics) {
                    // Enhanced JLine implementation with AttributedStyle
                    AttributedStyle titleStyle = AttributedStyle.DEFAULT
                        .foreground(AttributedStyle.BRIGHT + AttributedStyle.YELLOW)
                        .background(AttributedStyle.BLUE)
                        .bold();

                    String title = "=== Console Master Framework Demo ===";
                    int centerX = getWidth() / 2 - title.length() / 2;

                    // Fill background and draw title
                    graphics.setStyle(titleStyle);
                    graphics.fillRect(0, 0, getWidth(), 1, ' ');
                    graphics.drawString(centerX, 0, title);

                    // Subtitle with different style
                    AttributedStyle subtitleStyle = AttributedStyle.DEFAULT
                        .foreground(AttributedStyle.CYAN)
                        .italic();

                    String subtitle = "Welcome to the JLine-powered console!";
                    centerX = getWidth() / 2 - subtitle.length() / 2;
                    graphics.setStyle(subtitleStyle);
                    graphics.drawString(centerX, 1, subtitle);

                    // Border
                    graphics.setStyle(AttributedStyle.DEFAULT.foreground(AttributedStyle.WHITE));
                    graphics.drawHorizontalLine(0, getWidth() - 1, 2, '=');
                }
            };

            // Create colorful game area canvas
            Canvas gameArea = new Canvas(2, 4, screen.getWidth() - 20, 12) {
                @Override
                public void paint(Graphics graphics) {
                    // Legacy implementation
                    graphics.drawRect(0, 0, getWidth(), getHeight(), '#');
                    graphics.drawString(2, 1, "GAME AREA");
                    graphics.drawString(2, 3, "This is where your");
                    graphics.drawString(2, 4, "AWESOME GAME");
                    graphics.drawString(2, 5, "content would be displayed.");
                }

                @Override
                public void paint(JLineGraphics graphics) {
                    // Enhanced JLine implementation
                    graphics.setStyle(AttributedStyle.DEFAULT.foreground(AttributedStyle.GREEN));
                    graphics.drawRect(0, 0, getWidth(), getHeight(), '#');

                    // Game title in bold red
                    graphics.setStyle(AttributedStyle.DEFAULT
                        .foreground(AttributedStyle.BRIGHT + AttributedStyle.RED)
                        .bold());
                    graphics.drawString(2, 1, "GAME AREA");

                    // Game content with various styles
                    graphics.setStyle(AttributedStyle.DEFAULT.foreground(AttributedStyle.WHITE));
                    graphics.drawString(2, 3, "This is where your");

                    graphics.setStyle(AttributedStyle.DEFAULT
                        .foreground(AttributedStyle.BRIGHT + AttributedStyle.GREEN)
                        .bold());
                    graphics.drawString(2, 4, "AWESOME GAME");

                    graphics.setStyle(AttributedStyle.DEFAULT.foreground(AttributedStyle.WHITE));
                    graphics.drawString(2, 5, "content would be displayed.");

                    // Status with colors
                    graphics.setStyle(AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW));
                    graphics.drawString(2, 7, "Status: ");
                    graphics.setStyle(AttributedStyle.DEFAULT
                        .foreground(AttributedStyle.BRIGHT + AttributedStyle.GREEN)
                        .bold());
                    graphics.drawString(10, 7, "READY");

                    // Instructions with underline
                    graphics.setStyle(AttributedStyle.DEFAULT
                        .foreground(AttributedStyle.CYAN)
                        .underline());
                    graphics.drawString(2, 9, "Press any key to continue...");
                }
            };

            // Create info panel with JLine styling
            Canvas infoPanel = new Canvas(screen.getWidth() - 18, 4, 18, 12) {
                @Override
                public void paint(Graphics graphics) {
                    // Legacy implementation
                    graphics.drawRect(0, 0, getWidth(), getHeight(), '|');
                    graphics.drawString(2, 1, "INFO PANEL");
                    graphics.drawString(2, 3, "Score: 1000");
                    graphics.drawString(2, 4, "Level: 5");
                    graphics.drawString(2, 5, "Lives: 3");
                    graphics.drawString(2, 7, "Stats:");
                    graphics.drawString(2, 8, "HP: 100");
                    graphics.drawString(2, 9, "MP: 50");
                }

                @Override
                public void paint(JLineGraphics graphics) {
                    // Enhanced JLine implementation
                    graphics.setStyle(AttributedStyle.DEFAULT.foreground(AttributedStyle.MAGENTA));
                    graphics.drawRect(0, 0, getWidth(), getHeight(), '|');

                    // Panel title
                    graphics.setStyle(AttributedStyle.DEFAULT
                        .foreground(AttributedStyle.BRIGHT + AttributedStyle.MAGENTA)
                        .bold());
                    graphics.drawString(2, 1, "INFO PANEL");

                    // Colored statistics
                    graphics.setStyle(AttributedStyle.DEFAULT.foreground(AttributedStyle.BRIGHT + AttributedStyle.YELLOW));
                    graphics.drawString(2, 3, "Score: ");
                    graphics.setBold(true);
                    graphics.drawString(9, 3, "1000");

                    graphics.setStyle(AttributedStyle.DEFAULT.foreground(AttributedStyle.BRIGHT + AttributedStyle.BLUE));
                    graphics.drawString(2, 4, "Level: ");
                    graphics.setBold(true);
                    graphics.drawString(9, 4, "5");

                    graphics.setStyle(AttributedStyle.DEFAULT.foreground(AttributedStyle.BRIGHT + AttributedStyle.RED));
                    graphics.drawString(2, 5, "Lives: ");
                    graphics.setBold(true);
                    graphics.drawString(9, 5, "3");

                    // Stats section
                    graphics.setStyle(AttributedStyle.DEFAULT
                        .foreground(AttributedStyle.WHITE)
                        .underline());
                    graphics.drawString(2, 7, "Stats:");

                    graphics.setStyle(AttributedStyle.DEFAULT.foreground(AttributedStyle.GREEN));
                    graphics.drawString(2, 8, "HP: 100");
                    graphics.setStyle(AttributedStyle.DEFAULT.foreground(AttributedStyle.BLUE));
                    graphics.drawString(2, 9, "MP: 50");
                }
            };

            // Create footer with mixed styling
            Canvas footer = new Canvas(0, screen.getHeight() - 3, screen.getWidth(), 3) {
                @Override
                public void paint(Graphics graphics) {
                    // Legacy implementation
                    graphics.drawHorizontalLine(0, getWidth() - 1, 0, '-');
                    graphics.drawString(1, 1, "Status: Ready | Controls: WASD to move, SPACE to action");
                }

                @Override
                public void paint(JLineGraphics graphics) {
                    // Enhanced JLine implementation
                    graphics.setStyle(AttributedStyle.DEFAULT.foreground(AttributedStyle.WHITE));
                    graphics.drawHorizontalLine(0, getWidth() - 1, 0, '-');

                    // Status with mixed colors
                    graphics.drawString(1, 1, "Status: ");
                    graphics.setStyle(AttributedStyle.DEFAULT
                        .foreground(AttributedStyle.BRIGHT + AttributedStyle.GREEN)
                        .bold());
                    graphics.drawString(9, 1, "Ready");

                    graphics.setStyle(AttributedStyle.DEFAULT.foreground(AttributedStyle.WHITE));
                    graphics.drawString(16, 1, " | Controls: ");
                    graphics.setStyle(AttributedStyle.DEFAULT
                        .foreground(AttributedStyle.YELLOW)
                        .bold());
                    graphics.drawString(28, 1, "WASD");
                    graphics.setStyle(AttributedStyle.DEFAULT.foreground(AttributedStyle.WHITE));
                    graphics.drawString(33, 1, " to move, ");
                    graphics.setStyle(AttributedStyle.DEFAULT
                        .foreground(AttributedStyle.YELLOW)
                        .bold());
                    graphics.drawString(43, 1, "SPACE");
                    graphics.setStyle(AttributedStyle.DEFAULT.foreground(AttributedStyle.WHITE));
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
            System.out.println("\nJLine-enhanced demo rendered! Check your console for improved ANSI styling.");
            System.out.println("This version uses JLine's AttributedStyle for better performance and compatibility.");
            System.out.println("Press Enter to exit...");
            System.in.read();

            // Clean up
            screen.close();

        } catch (IOException e) {
            System.err.println("Error running JLine demo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
