package com.consolemaster.demo;

import com.consolemaster.AnsiColor;
import com.consolemaster.BorderLayout;
import com.consolemaster.Box;
import com.consolemaster.Composite;
import com.consolemaster.DefaultBorder;
import com.consolemaster.FlowLayout;
import com.consolemaster.NoLayout;
import com.consolemaster.PositionConstraint;
import com.consolemaster.ProcessLoop;
import com.consolemaster.ScreenCanvas;
import com.consolemaster.ScrollerCanvas;
import com.consolemaster.Text;

import java.io.IOException;

/**
 * Demo application showcasing the ScrollerCanvas functionality.
 * Creates a large content area within a smaller viewport with scrolling capabilities.
 */
public class ScrollerDemo {

    public static void main(String[] args) throws IOException {
        new ScrollerDemo().run();
    }

    public void run() throws IOException {
        // Create main screen
        ScreenCanvas screen = new ScreenCanvas(80, 25);

        // Create main layout
        Composite mainContainer = new Composite("mainContainer", 80, 25, new BorderLayout(1));

        // Create header
        Box headerBox = new Box("headerBox", 0, 3, new DefaultBorder());
        Text headerText = new Text("headerText", 0, 0,
            "ScrollerCanvas Demo - Use Arrow Keys, Page Up/Down, Mouse Wheel, Click Scrollbars",
            Text.Alignment.CENTER);
        headerText.setForegroundColor(AnsiColor.BRIGHT_CYAN);
        headerText.setBold(true);
        headerBox.setContent(headerText);
        headerBox.setLayoutConstraint(new PositionConstraint(PositionConstraint.Position.TOP_CENTER));

        // Create footer with instructions
        Box footerBox = new Box("footerBox", 0, 4, new DefaultBorder());
        Composite footerContent = new Composite("footerContent", 0, 0, new FlowLayout(0, 0));

        String[] instructions = {
            "Controls: ← → ↑ ↓ (scroll)  |  Page Up/Down (page scroll)  |  Home/End (horizontal)",
            "Mouse: Wheel (vertical)  |  Shift+Wheel (horizontal)  |  Click scrollbars (jump)",
            "Shortcuts: Ctrl+Home (top-left)  |  Ctrl+End (bottom-right)  |  ESC (exit)"
        };

        for (String instruction : instructions) {
            Text instructionText = new Text("instructionText", 0, 1, instruction, Text.Alignment.CENTER);
            instructionText.setForegroundColor(AnsiColor.YELLOW);
            footerContent.addChild(instructionText);
        }

        footerBox.setContent(footerContent);
        footerBox.setLayoutConstraint(new PositionConstraint(PositionConstraint.Position.BOTTOM_CENTER));

        // Create large content canvas (much larger than viewport)
        Composite largeContent = createLargeContent();

        // Create scroller canvas with smaller viewport
        ScrollerCanvas scroller = new ScrollerCanvas("scroller", 0, 0, 60, 15, largeContent);
        scroller.setScrollEnabled(true, true); // Enable both horizontal and vertical scrolling
        scroller.setScrollStep(2, 1); // Customize scroll steps
        scroller.setMouseScrollStep(3, 2); // Customize mouse scroll steps
        scroller.setScrollbarsVisible(true, true); // Show both scrollbars

        // Wrap scroller in a box for visual distinction
        Box scrollerBox = new Box("scrollerBox", 0, 0, new DefaultBorder());
        scrollerBox.setContent(scroller);
        scrollerBox.setLayoutConstraint(new PositionConstraint(PositionConstraint.Position.CENTER));

        // Add all components
        mainContainer.addChild(headerBox);
        mainContainer.addChild(scrollerBox);
        mainContainer.addChild(footerBox);

        // Set up screen
        screen.setContent(mainContainer);
        screen.pack();

        // Create process loop
        ProcessLoop processLoop = new ProcessLoop(screen);
        processLoop.setTargetFPS(30);

        // Register shortcuts
        screen.registerShortcut("ESC", () -> {
            try {
                processLoop.stop();
            } catch (IOException e) {
                System.err.println("Error stopping process loop: " + e.getMessage());
            }
        });
        screen.registerShortcut("Ctrl+Q", () -> {
            try {
                processLoop.stop();
            } catch (IOException e) {
                System.err.println("Error stopping process loop: " + e.getMessage());
            }
        });

        // Set initial focus on scroller
        scroller.setCanFocus(true);
        screen.setContent(mainContainer);
        screen.focusFirst();

        // Start the demo
        processLoop.start();
    }

