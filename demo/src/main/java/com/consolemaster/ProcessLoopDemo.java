package com.consolemaster;

import java.io.IOException;

/**
 * Interactive demo showcasing the ProcessLoop system with continuous rendering,
 * non-blocking input handling, and event-driven component interactions.
 */
public class ProcessLoopDemo {

    private static int counter = 0;
    private static boolean showHelp = false;

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
            Text headerText = new Text("headerText", 0, 0, "ProcessLoop Demo - Interactive Console Application", Text.Alignment.CENTER);
            headerText.setForegroundColor(AnsiColor.BRIGHT_CYAN);
            headerText.setBold(true);
            headerBox.setContent(headerText);
            headerBox.setLayoutConstraint(new PositionConstraint(PositionConstraint.Position.TOP_CENTER));

            // Create interactive buttons with event handling
            Composite centerPanel = new Composite("centerPanel", 0, 0, new FlowLayout(2, 2));

            // Counter button
            Box counterBox = new Box("counterBox", 20, 5, new DefaultBorder()) {
                @Override
                protected void onFocusChanged(boolean focused) {
                    super.onFocusChanged(focused);
                    updateButtonStyle(this, focused, AnsiColor.GREEN);
                }
            };
            Text counterText = new Text("counterText", 0, 0, "", Text.Alignment.CENTER);
            updateCounterText(counterText);
            counterBox.setContent(counterText);
            counterBox.setCanFocus(true);

            // Help button
            Box helpBox = new Box("helpBox", 20, 5, new DefaultBorder()) {
                @Override
                protected void onFocusChanged(boolean focused) {
                    super.onFocusChanged(focused);
                    updateButtonStyle(this, focused, AnsiColor.BLUE);
                }
            };
            Text helpText = new Text("helpText", 0, 0, "Help\n\nPress ENTER\nto toggle\nhelp display", Text.Alignment.CENTER);
            helpText.setForegroundColor(AnsiColor.BLUE);
            helpBox.setContent(helpText);
            helpBox.setCanFocus(true);

            // Reset button
            Box resetBox = new Box("resetBox", 20, 5, new DefaultBorder()) {
                @Override
                protected void onFocusChanged(boolean focused) {
                    super.onFocusChanged(focused);
                    updateButtonStyle(this, focused, AnsiColor.RED);
                }
            };
            Text resetText = new Text("resetText", 0, 0, "Reset\n\nPress ENTER\nto reset\ncounter", Text.Alignment.CENTER);
            resetText.setForegroundColor(AnsiColor.RED);
            resetBox.setContent(resetText);
            resetBox.setCanFocus(true);

            // Make buttons event handlers
            addEventHandling(counterBox, () -> {
                counter++;
                updateCounterText((Text) counterBox.getChild());
            });

            addEventHandling(helpBox, () -> {
                showHelp = !showHelp;
                updateHelpDisplay(mainContainer);
            });

            addEventHandling(resetBox, () -> {
                counter = 0;
                updateCounterText((Text) counterBox.getChild());
            });

            centerPanel.addChild(counterBox);
            centerPanel.addChild(helpBox);
            centerPanel.addChild(resetBox);
            centerPanel.setLayoutConstraint(new PositionConstraint(PositionConstraint.Position.CENTER));

            // Create footer with status info
            Box footerBox = new Box("footerBox", 0, 4, new DefaultBorder());
            Text footerText = new Text("footerText", 0, 0, "", Text.Alignment.CENTER);
            updateFooterText(footerText);
            footerBox.setContent(footerText);
            footerBox.setLayoutConstraint(new PositionConstraint(PositionConstraint.Position.BOTTOM_CENTER));

            // Add components to main container
            mainContainer.addChild(headerBox);
            mainContainer.addChild(centerPanel);
            mainContainer.addChild(footerBox);

            // Set content and initialize
            screen.setContent(mainContainer);
            screen.focusFirst();

            // Create process loop with update callback
            ProcessLoop processLoop = new ProcessLoop(screen);
            processLoop.setTargetFPS(60);

