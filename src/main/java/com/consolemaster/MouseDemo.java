package com.consolemaster;

import java.io.IOException;

/**
 * Interactive demo showcasing the Mouse Reporting system with various mouse interactions.
 * Demonstrates mouse events, different mouse managers, and interactive components.
 */
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
            CompositeCanvas mainContainer = new CompositeCanvas(2, 2,
                                                               screen.getWidth() - 4,
                                                               screen.getHeight() - 4,
                                                               new BorderLayout(1));

            // Create header
            Box headerBox = new Box(0, 0, 0, 3, new DefaultBorder());
            Text headerText = new Text(0, 0, 0, 0, "Mouse Demo - Interactive Mouse Events", Text.Alignment.CENTER);
            headerText.setForegroundColor(AnsiColor.BRIGHT_CYAN);
            headerText.setBold(true);
            headerBox.setChild(headerText);
            headerBox.setLayoutConstraint(new PositionConstraint(PositionConstraint.Position.TOP_CENTER));

            // Create interactive buttons with mouse event handling
            CompositeCanvas centerPanel = new CompositeCanvas(0, 0, 0, 0, new FlowLayout(2, 2));

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
            Box statusBox = new Box(0, 0, 0, 5, new DefaultBorder());
            Text statusText = new Text(0, 0, 0, 0, "", Text.Alignment.CENTER);
            updateStatusText(statusText);
            statusBox.setChild(statusText);
            statusBox.setLayoutConstraint(new PositionConstraint(PositionConstraint.Position.BOTTOM_CENTER));

            // Add components to main container
            mainContainer.addChild(headerBox);
            mainContainer.addChild(centerPanel);
            mainContainer.addChild(statusBox);

            // Set content
            screen.setContentCanvas(mainContainer);

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
            System.err.println("Error running Mouse demo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static Box createMouseButton(String text, AnsiColor color, Runnable clickAction) {
        Box button = new Box(0, 0, 18, 6, new DefaultBorder()) {
            @Override
            protected void onFocusChanged(boolean focused) {
                super.onFocusChanged(focused);
                updateButtonStyle(this, focused, false, color);
            }
        };

        Text buttonText = new Text(0, 0, 0, 0, text, Text.Alignment.CENTER);
        buttonText.setForegroundColor(color);
        button.setChild(buttonText);
        button.setCanFocus(true);

        // Add mouse event handling
        addMouseEventHandling(button, clickAction, color);

        return button;
    }

    private static void addMouseEventHandling(Box box, Runnable clickAction, AnsiColor baseColor) {
        // Create a wrapper that implements EventHandler
        Box eventBox = new Box(box.getX(), box.getY(), box.getWidth(), box.getHeight(),
                              box.getBorder()) {
            private boolean isHovered = false;
            private boolean isPressed = false;

            @Override
            protected void onFocusChanged(boolean focused) {
                super.onFocusChanged(focused);
                box.onFocusChanged(focused);
                updateButtonStyle(this, focused, isHovered, baseColor);
            }

            @Override
            public void paint(Graphics graphics) {
                box.paint(graphics);
            }
        };

        // Implement mouse event handling
        EventHandler mouseHandler = new EventHandler() {
            private boolean isHovered = false;
            private boolean isPressed = false;

            @Override
            public void handleEvent(Event event) {
                if (event instanceof MouseEvent mouseEvent) {
                    mouseX = mouseEvent.getX();
                    mouseY = mouseEvent.getY();

                    switch (mouseEvent.getAction()) {
                        case MOVE:
                            if (!isHovered) {
                                isHovered = true;
                                lastAction = "Mouse Enter: " + ((Text)box.getChild()).getText().split("\n")[0];
                                updateButtonStyle(box, box.isHasFocus(), true, baseColor);
                            }
                            break;

                        case PRESS:
                            if (mouseEvent.getButton() == MouseEvent.Button.LEFT) {
                                isPressed = true;
                                lastAction = "Mouse Press: " + mouseEvent.getButton();
                                updateButtonStyle(box, box.isHasFocus(), true, baseColor);
                            }
                            break;

                        case RELEASE:
                            if (isPressed) {
                                isPressed = false;
                                updateButtonStyle(box, box.isHasFocus(), isHovered, baseColor);
                            }
                            break;

                        case CLICK:
                            if (mouseEvent.getButton() == MouseEvent.Button.LEFT) {
                                clickAction.run();
                                mouseEvent.consume();
                            } else if (mouseEvent.getButton() == MouseEvent.Button.RIGHT) {
                                lastAction = "Right Click: " + ((Text)box.getChild()).getText().split("\n")[0];
                                mouseEvent.consume();
                            }
                            break;

                        case DOUBLE_CLICK:
                            lastAction = "Double Click: " + ((Text)box.getChild()).getText().split("\n")[0];
                            clickCount += 2;
                            mouseEvent.consume();
                            break;

                        case WHEEL_UP:
                            lastAction = "Wheel Up";
                            mouseEvent.consume();
                            break;

                        case WHEEL_DOWN:
                            lastAction = "Wheel Down";
                            mouseEvent.consume();
                            break;

                        case DRAG:
                            lastAction = String.format("Dragging to (%d,%d)", mouseEvent.getX(), mouseEvent.getY());
                            break;
                    }
                }
            }
        };

        // Apply the event handler to the box
        box.setCanFocus(true);
        // Note: In a real implementation, Box would implement EventHandler directly
        // This is a simplified approach for demonstration
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
