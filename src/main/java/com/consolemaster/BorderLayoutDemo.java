package com.consolemaster;

import java.io.IOException;

/**
 * Demo application showcasing the BorderLayout system using modern Box and Text components.
 * Creates components in all five border layout regions: NORTH, SOUTH, EAST, WEST, and CENTER.
 */
public class BorderLayoutDemo {

    public static void main(String[] args) {
        try {
            // Create the main screen canvas
            ScreenCanvas screen = new ScreenCanvas(80, 24);

            // Create a composite canvas with BorderLayout
            CompositeCanvas borderContainer = new CompositeCanvas(2, 2,
                                                                 screen.getWidth() - 4,
                                                                 screen.getHeight() - 4,
                                                                 new BorderLayout(1));

            // Create NORTH component (Header)
            Box northBox = new Box(0, 0, 0, 3, new SimpleBorder());
            Text northText = new Text(0, 0, 0, 0, "NORTH - Header Region", Text.Alignment.CENTER);
            northText.setForegroundColor(AnsiColor.BRIGHT_CYAN);
            northText.setBold(true);
            northBox.setChild(northText);
            northBox.setLayoutConstraint(new PositionConstraint(PositionConstraint.Position.TOP_CENTER));
            borderContainer.addChild(northBox);

            // Create SOUTH component (Footer)
            Box southBox = new Box(0, 0, 0, 3, new SimpleBorder());
            Text southText = new Text(0, 0, 0, 0, "SOUTH - Footer Region", Text.Alignment.CENTER);
            southText.setForegroundColor(AnsiColor.BRIGHT_YELLOW);
            southText.setBold(true);
            southBox.setChild(southText);
            southBox.setLayoutConstraint(new PositionConstraint(PositionConstraint.Position.BOTTOM_CENTER));
            borderContainer.addChild(southBox);

            // Create WEST component (Left Sidebar)
            Box westBox = new Box(0, 0, 15, 0, new SimpleBorder());
            Text westText = new Text(0, 0, 0, 0, "WEST\nLeft\nSidebar\nMenu", Text.Alignment.CENTER);
            westText.setForegroundColor(AnsiColor.BRIGHT_GREEN);
            westText.setBold(true);
            westBox.setChild(westText);
            westBox.setLayoutConstraint(new PositionConstraint(PositionConstraint.Position.CENTER_LEFT));
            borderContainer.addChild(westBox);

            // Create EAST component (Right Sidebar)
            Box eastBox = new Box(0, 0, 15, 0, new SimpleBorder());
            Text eastText = new Text(0, 0, 0, 0, "EAST\nRight\nSidebar\nInfo", Text.Alignment.CENTER);
            eastText.setForegroundColor(AnsiColor.BRIGHT_MAGENTA);
            eastText.setBold(true);
            eastBox.setChild(eastText);
            eastBox.setLayoutConstraint(new PositionConstraint(PositionConstraint.Position.CENTER_RIGHT));
            borderContainer.addChild(eastBox);

            // Create CENTER component (Main Content)
            Box centerBox = new Box(0, 0, 0, 0, new ThickBorder());
            Text centerText = new Text(0, 0, 0, 0,
                "CENTER - Main Content Area\n\n" +
                "This is the main content region\n" +
                "of the BorderLayout demo.\n\n" +
                "BorderLayout automatically\n" +
                "arranges components in five\n" +
                "distinct regions:\n\n" +
                "• NORTH (top)\n" +
                "• SOUTH (bottom)\n" +
                "• WEST (left)\n" +
                "• EAST (right)\n" +
                "• CENTER (middle)\n\n" +
                "Each region uses Box and Text\n" +
                "components for modern styling.",
                Text.Alignment.CENTER);
            centerText.setForegroundColor(AnsiColor.WHITE);
            centerBox.setChild(centerText);
            centerBox.setLayoutConstraint(new PositionConstraint(PositionConstraint.Position.CENTER));
            borderContainer.addChild(centerBox);

            // Create title using Text component
            Text title = new Text(0, 0, screen.getWidth(), 1,
                                "BorderLayout Demo - Modern Box and Text Components",
                                Text.Alignment.CENTER);
            title.setForegroundColor(AnsiColor.BRIGHT_WHITE);
            title.setBold(true);
            title.setBackgroundColor(AnsiColor.BLUE);

            // Create main container
            CompositeCanvas mainContent = new CompositeCanvas(0, 0,
                                                             screen.getWidth(),
                                                             screen.getHeight());
            mainContent.addChild(title);
            mainContent.addChild(borderContainer);

            // Set the content canvas
            screen.setContentCanvas(mainContent);

            // Render the screen
            screen.render();

            System.out.println("\nModern BorderLayout Demo rendered! Features:");
            System.out.println("- BorderLayout arranges components in 5 distinct regions");
            System.out.println("- Each region uses Box with SimpleBorder or ThickBorder");
            System.out.println("- Text components provide styled content with alignment");
            System.out.println("- Demonstrates integration of BorderLayout, Box, and Text systems");
            System.out.println("- Automatic sizing and positioning of all regions");
            System.out.println("Demo completed successfully.");

            // Clean up
            screen.close();

        } catch (IOException e) {
            System.err.println("Error running BorderLayout demo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