    /**
     * Creates a large content canvas that's bigger than the viewport.
     */
    private Composite createLargeContent() {
        // Create a 120x40 content area (larger than 60x15 viewport)
        Composite content = new Composite("largeContent", 120, 40, NoLayout.INSTANCE);

        // Add grid pattern for visual reference
        for (int y = 0; y < 40; y += 2) {
            for (int x = 0; x < 120; x += 10) {
                Text gridText = new Text("gridText_" + x + "_" + y, 8, 1,
                    String.format("(%d,%d)", x, y), Text.Alignment.LEFT);
                gridText.setForegroundColor(AnsiColor.BRIGHT_BLACK);
                content.addChild(gridText);
            }
        }

        // Add some colored content areas
        addColoredArea(content, 10, 5, 20, 8, "Red Area", AnsiColor.RED, AnsiColor.WHITE);
        addColoredArea(content, 40, 10, 25, 6, "Blue Area", AnsiColor.BLUE, AnsiColor.BRIGHT_WHITE);
        addColoredArea(content, 70, 15, 30, 10, "Green Area", AnsiColor.GREEN, AnsiColor.BLACK);
        addColoredArea(content, 20, 25, 35, 8, "Yellow Area", AnsiColor.YELLOW, AnsiColor.BLACK);
        addColoredArea(content, 80, 30, 25, 6, "Magenta Area", AnsiColor.MAGENTA, AnsiColor.WHITE);

        // Add border around entire content
        addBorderArea(content, 0, 0, 120, 40, "Large Content Border", AnsiColor.CYAN);

        // Add special markers at corners
        addCornerMarker(content, 0, 0, "TOP-LEFT", AnsiColor.BRIGHT_RED);
        addCornerMarker(content, 110, 0, "TOP-RIGHT", AnsiColor.BRIGHT_GREEN);
        addCornerMarker(content, 0, 35, "BOTTOM-LEFT", AnsiColor.BRIGHT_BLUE);
        addCornerMarker(content, 105, 35, "BOTTOM-RIGHT", AnsiColor.BRIGHT_YELLOW);

        // Add center marker
        addCornerMarker(content, 55, 18, "CENTER", AnsiColor.BRIGHT_MAGENTA);

        return content;
    }

    /**
     * Adds a colored rectangular area to the content.
     */
    private void addColoredArea(Composite content, int x, int y, int width, int height,
                                String title, AnsiColor bgColor, AnsiColor fgColor) {
        for (int dy = 0; dy < height; dy++) {
            for (int dx = 0; dx < width; dx++) {
                Text colorText = new Text("colorText_" + (x + dx) + "_" + (y + dy), 1, 1,
                    (dx == 0 || dx == width-1 || dy == 0 || dy == height-1) ? "█" : " ",
                    Text.Alignment.CENTER);
                colorText.setBackgroundColor(bgColor);
                colorText.setForegroundColor(fgColor);
                content.addChild(colorText);
            }
        }

        // Add title in the center
        if (title != null && width > title.length() && height > 2) {
            Text titleText = new Text("titleText_" + title.replaceAll(" ", "_"),
                    title.length(), 1, title, Text.Alignment.CENTER);
            titleText.setBackgroundColor(bgColor);
            titleText.setForegroundColor(fgColor);
            titleText.setBold(true);
            content.addChild(titleText);
        }
    }

    /**
     * Adds a border around the entire content area.
     */
    private void addBorderArea(Composite content, int x, int y, int width, int height,
                               String title, AnsiColor color) {
        // Top and bottom borders
        for (int dx = 0; dx < width; dx++) {
            Text topBorder = new Text("topBorder_" + dx, 1, 1, "═", Text.Alignment.CENTER);
            topBorder.setForegroundColor(color);
            content.addChild(topBorder);

            Text bottomBorder = new Text("bottomBorder_" + dx, 1, 1, "═", Text.Alignment.CENTER);
            bottomBorder.setForegroundColor(color);
            content.addChild(bottomBorder);
        }

        // Left and right borders
        for (int dy = 1; dy < height - 1; dy++) {
            Text leftBorder = new Text("leftBorder_" + dy, 1, 1, "║", Text.Alignment.CENTER);
            leftBorder.setForegroundColor(color);
            content.addChild(leftBorder);

            Text rightBorder = new Text("rightBorder_" + dy, 1, 1, "║", Text.Alignment.CENTER);
            rightBorder.setForegroundColor(color);
            content.addChild(rightBorder);
        }

        // Corners
        Text topLeft = new Text("topLeft", 1, 1, "╔", Text.Alignment.CENTER);
        topLeft.setForegroundColor(color);
        content.addChild(topLeft);

        Text topRight = new Text("topRight", 1, 1, "╗", Text.Alignment.CENTER);
        topRight.setForegroundColor(color);
        content.addChild(topRight);

        Text bottomLeft = new Text("bottomLeft", 1, 1, "╚", Text.Alignment.CENTER);
        bottomLeft.setForegroundColor(color);
        content.addChild(bottomLeft);

        Text bottomRight = new Text("bottomRight", 1, 1, "╝", Text.Alignment.CENTER);
        bottomRight.setForegroundColor(color);
        content.addChild(bottomRight);

        // Add title at top center
        if (title != null && width > title.length() + 4) {
            Text titleText = new Text("borderTitle",
                    title.length(), 1, title, Text.Alignment.CENTER);
            titleText.setForegroundColor(color);
            titleText.setBold(true);
            content.addChild(titleText);
        }
    }

    /**
     * Adds a corner marker with text.
     */
    private void addCornerMarker(Composite content, int x, int y, String text, AnsiColor color) {
        Text marker = new Text("marker_" + text, text.length(), 1, text, Text.Alignment.LEFT);
        marker.setForegroundColor(color);
        marker.setBold(true);
        marker.setBackgroundColor(AnsiColor.BLACK);
        content.addChild(marker);
    }
}