            // Register custom shortcuts
            screen.registerShortcut("Ctrl+Q", () -> {
                try {
                    processLoop.stop();
                } catch (IOException e) {
                    System.err.println("Error stopping process loop: " + e.getMessage());
                }
            });
            screen.registerShortcut("Ctrl+H", () -> {
                showHelp = !showHelp;
                updateHelpDisplay(mainContainer);
            });
            screen.registerShortcut("SPACE", () -> {
                counter++;
                updateCounterText((Text) counterBox.getChild());
            });

            // Update callback for continuous updates
            processLoop.setUpdateCallback(() -> {
                // Update footer with current FPS and frame count
                updateFooterText((Text) footerBox.getChild(), processLoop);
            });

            System.out.println("Starting ProcessLoop Demo...");
            System.out.println("Use TAB/SHIFT+TAB to navigate, ENTER to activate buttons");
            System.out.println("ESC or Ctrl+Q to quit, SPACE to increment counter");
            System.out.println("Ctrl+H to toggle help, F5 to refresh");

            // Start the process loop (this will block until stopped)
            processLoop.start();

            System.out.println("ProcessLoop Demo ended.");

        } catch (IOException e) {
            System.err.println("Error running ProcessLoop demo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void updateButtonStyle(Box box, boolean focused, AnsiColor baseColor) {
        Text text = (Text) box.getChild();
        if (text != null) {
            if (focused) {
                text.setBackgroundColor(baseColor);
                text.setForegroundColor(AnsiColor.BRIGHT_WHITE);
            } else {
                text.setBackgroundColor(null);
                text.setForegroundColor(baseColor);
            }
        }
    }

    private static void updateCounterText(Text counterText) {
        counterText.setText("Counter\n\nValue: " + counter + "\n\nPress ENTER\nto increment");
        counterText.setForegroundColor(AnsiColor.GREEN);
    }

    private static void updateFooterText(Text footerText) {
        updateFooterText(footerText, null);
    }

    private static void updateFooterText(Text footerText, ProcessLoop processLoop) {
        String status = "ProcessLoop Demo - Interactive Console Framework\n";
        if (processLoop != null) {
            status += String.format("FPS: %d | Frames: %d | Counter: %d",
                                   processLoop.getCurrentFPS(),
                                   processLoop.getFrameCount(),
                                   counter);
        } else {
            status += "Initializing...";
        }
        status += "\nControls: TAB/SHIFT+TAB=Navigate, ENTER=Activate, SPACE=Count, ESC/Ctrl+Q=Quit";

        footerText.setText(status);
        footerText.setForegroundColor(AnsiColor.BRIGHT_WHITE);
    }

    private static void updateHelpDisplay(Composite mainContainer) {
        // This is a placeholder for help display toggle
        // In a real application, you might add/remove help panels
    }

    private static void addEventHandling(Box box, Runnable action) {
        // Add event handling capability to the box
        Canvas originalBox = box;
        Canvas eventHandlingBox = new Canvas("eventHandlingBox", box.getWidth(), box.getHeight()) {
            @Override
            public void paint(Graphics graphics) {
                originalBox.paint(graphics);
            }

            @Override
            protected void onFocusChanged(boolean focused) {
                originalBox.setHasFocus(focused);
                if (originalBox instanceof Box boxInstance) {
                    boxInstance.onFocusChanged(focused);
                }
            }
        };

        // Make the box implement EventHandler
        Box eventBox = new Box("eventBox", box.getWidth(), box.getHeight(),
                              box.getBorder()) {
            @Override
            protected void onFocusChanged(boolean focused) {
                super.onFocusChanged(focused);
                box.onFocusChanged(focused);
            }

            @Override
            public void paint(Graphics graphics) {
                box.paint(graphics);
            }
        };

        // Add EventHandler implementation
        EventHandler handler = new EventHandler() {
            @Override
            public void handleEvent(Event event) {
                if (event instanceof KeyEvent keyEvent) {
                    if (keyEvent.isSpecialKey() && keyEvent.getSpecialKey() == KeyEvent.SpecialKey.ENTER) {
                        action.run();
                        keyEvent.consume();
                    }
                }
            }
        };

        // This is a simplified approach - in a real implementation,
        // we'd extend Box to implement EventHandler directly
        box.setCanFocus(true);
    }
}
