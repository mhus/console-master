package com.consolemaster.demo;

import com.consolemaster.AnsiColor;
import com.consolemaster.BorderLayout;
import com.consolemaster.Box;
import com.consolemaster.Composite;
import com.consolemaster.ConsoleOutput;
import com.consolemaster.DefaultBorder;
import com.consolemaster.FlowLayout;
import com.consolemaster.PositionConstraint;
import com.consolemaster.ProcessLoop;
import com.consolemaster.ScreenCanvas;
import com.consolemaster.Text;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Demo application showing the output capture functionality.
 * This demo creates a split-screen interface with controls on the left and captured console output on the right.
 */
public class OutputCaptureDemo {

    private ScheduledExecutorService outputGenerator;
    private int messageCounter = 0;
    private ConsoleOutput consoleOutput;

    public static void main(String[] args) throws IOException {
        new OutputCaptureDemo().run();
    }

    public void run() throws IOException {
        // Create main screen
        ScreenCanvas screen = new ScreenCanvas(100, 30);

        // Create main layout using BorderLayout
        Composite mainCanvas = new Composite("mainCanvas", 100, 30, new BorderLayout(1));

        // Create header
        Box header = new Box("header", 0, 3, new DefaultBorder());
        Text headerText = new Text("headerText", 0, 0, "Output Capture Demo - Press keys to generate output", Text.Alignment.CENTER);
        headerText.setForegroundColor(AnsiColor.BRIGHT_CYAN);
        headerText.setBold(true);
        header.setContent(headerText);
        header.setLayoutConstraint(new PositionConstraint(PositionConstraint.Position.TOP_CENTER));

        // Create control panel (left side)
        Composite controlPanel = createControlPanel();
        controlPanel.setLayoutConstraint(new PositionConstraint(PositionConstraint.Position.CENTER_LEFT));

        // Create ProcessLoop and enable output capture
        ProcessLoop processLoop = new ProcessLoop(screen);
        processLoop.setCaptureOutput(true);

        // Create console output display (right side)
        consoleOutput = processLoop.createConsoleOutputCanvas(0, 0, 0, 0);
        consoleOutput.setDisplayMode(ConsoleOutput.DisplayMode.BOTH);
        consoleOutput.setShowLineNumbers(true);
        consoleOutput.setStdoutColor(AnsiColor.GREEN);
        consoleOutput.setStderrColor(AnsiColor.BRIGHT_RED);

        Box outputBox = new Box("outputBox", 0, 0, new DefaultBorder());
        outputBox.setContent(consoleOutput);
        outputBox.setLayoutConstraint(new PositionConstraint(PositionConstraint.Position.CENTER_RIGHT));

        // Add components to main canvas
        mainCanvas.addChild(header);
        mainCanvas.addChild(controlPanel);
        mainCanvas.addChild(outputBox);

        // Set up screen
        screen.setContent(mainCanvas);
        screen.pack();

        // Register shortcuts for demo functionality
        setupShortcuts(screen, processLoop);

        // Start background output generation
        startOutputGeneration();

        // Set update callback to refresh display
        processLoop.setUpdateCallback(() -> {
            // Request redraw if there's new output
            if (processLoop.getOutputCapture().getStdoutLines().size() +
                processLoop.getOutputCapture().getStderrLines().size() > 0) {
                processLoop.requestRedraw();
            }
        });

        // Start the process loop
        try {
            processLoop.start();
        } finally {
            stopOutputGeneration();
        }
    }

    private Composite createControlPanel() {
        Composite panel = new Composite("controlPanel", 45, 0, new FlowLayout(1, 1));

        // Title
        Text title = new Text("controlTitle", 43, 1, "Controls:", Text.Alignment.LEFT);
        title.setForegroundColor(AnsiColor.BRIGHT_YELLOW);
        title.setBold(true);
        panel.addChild(title);

        // Instructions
        String[] instructions = {
            "",
            "S - Generate stdout message",
            "E - Generate stderr message",
            "B - Generate both messages",
            "C - Clear captured output",
            "1 - Show stdout only",
            "2 - Show stderr only",
            "3 - Show both (interleaved)",
            "4 - Show separated view",
            "L - Toggle line numbers",
            "A - Toggle auto-scroll",
            "UP/DOWN - Scroll when auto-scroll off",
            "",
            "F5 - Force redraw",
            "ESC/Ctrl+C - Exit",
            "",
            "Output will be generated automatically",
            "every 2 seconds for demonstration."
        };

        int instructionIndex = 0;
        for (String instruction : instructions) {
            Text text = new Text("instruction_" + instructionIndex, 43, 1, instruction, Text.Alignment.LEFT);
            if (instruction.contains(" - ")) {
                text.setForegroundColor(AnsiColor.WHITE);
            } else {
                text.setForegroundColor(AnsiColor.BRIGHT_BLACK);
            }
            panel.addChild(text);
            instructionIndex++;
        }

        return panel;
    }

