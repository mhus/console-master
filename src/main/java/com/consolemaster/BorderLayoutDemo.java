package com.consolemaster;

import org.jline.utils.AttributedStyle;
import java.io.IOException;

/**
 * Demo application showcasing the BorderLayout system with PositionConstraints.
 * Demonstrates how components are arranged in the five border regions: NORTH, SOUTH, EAST, WEST, and CENTER.
 */
public class BorderLayoutDemo {

    public static void main(String[] args) {
        try {
            // Create the main screen canvas with appropriate minimum size for border layout
            ScreenCanvas screen = new ScreenCanvas(60, 20);

            // Create a composite canvas with BorderLayout
            CompositeCanvas borderContainer = new CompositeCanvas(1, 1,
                                                                 screen.getWidth() - 2,
                                                                 screen.getHeight() - 2,
                                                                 new BorderLayout(1));

            // Create NORTH component (Header)
            Canvas northPanel = new Canvas(0, 0, 0, 3) { // Size will be set by layout
                @Override
                public void paint(Graphics graphics) {
                    // Legacy implementation
                    graphics.drawRect(0, 0, getWidth(), getHeight(), '=');
                    graphics.drawString(2, 1, "NORTH - Header Area");
                }

                @Override
                public void paint(JLineGraphics graphics) {
                    // Enhanced JLine implementation
                    AttributedStyle headerStyle = AttributedStyle.DEFAULT
                        .foreground(AttributedStyle.BRIGHT + AttributedStyle.YELLOW)
                        .background(AttributedStyle.BLUE)
                        .bold();

                    // Draw border
                    graphics.setStyle(AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW));
                    graphics.drawRect(getX(), getY(), getWidth(), getHeight(), '=');

                    // Draw header text
                    graphics.setStyle(headerStyle);
                    String title = "NORTH - Header Area";
                    int centerX = getX() + (getWidth() / 2) - (title.length() / 2);
                    graphics.drawString(centerX, getY() + 1, title);
                }
            };

            // Create SOUTH component (Footer)
            Canvas southPanel = new Canvas(0, 0, 0, 2) { // Size will be set by layout
                @Override
                public void paint(Graphics graphics) {
                    // Legacy implementation
                    graphics.drawRect(0, 0, getWidth(), getHeight(), '-');
                    graphics.drawString(2, 0, "SOUTH - Footer Area");
                }

                @Override
                public void paint(JLineGraphics graphics) {
                    // Enhanced JLine implementation
                    AttributedStyle footerStyle = AttributedStyle.DEFAULT
                        .foreground(AttributedStyle.BRIGHT + AttributedStyle.GREEN)
                        .bold();

                    // Draw border
                    graphics.setStyle(AttributedStyle.DEFAULT.foreground(AttributedStyle.GREEN));
                    graphics.drawRect(getX(), getY(), getWidth(), getHeight(), '-');

                    // Draw footer text
                    graphics.setStyle(footerStyle);
                    String status = "SOUTH - Footer Area | Status: Ready";
                    graphics.drawString(getX() + 2, getY() + 0, status);
                }
            };

            // Create WEST component (Sidebar)
            Canvas westPanel = new Canvas(0, 0, 15, 0) { // Size will be set by layout
                @Override
                public void paint(Graphics graphics) {
                    // Legacy implementation
                    graphics.drawRect(0, 0, getWidth(), getHeight(), '|');
                    graphics.drawString(2, 1, "WEST");
                    graphics.drawString(2, 2, "Sidebar");
                    graphics.drawString(2, 4, "Menu:");
                    graphics.drawString(2, 5, "- Item 1");
                    graphics.drawString(2, 6, "- Item 2");
                    graphics.drawString(2, 7, "- Item 3");
                }

                @Override
                public void paint(JLineGraphics graphics) {
                    // Enhanced JLine implementation
                    AttributedStyle sidebarStyle = AttributedStyle.DEFAULT
                        .foreground(AttributedStyle.BRIGHT + AttributedStyle.MAGENTA)
                        .bold();

                    // Draw border
                    graphics.setStyle(AttributedStyle.DEFAULT.foreground(AttributedStyle.MAGENTA));
                    graphics.drawRect(getX(), getY(), getWidth(), getHeight(), '|');

                    // Draw sidebar content
                    graphics.setStyle(sidebarStyle);
                    graphics.drawString(getX() + 2, getY() + 1, "WEST");
                    graphics.drawString(getX() + 2, getY() + 2, "Sidebar");

                    graphics.setStyle(AttributedStyle.DEFAULT.foreground(AttributedStyle.WHITE).underline());
                    graphics.drawString(getX() + 2, getY() + 4, "Menu:");

                    graphics.setStyle(AttributedStyle.DEFAULT.foreground(AttributedStyle.CYAN));
                    graphics.drawString(getX() + 2, getY() + 5, "- Item 1");
                    graphics.drawString(getX() + 2, getY() + 6, "- Item 2");
                    graphics.drawString(getX() + 2, getY() + 7, "- Item 3");
                }
            };

            // Create EAST component (Tools)
            Canvas eastPanel = new Canvas(0, 0, 12, 0) { // Size will be set by layout
                @Override
                public void paint(Graphics graphics) {
                    // Legacy implementation
                    graphics.drawRect(0, 0, getWidth(), getHeight(), '|');
                    graphics.drawString(2, 1, "EAST");
                    graphics.drawString(2, 2, "Tools");
                    graphics.drawString(2, 4, "Stats:");
                    graphics.drawString(2, 5, "FPS: 60");
                    graphics.drawString(2, 6, "Mem: 45%");
                }

                @Override
                public void paint(JLineGraphics graphics) {
                    // Enhanced JLine implementation
                    AttributedStyle toolsStyle = AttributedStyle.DEFAULT
                        .foreground(AttributedStyle.BRIGHT + AttributedStyle.CYAN)
                        .bold();

                    // Draw border
                    graphics.setStyle(AttributedStyle.DEFAULT.foreground(AttributedStyle.CYAN));
                    graphics.drawRect(getX(), getY(), getWidth(), getHeight(), '|');

                    // Draw tools content
                    graphics.setStyle(toolsStyle);
                    graphics.drawString(getX() + 2, getY() + 1, "EAST");
                    graphics.drawString(getX() + 2, getY() + 2, "Tools");

                    graphics.setStyle(AttributedStyle.DEFAULT.foreground(AttributedStyle.WHITE).underline());
                    graphics.drawString(getX() + 2, getY() + 4, "Stats:");

                    graphics.setStyle(AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW));
                    graphics.drawString(getX() + 2, getY() + 5, "FPS: 60");
                    graphics.setStyle(AttributedStyle.DEFAULT.foreground(AttributedStyle.RED));
                    graphics.drawString(getX() + 2, getY() + 6, "Mem: 45%");
                }
            };

