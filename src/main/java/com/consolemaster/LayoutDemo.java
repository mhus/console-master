package com.consolemaster;

import org.jline.utils.AttributedStyle;
import java.io.IOException;

/**
 * Demo application showcasing the Layout system with FlowLayout.
 * Creates multiple small canvases that are automatically arranged by the layout manager.
 */
public class LayoutDemo {

    public static void main(String[] args) {
        try {
            // Create the main screen canvas with a smaller minimum size for this demo
            ScreenCanvas screen = new ScreenCanvas(40, 15);

            // Create a composite canvas with FlowLayout
            CompositeCanvas flowContainer = new CompositeCanvas(2, 2,
                                                               screen.getWidth() - 4,
                                                               screen.getHeight() - 4,
                                                               new FlowLayout(1, 1));

            // Create several small canvases to demonstrate flow layout
            for (int i = 1; i <= 8; i++) {
                final int boxNumber = i;
                Canvas box = new Canvas(0, 0, 8, 3) { // Position will be set by layout
                    @Override
                    public void paint(Graphics graphics) {
                        // Legacy implementation
                        graphics.drawRect(0, 0, getWidth(), getHeight(), '#');
                        graphics.drawString(2, 1, "Box" + boxNumber);
                    }

                    @Override
                    public void paint(JLineGraphics graphics) {
                        // Enhanced JLine implementation with colors
                        AttributedStyle boxStyle = AttributedStyle.DEFAULT
                            .foreground(AttributedStyle.BRIGHT + (boxNumber % 6 + 1)) // Cycle through colors
                            .bold();

                        graphics.setStyle(boxStyle);
                        graphics.drawRect(getX(), getY(), getWidth(), getHeight(), '#');

                        AttributedStyle textStyle = AttributedStyle.DEFAULT
                            .foreground(AttributedStyle.WHITE)
                            .bold();
                        graphics.setStyle(textStyle);
                        graphics.drawString(getX() + 2, getY() + 1, "Box" + boxNumber);
                    }
                };

                flowContainer.addChild(box);
            }

            // Create header showing layout info
            Canvas header = new Canvas(0, 0, screen.getWidth(), 2) {
                @Override
                public void paint(Graphics graphics) {
                    graphics.drawString(0, 0, "Layout Demo - FlowLayout arranges children automatically");
                    graphics.drawHorizontalLine(0, getWidth() - 1, 1, '=');
                }

                @Override
                public void paint(JLineGraphics graphics) {
                    AttributedStyle titleStyle = AttributedStyle.DEFAULT
                        .foreground(AttributedStyle.BRIGHT + AttributedStyle.CYAN)
                        .bold();

                    graphics.setStyle(titleStyle);
                    graphics.drawString(0, 0, "Layout Demo - FlowLayout arranges children automatically");

                    graphics.setStyle(AttributedStyle.DEFAULT.foreground(AttributedStyle.WHITE));
                    graphics.drawHorizontalLine(0, getWidth() - 1, 1, '=');
                }
            };

            // Create main container
            CompositeCanvas mainContent = new CompositeCanvas(0, 0,
                                                             screen.getWidth(),
                                                             screen.getHeight());
            mainContent.addChild(header);
            mainContent.addChild(flowContainer);

            // Set the content canvas
            screen.setContentCanvas(mainContent);

            // Render the screen
            screen.render();

            System.out.println("\nLayout Demo rendered! The FlowLayout automatically arranged the boxes.");
            System.out.println("Notice how boxes flow to the next row when the current row is full.");
            System.out.println("Demo completed successfully.");

            // Clean up
            screen.close();

        } catch (IOException e) {
            System.err.println("Error running Layout demo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