    private void setupShortcuts(ScreenCanvas screen, ProcessLoop processLoop) {
        // Output generation shortcuts
        screen.registerShortcut("s", () -> generateStdoutMessage());
        screen.registerShortcut("S", () -> generateStdoutMessage());
        screen.registerShortcut("e", () -> generateStderrMessage());
        screen.registerShortcut("E", () -> generateStderrMessage());
        screen.registerShortcut("b", () -> {
            generateStdoutMessage();
            generateStderrMessage();
        });
        screen.registerShortcut("B", () -> {
            generateStdoutMessage();
            generateStderrMessage();
        });

        // Output control shortcuts
        screen.registerShortcut("c", processLoop::clearCapturedOutput);
        screen.registerShortcut("C", processLoop::clearCapturedOutput);

        // Display mode shortcuts
        screen.registerShortcut("1", () -> consoleOutput.setDisplayMode(ConsoleOutput.DisplayMode.STDOUT_ONLY));
        screen.registerShortcut("2", () -> consoleOutput.setDisplayMode(ConsoleOutput.DisplayMode.STDERR_ONLY));
        screen.registerShortcut("3", () -> consoleOutput.setDisplayMode(ConsoleOutput.DisplayMode.BOTH));
        screen.registerShortcut("4", () -> consoleOutput.setDisplayMode(ConsoleOutput.DisplayMode.SEPARATED));

        // Display options
        screen.registerShortcut("l", () -> consoleOutput.setShowLineNumbers(!consoleOutput.isShowLineNumbers()));
        screen.registerShortcut("L", () -> consoleOutput.setShowLineNumbers(!consoleOutput.isShowLineNumbers()));
        screen.registerShortcut("a", () -> {
            if (consoleOutput.isAutoScroll()) {
                consoleOutput.setAutoScroll(false);
            } else {
                consoleOutput.scrollToBottom();
            }
        });
        screen.registerShortcut("A", () -> {
            if (consoleOutput.isAutoScroll()) {
                consoleOutput.setAutoScroll(false);
            } else {
                consoleOutput.scrollToBottom();
            }
        });

        // Scroll shortcuts
        screen.registerShortcut("UP", () -> consoleOutput.scrollUp(1));
        screen.registerShortcut("DOWN", () -> consoleOutput.scrollDown(1));
        screen.registerShortcut("PAGE_UP", () -> consoleOutput.scrollUp(5));
        screen.registerShortcut("PAGE_DOWN", () -> consoleOutput.scrollDown(5));
        screen.registerShortcut("HOME", () -> consoleOutput.scrollToTop());
        screen.registerShortcut("END", () -> consoleOutput.scrollToBottom());
    }

    private void generateStdoutMessage() {
        messageCounter++;
        System.out.println("STDOUT Message #" + messageCounter + " - This is a standard output message at " +
                          new java.util.Date());
    }

    private void generateStderrMessage() {
        messageCounter++;
        System.err.println("STDERR Message #" + messageCounter + " - This is an error message at " +
                          new java.util.Date());
    }

    private void startOutputGeneration() {
        outputGenerator = Executors.newSingleThreadScheduledExecutor();
        outputGenerator.scheduleAtFixedRate(() -> {
            try {
                if (Math.random() < 0.6) {
                    generateStdoutMessage();
                } else {
                    generateStderrMessage();
                }

                // Occasionally generate some complex output
                if (Math.random() < 0.1) {
                    System.out.println("Complex output with multiple lines:");
                    System.out.println("  - Line 1 of complex output");
                    System.out.println("  - Line 2 of complex output");
                    System.err.println("ERROR: Something went wrong in complex operation!");
                }
            } catch (Exception e) {
                System.err.println("Error in output generation: " + e.getMessage());
            }
        }, 2, 2, TimeUnit.SECONDS);
    }

    private void stopOutputGeneration() {
        if (outputGenerator != null) {
            outputGenerator.shutdown();
            try {
                if (!outputGenerator.awaitTermination(1, TimeUnit.SECONDS)) {
                    outputGenerator.shutdownNow();
                }
            } catch (InterruptedException e) {
                outputGenerator.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
}