            // Create CENTER component (Main content)
            Canvas centerPanel = new Canvas(0, 0, 0, 0) { // Size will be set by layout
                @Override
                public void paint(Graphics graphics) {
                    // Legacy implementation
                    graphics.drawRect(0, 0, getWidth(), getHeight(), '#');
                    graphics.drawString(2, 1, "CENTER - Main Content Area");
                    graphics.drawString(2, 3, "This is the main game area where");
                    graphics.drawString(2, 4, "your content would be displayed.");
                    graphics.drawString(2, 6, "The BorderLayout automatically");
                    graphics.drawString(2, 7, "arranges all components around");
                    graphics.drawString(2, 8, "this central area.");
                }

                @Override
                public void paint(JLineGraphics graphics) {
                    // Enhanced JLine implementation
                    AttributedStyle centerStyle = AttributedStyle.DEFAULT
                        .foreground(AttributedStyle.BRIGHT + AttributedStyle.WHITE)
                        .bold();

                    // Draw border
                    graphics.setStyle(AttributedStyle.DEFAULT.foreground(AttributedStyle.WHITE));
                    graphics.drawRect(getX(), getY(), getWidth(), getHeight(), '#');

                    // Draw center content
                    graphics.setStyle(centerStyle);
                    graphics.drawString(getX() + 2, getY() + 1, "CENTER - Main Content Area");

                    graphics.setStyle(AttributedStyle.DEFAULT.foreground(AttributedStyle.WHITE));
                    graphics.drawString(getX() + 2, getY() + 3, "This is the main game area where");
                    graphics.drawString(getX() + 2, getY() + 4, "your content would be displayed.");

                    graphics.setStyle(AttributedStyle.DEFAULT.foreground(AttributedStyle.GREEN));
                    graphics.drawString(getX() + 2, getY() + 6, "The BorderLayout automatically");
                    graphics.drawString(getX() + 2, getY() + 7, "arranges all components around");
                    graphics.drawString(getX() + 2, getY() + 8, "this central area.");

                    // Add some decorative elements
                    graphics.setStyle(AttributedStyle.DEFAULT.foreground(AttributedStyle.BRIGHT + AttributedStyle.BLUE));
                    graphics.drawString(getX() + getWidth() - 10, getY() + getHeight() - 2, "* * *");
                }
            };

            // Add components to border layout with appropriate position constraints
            borderContainer.addChild(northPanel, new PositionConstraint(PositionConstraint.Position.TOP_CENTER));
            borderContainer.addChild(southPanel, new PositionConstraint(PositionConstraint.Position.BOTTOM_CENTER));
            borderContainer.addChild(westPanel, new PositionConstraint(PositionConstraint.Position.CENTER_LEFT));
            borderContainer.addChild(eastPanel, new PositionConstraint(PositionConstraint.Position.CENTER_RIGHT));
            borderContainer.addChild(centerPanel, new PositionConstraint(PositionConstraint.Position.CENTER));

            // Create main container with demo title
            CompositeCanvas mainContent = new CompositeCanvas(0, 0,
                                                             screen.getWidth(),
                                                             screen.getHeight());

            // Create demo title
            Canvas titleCanvas = new Canvas(0, 0, screen.getWidth(), 1) {
                @Override
                public void paint(Graphics graphics) {
                    graphics.drawString(0, 0, "BorderLayout Demo - Five regions: NORTH, SOUTH, EAST, WEST, CENTER");
                }

                @Override
                public void paint(JLineGraphics graphics) {
                    AttributedStyle titleStyle = AttributedStyle.DEFAULT
                        .foreground(AttributedStyle.BRIGHT + AttributedStyle.WHITE)
                        .background(AttributedStyle.BLACK)
                        .bold();

                    graphics.setStyle(titleStyle);
                    String title = "BorderLayout Demo - Five regions: NORTH, SOUTH, EAST, WEST, CENTER";
                    graphics.drawString(0, 0, title);
                }
            };

            mainContent.addChild(titleCanvas);
            mainContent.addChild(borderContainer);

            // Set the content canvas
            screen.setContentCanvas(mainContent);

            // Render the screen
            screen.render();

            System.out.println("\nBorderLayout Demo rendered! Each region is clearly marked:");
            System.out.println("- NORTH (yellow): Header area at the top");
            System.out.println("- SOUTH (green): Footer area at the bottom");
            System.out.println("- WEST (magenta): Left sidebar with menu");
            System.out.println("- EAST (cyan): Right panel with tools/stats");
            System.out.println("- CENTER (white): Main content area in the middle");
            System.out.println("Demo completed successfully.");

            // Clean up
            screen.close();

        } catch (IOException e) {
            System.err.println("Error running BorderLayout demo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
