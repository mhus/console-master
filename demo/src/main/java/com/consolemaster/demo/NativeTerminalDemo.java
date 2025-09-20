package com.consolemaster.demo;

import com.consolemaster.AnsiColor;
import com.consolemaster.AnsiFormat;
import com.consolemaster.BorderLayout;
import com.consolemaster.Box;
import com.consolemaster.Canvas;
import com.consolemaster.Composite;
import com.consolemaster.DefaultBorder;
import com.consolemaster.FlowLayout;
import com.consolemaster.PositionConstraint;
import com.consolemaster.ProcessLoop;
import com.consolemaster.ScreenCanvas;
import com.consolemaster.Text;

import java.io.IOException;

/**
 * Demo application showcasing the native terminal implementation without JLine dependency.
 * Demonstrates the replacement of JLine with native ANSI terminal handling.
 */
public class NativeTerminalDemo {

    public static void main(String[] args) {
        try {
            // Create the main screen canvas using native terminal
            ScreenCanvas screen = new ScreenCanvas("MainScreen", 80, 25);

            // Create a composite canvas with BorderLayout
            Composite borderContainer = new Composite("BorderContainer",
                    screen.getWidth() - 4,
                                                                 screen.getHeight() - 4,
                                                                 new BorderLayout(1));

            // Create NORTH component (Header)
            Box northBox = new Box("NorthBox", 0, 3, new DefaultBorder());
            Text northText = new Text("NorthText", 0, 0, "NORTH - Native Terminal Demo", Text.Alignment.CENTER);
            northText.setForegroundColor(AnsiColor.BRIGHT_CYAN);
            northText.setFormats(AnsiFormat.BOLD);
            northBox.setContent(northText);
            northBox.setLayoutConstraint(PositionConstraint.NORTH);

            // Create SOUTH component (Footer)
            Box southBox = new Box("SouthBox", 0, 3, new DefaultBorder());
            Text southText = new Text("SouthText", 0, 0, "SOUTH - Press 'q' to quit, TAB to navigate", Text.Alignment.CENTER);
            southText.setForegroundColor(AnsiColor.BRIGHT_GREEN);
            southBox.setContent(southText);
            southBox.setLayoutConstraint(PositionConstraint.SOUTH);

            // Create WEST component (Sidebar)
            Box westBox = new Box("WestBox", 20, 0, new DefaultBorder());
            Composite westContent = new Composite("WestContent", 0, 0, new FlowLayout(1, 1));

            Text westTitle = new Text("WestTitle", 0, 1, "WEST Sidebar:", Text.Alignment.LEFT);
            westTitle.setForegroundColor(AnsiColor.YELLOW);
            westTitle.setFormats(AnsiFormat.BOLD);
            westTitle.setCanFocus(true);
            westContent.addChild(westTitle);

            Text westInfo1 = new Text("WestInfo1", 0, 1, "• Native ANSI", Text.Alignment.LEFT);
            westInfo1.setForegroundColor(AnsiColor.WHITE);
            westContent.addChild(westInfo1);

            Text westInfo2 = new Text("WestInfo2", 0, 1, "• No JLine deps", Text.Alignment.LEFT);
            westInfo2.setForegroundColor(AnsiColor.WHITE);
            westContent.addChild(westInfo2);

            Text westInfo3 = new Text("WestInfo3", 0, 1, "• Full keyboard", Text.Alignment.LEFT);
            westInfo3.setForegroundColor(AnsiColor.WHITE);
            westContent.addChild(westInfo3);

            Text westInfo4 = new Text("WestInfo4", 0, 1, "• Mouse support", Text.Alignment.LEFT);
            westInfo4.setForegroundColor(AnsiColor.WHITE);
            westContent.addChild(westInfo4);

            westBox.setContent(westContent);
            westBox.setLayoutConstraint(PositionConstraint.WEST);

            // Create EAST component (Status)
            Box eastBox = new Box("EastBox", 20, 0, new DefaultBorder());
            Composite eastContent = new Composite("EastContent", 0, 0, new FlowLayout(1, 1));

            Text eastTitle = new Text("EastTitle", 0, 1, "EAST Status:", Text.Alignment.LEFT);
            eastTitle.setForegroundColor(AnsiColor.MAGENTA);
            eastTitle.setFormats(AnsiFormat.BOLD);
            eastTitle.setCanFocus(true);
            eastContent.addChild(eastTitle);

            Text statusText = new Text("StatusText", 0, 1, "Terminal Ready", Text.Alignment.LEFT);
            statusText.setForegroundColor(AnsiColor.BRIGHT_GREEN);
            eastContent.addChild(statusText);

            Text sizeText = new Text("SizeText", 0, 1,
                "Size: " + screen.getWidth() + "x" + screen.getHeight(), Text.Alignment.LEFT);
            sizeText.setForegroundColor(AnsiColor.CYAN);
            eastContent.addChild(sizeText);

            eastBox.setContent(eastContent);
            eastBox.setLayoutConstraint(PositionConstraint.EAST);

            // Create CENTER component (Main content)
            Box centerBox = new Box("CenterBox", 0, 0, new DefaultBorder());
            Composite centerContent = new Composite("CenterContent", 0, 0, new FlowLayout(2, 2));

            Text centerTitle = new Text("CenterTitle", 0, 2, "CENTER - Native Terminal Implementation", Text.Alignment.CENTER);
            centerTitle.setForegroundColor(AnsiColor.BRIGHT_WHITE);
            centerTitle.setFormats(AnsiFormat.BOLD, AnsiFormat.UNDERLINE);
            centerTitle.setCanFocus(true);
            centerContent.addChild(centerTitle);

            Text infoText1 = new Text("InfoText1", 0, 1, "This demo shows the framework running completely", Text.Alignment.CENTER);
            infoText1.setForegroundColor(AnsiColor.WHITE);
            centerContent.addChild(infoText1);

            Text infoText2 = new Text("InfoText2", 0, 1, "without JLine dependencies!", Text.Alignment.CENTER);
            infoText2.setForegroundColor(AnsiColor.BRIGHT_GREEN);
            infoText2.setFormats(AnsiFormat.BOLD);
            centerContent.addChild(infoText2);

            Text controlsTitle = new Text("ControlsTitle", 0, 2, "Controls:", Text.Alignment.CENTER);
            controlsTitle.setForegroundColor(AnsiColor.YELLOW);
            controlsTitle.setFormats(AnsiFormat.BOLD);
            centerContent.addChild(controlsTitle);

            Text control1 = new Text("Control1", 0, 1, "TAB/Shift+TAB - Navigate focus", Text.Alignment.CENTER);
            control1.setForegroundColor(AnsiColor.CYAN);
            centerContent.addChild(control1);

            Text control2 = new Text("Control2", 0, 1, "Arrow keys - Navigate", Text.Alignment.CENTER);
            control2.setForegroundColor(AnsiColor.CYAN);
            centerContent.addChild(control2);

            Text control3 = new Text("Control3", 0, 1, "F1-F12 - Function keys", Text.Alignment.CENTER);
            control3.setForegroundColor(AnsiColor.CYAN);
            centerContent.addChild(control3);

            Text control4 = new Text("Control4", 0, 1, "'q' or ESC - Quit", Text.Alignment.CENTER);
            control4.setForegroundColor(AnsiColor.BRIGHT_RED);
            control4.setFormats(AnsiFormat.BOLD);
            centerContent.addChild(control4);

            centerBox.setContent(centerContent);
            centerBox.setLayoutConstraint(new PositionConstraint(PositionConstraint.Position.CENTER));

            // Add all components to border layout
            borderContainer.addChild(northBox);
            borderContainer.addChild(southBox);
            borderContainer.addChild(westBox);
            borderContainer.addChild(eastBox);
            borderContainer.addChild(centerBox);

            // Set the border container as the main content
            screen.setContent(borderContainer);

            // Create and start the process loop with native terminal
            ProcessLoop processLoop = new ProcessLoop(screen);

            // Enable mouse reporting
            processLoop.enableMouseReporting();

            // Register keyboard shortcuts
            screen.registerShortcut("q", () -> {
                try {
                    processLoop.stop();
                } catch (IOException e) {
                    System.err.println("Error stopping process loop: " + e.getMessage());
                }
            });

            screen.registerShortcut("ESC", () -> {
                try {
                    processLoop.stop();
                } catch (IOException e) {
                    System.err.println("Error stopping process loop: " + e.getMessage());
                }
            });

            // Add update callback to show dynamic content
            processLoop.setUpdateCallback(() -> {
                // Update terminal size display
                sizeText.setText("Size: " + screen.getWidth() + "x" + screen.getHeight());

                // Update status based on focus
                Canvas focused = screen.getFocusedCanvas();
                if (focused != null) {
                    statusText.setText("Focus: " + focused.getClass().getSimpleName());
                } else {
                    statusText.setText("No Focus");
                }
            });

            // Start the process loop
            System.out.println("Starting Native Terminal Demo...");
            System.out.println("Framework is now running without JLine!");
            processLoop.start();

            // Cleanup
            screen.close();
            System.out.println("\nNative Terminal Demo finished.");

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
