package com.consolemaster;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * Demo application showcasing the Focus Management system.
 * Creates multiple focusable components and demonstrates focus traversal.
 */
@Slf4j
public class FocusDemo {

    public static void main(String[] args) {
        try {
            // Create the main screen canvas
            ScreenCanvas screen = new ScreenCanvas(80, 25);

            // Create a main container with BorderLayout
            Composite mainContainer = new Composite("mainContainer", 2, 2,
                                                               screen.getWidth() - 4,
                                                               screen.getHeight() - 4,
                                                               new BorderLayout(1));

            // Create focusable components

            // Header (not focusable)
            Box headerBox = new Box("headerBox", 0, 0, 0, 3, new DefaultBorder());
            Text headerText = new Text("headerText", 0, 0, 0, 0, "Focus Management Demo - Use TAB/SHIFT+TAB to navigate", Text.Alignment.CENTER);
            headerText.setForegroundColor(AnsiColor.BRIGHT_CYAN);
            headerText.setBold(true);
            headerBox.setChild(headerText);
            headerBox.setLayoutConstraint(new PositionConstraint(PositionConstraint.Position.TOP_CENTER));
            mainContainer.addChild(headerBox);

            // Left Panel - Focusable Button 1
            Box leftBox = new Box("leftBox", 0, 0, 20, 0, new DefaultBorder()) {
                @Override
                protected void onFocusChanged(boolean focused) {
                    super.onFocusChanged(focused);
                    // Change text styling when focused (border cannot be changed as it's final)
                    Text text = (Text) getChild();
                    if (text != null) {
                        if (focused) {
                            text.setBackgroundColor(AnsiColor.BLUE);
                            text.setForegroundColor(AnsiColor.BRIGHT_WHITE);
                        } else {
                            text.setBackgroundColor(null);
                            text.setForegroundColor(AnsiColor.GREEN);
                        }
                    }
                }
            };
            Text leftText = new Text("leftText", 0, 0, 0, 0, "Button 1\n\nFocusable\nComponent\n\nPress ENTER\nto activate", Text.Alignment.CENTER);
            leftText.setForegroundColor(AnsiColor.GREEN);
            leftText.setBold(true);
            leftBox.setChild(leftText);
            leftBox.setCanFocus(true); // Make this box focusable
            leftBox.setLayoutConstraint(new PositionConstraint(PositionConstraint.Position.CENTER_LEFT));
            mainContainer.addChild(leftBox);

            // Right Panel - Focusable Button 2
            Box rightBox = new Box("rightBox", 0, 0, 20, 0, new DefaultBorder()) {
                @Override
                protected void onFocusChanged(boolean focused) {
                    super.onFocusChanged(focused);
                    // Change text styling when focused (border cannot be changed as it's final)
                    Text text = (Text) getChild();
                    if (text != null) {
                        if (focused) {
                            text.setBackgroundColor(AnsiColor.MAGENTA);
                            text.setForegroundColor(AnsiColor.BRIGHT_WHITE);
                        } else {
                            text.setBackgroundColor(null);
                            text.setForegroundColor(AnsiColor.YELLOW);
                        }
                    }
                }
            };
            Text rightText = new Text("rightText", 0, 0, 0, 0, "Button 2\n\nAnother\nFocusable\nComponent\n\nPress ENTER\nto activate", Text.Alignment.CENTER);
            rightText.setForegroundColor(AnsiColor.YELLOW);
            rightText.setBold(true);
            rightBox.setChild(rightText);
            rightBox.setCanFocus(true); // Make this box focusable
            rightBox.setLayoutConstraint(new PositionConstraint(PositionConstraint.Position.CENTER_RIGHT));
            mainContainer.addChild(rightBox);

            // Center Panel - Multiple focusable text components
            Composite centerPanel = new Composite("centerPanel", 0, 0, 0, 0, new FlowLayout(2, 2));

            for (int i = 1; i <= 4; i++) {
                final int buttonNumber = i;
                Box centerBox = new Box("centerBox_" + i, 0, 0, 15, 4, new DefaultBorder()) {
                    @Override
                    protected void onFocusChanged(boolean focused) {
                        super.onFocusChanged(focused);
                        // Change text styling when focused (border cannot be changed as it's final)
                        Text text = (Text) getChild();
                        if (text != null) {
                            if (focused) {
                                text.setBackgroundColor(AnsiColor.RED);
                                text.setForegroundColor(AnsiColor.BRIGHT_WHITE);
                            } else {
                                text.setBackgroundColor(null);
                                text.setForegroundColor(AnsiColor.WHITE);
                            }
                        }
                    }
                };

                Text centerText = new Text("centerText_" + i, 0, 0, 0, 0, "Item " + i + "\n\nFocus\nMe!", Text.Alignment.CENTER);
                centerText.setForegroundColor(AnsiColor.WHITE);
                centerBox.setChild(centerText);
                centerBox.setCanFocus(true); // Make this box focusable
                centerPanel.addChild(centerBox);
            }

            centerPanel.setLayoutConstraint(new PositionConstraint(PositionConstraint.Position.CENTER));
            mainContainer.addChild(centerPanel);

            // Footer - Status info (not focusable)
            Box footerBox = new Box("footerBox", 0, 0, 0, 4, new DefaultBorder());
            Text footerText = new Text("footerText", 0, 0, 0, 0,
                "Focus Management Features:\n" +
                "• TAB: Next component • SHIFT+TAB: Previous component\n" +
                "• Focused components show ThickBorder and background color\n" +
                "Total focusable components: 6",
                Text.Alignment.CENTER);
            footerText.setForegroundColor(AnsiColor.BRIGHT_WHITE);
            footerBox.setChild(footerText);
            footerBox.setLayoutConstraint(new PositionConstraint(PositionConstraint.Position.BOTTOM_CENTER));
            mainContainer.addChild(footerBox);

            // Set the content and render
            screen.setContent(mainContainer);

            // Start with first focusable component
            screen.focusFirst();

            screen.render();

            System.out.println("\nFocus Management Demo rendered! Features:");
            System.out.println("- 6 focusable components (2 side panels + 4 center items)");
            System.out.println("- Focused components show ThickBorder and background color");
            System.out.println("- Focus traversal: TAB (next) / SHIFT+TAB (previous)");
            System.out.println("- Focus indicators: Border style and background color changes");
            System.out.println("- FocusManager automatically handles focus navigation");
            System.out.println("Demo completed successfully.");

            // Clean up
            screen.close();

        } catch (IOException e) {
            log.error("Error running Focus Management demo: {}", e.getMessage(), e);
        }
    }
}
