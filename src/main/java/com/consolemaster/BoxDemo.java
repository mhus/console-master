package com.consolemaster;

import org.jline.utils.AttributedStyle;
import java.io.IOException;

/**
 * Demo application showcasing the Box component with different Border implementations.
 * Demonstrates how borders wrap around child content and handle various styling options.
 */
public class BoxDemo {

    public static void main(String[] args) {
        try {
            // Create the main screen canvas
            ScreenCanvas screen = new ScreenCanvas(70, 25);

            // Create main container
            CompositeCanvas mainContent = new CompositeCanvas(0, 0,
                                                             screen.getWidth(),
                                                             screen.getHeight());

            // Create title
            Canvas title = new Canvas(0, 0, screen.getWidth(), 2) {
                @Override
                public void paint(Graphics graphics) {
                    graphics.drawString(0, 0, "Box Component Demo - Borders with Child Content");
                    graphics.drawHorizontalLine(0, getWidth() - 1, 1, '=');
                }

                @Override
                public void paint(JLineGraphics graphics) {
                    AttributedStyle titleStyle = AttributedStyle.DEFAULT
                        .foreground(AttributedStyle.BRIGHT + AttributedStyle.CYAN)
                        .bold();

                    graphics.setStyle(titleStyle);
                    graphics.drawString(0, 0, "Box Component Demo - Borders with Child Content");

                    graphics.setStyle(AttributedStyle.DEFAULT.foreground(AttributedStyle.WHITE));
                    graphics.drawHorizontalLine(0, getWidth() - 1, 1, '=');
                }
            };

            // Create Box 1: Simple Border with TextCanvas
            Box simpleBox = new Box(5, 3, 25, 8, new SimpleBorder());
            Canvas textContent1 = new Canvas(0, 0, 0, 0) { // Size will be set by Box
                @Override
                public void paint(Graphics graphics) {
                    graphics.drawString(1, 1, "Simple Border");
                    graphics.drawString(1, 2, "with TextCanvas");
                    graphics.drawString(1, 4, "This content is");
                    graphics.drawString(1, 5, "inside a box!");
                }

                @Override
                public void paint(JLineGraphics graphics) {
                    graphics.setStyle(AttributedStyle.DEFAULT.foreground(AttributedStyle.GREEN).bold());
                    graphics.drawString(1, 1, "Simple Border");
                    graphics.setStyle(AttributedStyle.DEFAULT.foreground(AttributedStyle.WHITE));
                    graphics.drawString(1, 2, "with TextCanvas");
                    graphics.drawString(1, 4, "This content is");
                    graphics.setStyle(AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW));
                    graphics.drawString(1, 5, "inside a box!");
                }
            };
            simpleBox.setChild(textContent1);

            // Create Box 2: Thick Border with styled content
            AttributedStyle redBorderStyle = AttributedStyle.DEFAULT
                .foreground(AttributedStyle.BRIGHT + AttributedStyle.RED)
                .bold();
            Box thickBox = new Box(35, 3, 30, 8, new ThickBorder(redBorderStyle));
            Canvas gameContent = new Canvas(0, 0, 0, 0) {
                @Override
                public void paint(Graphics graphics) {
                    graphics.drawString(2, 1, "THICK BORDER BOX");
                    graphics.drawString(2, 2, "Player Stats:");
                    graphics.drawString(2, 3, "Health: 100/100");
                    graphics.drawString(2, 4, "Mana:   85/100");
                    graphics.drawString(2, 5, "Level:  42");
                }

                @Override
                public void paint(JLineGraphics graphics) {
                    graphics.setStyle(AttributedStyle.DEFAULT.foreground(AttributedStyle.BRIGHT + AttributedStyle.MAGENTA).bold());
                    graphics.drawString(2, 1, "THICK BORDER BOX");

                    graphics.setStyle(AttributedStyle.DEFAULT.foreground(AttributedStyle.WHITE).underline());
                    graphics.drawString(2, 2, "Player Stats:");

                    graphics.setStyle(AttributedStyle.DEFAULT.foreground(AttributedStyle.BRIGHT + AttributedStyle.GREEN));
                    graphics.drawString(2, 3, "Health: 100/100");
                    graphics.setStyle(AttributedStyle.DEFAULT.foreground(AttributedStyle.BRIGHT + AttributedStyle.BLUE));
                    graphics.drawString(2, 4, "Mana:   85/100");
                    graphics.setStyle(AttributedStyle.DEFAULT.foreground(AttributedStyle.BRIGHT + AttributedStyle.YELLOW));
                    graphics.drawString(2, 5, "Level:  42");
                }
            };
            thickBox.setChild(gameContent);

            // Create Box 3: Custom styled border
            AttributedStyle blueBorderStyle = AttributedStyle.DEFAULT
                .foreground(AttributedStyle.BRIGHT + AttributedStyle.BLUE)
                .bold();
            Box customBox = new Box(5, 13, 35, 10, new SimpleBorder('*', '*', '*', blueBorderStyle));
            Canvas inventoryContent = new Canvas(0, 0, 0, 0) {
                @Override
                public void paint(Graphics graphics) {
                    graphics.drawString(2, 1, "Custom Border (* chars)");
                    graphics.drawString(2, 2, "Inventory:");
                    graphics.drawString(2, 3, "[1] Iron Sword");
                    graphics.drawString(2, 4, "[2] Health Potion x3");
                    graphics.drawString(2, 5, "[3] Magic Shield");
                    graphics.drawString(2, 6, "[4] Gold: 1,250");
                    graphics.drawString(2, 7, "[5] Empty slot");
                }

                @Override
                public void paint(JLineGraphics graphics) {
                    graphics.setStyle(AttributedStyle.DEFAULT.foreground(AttributedStyle.BRIGHT + AttributedStyle.CYAN).bold());
                    graphics.drawString(2, 1, "Custom Border (* chars)");

                    graphics.setStyle(AttributedStyle.DEFAULT.foreground(AttributedStyle.WHITE).underline());
                    graphics.drawString(2, 2, "Inventory:");

                    graphics.setStyle(AttributedStyle.DEFAULT.foreground(AttributedStyle.WHITE));
                    graphics.drawString(2, 3, "[1] ");
                    graphics.setStyle(AttributedStyle.DEFAULT.foreground(AttributedStyle.BRIGHT + AttributedStyle.WHITE));
                    graphics.drawString(6, 3, "Iron Sword");

                    graphics.setStyle(AttributedStyle.DEFAULT.foreground(AttributedStyle.WHITE));
                    graphics.drawString(2, 4, "[2] ");
                    graphics.setStyle(AttributedStyle.DEFAULT.foreground(AttributedStyle.BRIGHT + AttributedStyle.RED));
                    graphics.drawString(6, 4, "Health Potion x3");

                    graphics.setStyle(AttributedStyle.DEFAULT.foreground(AttributedStyle.WHITE));
                    graphics.drawString(2, 5, "[3] ");
                    graphics.setStyle(AttributedStyle.DEFAULT.foreground(AttributedStyle.BRIGHT + AttributedStyle.BLUE));
                    graphics.drawString(6, 5, "Magic Shield");

                    graphics.setStyle(AttributedStyle.DEFAULT.foreground(AttributedStyle.WHITE));
                    graphics.drawString(2, 6, "[4] ");
                    graphics.setStyle(AttributedStyle.DEFAULT.foreground(AttributedStyle.BRIGHT + AttributedStyle.YELLOW));
                    graphics.drawString(6, 6, "Gold: 1,250");

                    graphics.setStyle(AttributedStyle.DEFAULT.foreground(AttributedStyle.WHITE));
                    graphics.drawString(2, 7, "[5] ");
                    graphics.setStyle(AttributedStyle.DEFAULT.foreground(AttributedStyle.BRIGHT + AttributedStyle.BLACK));
                    graphics.drawString(6, 7, "Empty slot");
                }
            };
            customBox.setChild(inventoryContent);

            // Create Box 4: Nested Box demonstration
            Box outerBox = new Box(45, 13, 20, 10, new SimpleBorder('-', '|', '+'));
            Box innerBox = new Box(2, 2, 14, 6, new SimpleBorder('=', '!', '#'));
            Canvas nestedContent = new Canvas(0, 0, 0, 0) {
                @Override
                public void paint(Graphics graphics) {
                    graphics.drawString(1, 1, "Nested");
                    graphics.drawString(1, 2, "Box Demo");
                }

                @Override
                public void paint(JLineGraphics graphics) {
                    graphics.setStyle(AttributedStyle.DEFAULT.foreground(AttributedStyle.BRIGHT + AttributedStyle.GREEN).bold());
                    graphics.drawString(1, 1, "Nested");
                    graphics.drawString(1, 2, "Box Demo");
                }
            };
            innerBox.setChild(nestedContent);
            outerBox.setChild(innerBox);

            // Add all components to main content
            mainContent.addChild(title);
            mainContent.addChild(simpleBox);
            mainContent.addChild(thickBox);
            mainContent.addChild(customBox);
            mainContent.addChild(outerBox);

            // Set the content canvas
            screen.setContentCanvas(mainContent);

            // Render the screen
            screen.render();

            System.out.println("\nBox Component Demo rendered! Features demonstrated:");
            System.out.println("- Simple Border: Basic '-', '|', '+' border characters");
            System.out.println("- Thick Border: Bold '#' characters with red styling");
            System.out.println("- Custom Border: Custom '*' characters with blue styling");
            System.out.println("- Nested Boxes: Box containing another Box");
            System.out.println("- Automatic child positioning within border inner area");
            System.out.println("- Different content types with various ANSI styling");
            System.out.println("Demo completed successfully.");

            // Clean up
            screen.close();

        } catch (IOException e) {
            System.err.println("Error running Box demo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
