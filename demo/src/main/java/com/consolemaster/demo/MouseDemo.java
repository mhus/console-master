package com.consolemaster.demo;

import com.consolemaster.AnsiColor;
import com.consolemaster.BorderLayout;
import com.consolemaster.Box;
import com.consolemaster.Composite;
import com.consolemaster.DefaultBorder;
import com.consolemaster.DisplayGridOverlayCanvas;
import com.consolemaster.FlowLayout;
import com.consolemaster.PositionConstraint;
import com.consolemaster.ProcessLoop;
import com.consolemaster.ScreenCanvas;
import com.consolemaster.Text;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * Interactive demo showcasing the Mouse Reporting system with various mouse interactions.
 * Demonstrates mouse events, different mouse managers, and interactive components.
 */
@Slf4j
public class MouseDemo {

    private static int clickCount = 0;
    private static String lastAction = "None";
    private static int mouseX = 0;
    private static int mouseY = 0;

    public static void main(String[] args) {
        try {
            // Create the main screen canvas
            ScreenCanvas screen = new ScreenCanvas(80, 25);

            // Create main container with BorderLayout
            Composite mainContainer = new Composite("mainContainer",
                    screen.getWidth() - 4,
                                                               screen.getHeight() - 4,
                                                               new BorderLayout(1));

            // Create header
            Box headerBox = new Box("headerBox", 0, 3, new DefaultBorder());
            Text headerText = new Text("headerText", 0, 0, "Mouse Demo - Interactive Mouse Events", Text.Alignment.CENTER);
            headerText.setForegroundColor(AnsiColor.BRIGHT_CYAN);
            headerText.setBold(true);
            headerBox.setContent(headerText);
            headerBox.setLayoutConstraint(new PositionConstraint(PositionConstraint.Position.TOP_CENTER));

            // Create interactive buttons with mouse event handling
            Composite centerPanel = new Composite("centerPanel", 0, 0, new FlowLayout(2, 2));

            // Clickable button 1
            Box button1 = createMouseButton("Button 1\n\nClick Me!\n\nLeft/Right\nClicks",
                                           AnsiColor.GREEN, () -> {
                clickCount++;
                lastAction = "Button 1 Clicked";
            });
            centerPanel.addChild(button1);

            // Clickable button 2
            Box button2 = createMouseButton("Button 2\n\nMouse Over!\n\nHover and\nClick Events",
                                           AnsiColor.BLUE, () -> {
                clickCount++;
                lastAction = "Button 2 Activated";
            });
            centerPanel.addChild(button2);

            // Draggable area
            Box dragArea = createMouseButton("Drag Area\n\nClick & Drag\n\nMouse Move\nTracking",
                                            AnsiColor.MAGENTA, () -> {
                lastAction = "Drag Area Clicked";
            });
            centerPanel.addChild(dragArea);

            // Wheel scroll area
            Box wheelArea = createMouseButton("Scroll Area\n\nMouse Wheel\n\nUp/Down\nScrolling",
                                             AnsiColor.YELLOW, () -> {
                lastAction = "Scroll Area Clicked";
            });
            centerPanel.addChild(wheelArea);

            centerPanel.setLayoutConstraint(new PositionConstraint(PositionConstraint.Position.CENTER));

            // Create status footer
            Box statusBox = new Box("statusBox", 0, 5, new DefaultBorder());
            Text statusText = new Text("statusText", 0, 0, "", Text.Alignment.CENTER);
            updateStatusText(statusText);
            statusBox.setContent(statusText);
            statusBox.setLayoutConstraint(new PositionConstraint(PositionConstraint.Position.BOTTOM_CENTER));

            // Add components to main container
            mainContainer.addChild(headerBox);
            mainContainer.addChild(centerPanel);
            mainContainer.addChild(statusBox);

            // Set content
            screen.setOverlayCanvas(new DisplayGridOverlayCanvas("gridOverlay"));
            screen.setContent(mainContainer);

            // Create process loop with mouse support
            ProcessLoop processLoop = new ProcessLoop(screen);
            processLoop.setTargetFPS(60);

            // Enable mouse reporting
            processLoop.enableMouseReporting();

            // Register keyboard shortcuts
            screen.registerShortcut("Ctrl+Q", () -> {
                try {
                    processLoop.stop();
                } catch (IOException e) {
                    System.err.println("Error stopping process loop: " + e.getMessage());
                }
            });
            screen.registerShortcut("M", () -> {
                if (processLoop.isMouseReportingEnabled()) {
                    processLoop.disableMouseReporting();
                    lastAction = "Mouse Reporting Disabled";
                } else {
                    processLoop.enableMouseReporting();
                    lastAction = "Mouse Reporting Enabled";
                }
            });

            // Update callback for continuous status updates
            processLoop.setUpdateCallback(() -> {
                updateStatusText((Text) statusBox.getChild(), processLoop);
            });

            System.out.println("Starting Mouse Demo...");
            System.out.println("Mouse Features:");
            System.out.println("- Click on buttons to interact");
            System.out.println("- Hover over components for visual feedback");
            System.out.println("- Drag mouse for movement tracking");
            System.out.println("- Use mouse wheel for scroll events");
            System.out.println("- Press 'M' to toggle mouse reporting");
            System.out.println("- ESC or Ctrl+Q to quit");

            // Start the process loop (this will block until stopped)
            processLoop.start();

            System.out.println("Mouse Demo ended.");

        } catch (IOException e) {
            log.error("Error running Mouse demo", e);
        }
    }

    private static Box createMouseButton(String text, AnsiColor color, Runnable clickAction) {
        Box button = new Box("box:"+text, 18, 6, new DefaultBorder()) {
            @Override
            public void onFocusChanged(boolean focused) {
                super.onFocusChanged(focused);
                updateButtonStyle(this, focused, false, color);
            }
        };

        Text buttonText = new Text("text:" + text, 0, 0, text, Text.Alignment.CENTER);
        buttonText.setForegroundColor(color);
        button.setContent(buttonText);
        button.setCanFocus(true);

        return button;
    }

    private static void updateButtonStyle(Box box, boolean focused, boolean hovered, AnsiColor baseColor) {
        Text text = (Text) box.getChild();
        if (text != null) {
            if (focused) {
                text.setBackgroundColor(baseColor);
                text.setForegroundColor(AnsiColor.BRIGHT_WHITE);
            } else if (hovered) {
                text.setBackgroundColor(null);
                text.setForegroundColor(AnsiColor.BRIGHT_WHITE);
            } else {
                text.setBackgroundColor(null);
                text.setForegroundColor(baseColor);
            }
        }
    }

    private static void updateStatusText(Text statusText) {
        updateStatusText(statusText, null);
    }

    private static void updateStatusText(Text statusText, ProcessLoop processLoop) {
        String status = "Mouse Demo - Interactive Events\n";
        if (processLoop != null) {
            status += String.format("FPS: %d | Clicks: %d | Mouse: (%d,%d)\n",
                                   processLoop.getCurrentFPS(),
                                   clickCount,
                                   mouseX, mouseY);
        } else {
            status += "Initializing...\n";
        }
        status += String.format("Last Action: %s\n", lastAction);
        status += "Controls: M=Toggle Mouse, ESC/Ctrl+Q=Quit";

        statusText.setText(status);
        statusText.setForegroundColor(AnsiColor.BRIGHT_WHITE);
    }
}
