package com.consolemaster;

import lombok.Getter;
import lombok.Setter;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;

/**
 * The main entry point canvas for the console application.
 * Manages the console terminal and handles minimum size requirements.
 */
@Getter
@Setter
public class ScreenCanvas extends CompositeCanvas {

    private static final int DEFAULT_MIN_WIDTH = 80;
    private static final int DEFAULT_MIN_HEIGHT = 24;

    private final Terminal terminal;
    private final int minWidth;
    private final int minHeight;
    private Canvas warningCanvas;
    private Canvas contentCanvas;

    /**
     * Creates a new ScreenCanvas with default minimum size requirements.
     */
    public ScreenCanvas() throws IOException {
        this(DEFAULT_MIN_WIDTH, DEFAULT_MIN_HEIGHT);
    }

    /**
     * Creates a new ScreenCanvas with specified minimum size requirements.
     */
    public ScreenCanvas(int minWidth, int minHeight) throws IOException {
        super(0, 0, 0, 0);
        this.minWidth = minWidth;
        this.minHeight = minHeight;
        this.terminal = TerminalBuilder.builder().system(true).build();

        // Initialize screen dimensions
        setWidth(terminal.getWidth());
        setHeight(terminal.getHeight());

        // Create warning canvas
        createWarningCanvas();
        updateDisplay();
    }

    public void setContentCanvas(Canvas contentCanvas) {
        if (this.contentCanvas != null) {
            removeChild(this.contentCanvas);
        }
        this.contentCanvas = contentCanvas;
        updateDisplay();
    }

    public void updateSize() {
        setWidth(terminal.getWidth());
        setHeight(terminal.getHeight());
        updateDisplay();
    }

    public void render() {
        JLineGraphics graphics = new JLineGraphics(getWidth(), getHeight());
        graphics.clear();
        paint(graphics);

        // Use JLine's efficient AttributedString output
        terminal.writer().print("\033[2J\033[H"); // Clear screen and move cursor to top
        terminal.writer().print(graphics.toAttributedString().toAnsi(terminal));
        terminal.flush();
    }

    public void close() throws IOException {
        terminal.close();
    }

    public boolean meetsMinimumSize() {
        return getWidth() >= minWidth && getHeight() >= minHeight;
    }

    private void createWarningCanvas() {
        warningCanvas = new Canvas(0, 0, getWidth(), getHeight()) {
            @Override
            public void paint(Graphics graphics) {
                String message = "Console too small!";
                String requirement = String.format("Required: %dx%d", minWidth, minHeight);
                String current = String.format("Current: %dx%d", getWidth(), getHeight());

                if (getWidth() > 10 && getHeight() > 5) {
                    int centerX = getWidth() / 2;
                    int centerY = getHeight() / 2;

                    graphics.drawRect(0, 0, getWidth(), getHeight(), '#');
                    graphics.drawString(Math.max(1, centerX - message.length() / 2), centerY - 1, message);
                    graphics.drawString(Math.max(1, centerX - requirement.length() / 2), centerY, requirement);
                    graphics.drawString(Math.max(1, centerX - current.length() / 2), centerY + 1, current);
                } else {
                    graphics.fillRect(0, 0, getWidth(), getHeight(), '!');
                }
            }

            @Override
            public void paint(JLineGraphics graphics) {
                String message = "Console too small!";
                String requirement = String.format("Required: %dx%d", minWidth, minHeight);
                String current = String.format("Current: %dx%d", getWidth(), getHeight());

                if (getWidth() > 10 && getHeight() > 5) {
                    int centerX = getWidth() / 2;
                    int centerY = getHeight() / 2;

                    // Draw border with red color for warning
                    graphics.setStyle(org.jline.utils.AttributedStyle.DEFAULT
                        .foreground(org.jline.utils.AttributedStyle.RED)
                        .bold());
                    graphics.drawRect(0, 0, getWidth(), getHeight(), '#');

                    // Draw warning message in bright red
                    graphics.setStyle(org.jline.utils.AttributedStyle.DEFAULT
                        .foreground(org.jline.utils.AttributedStyle.BRIGHT + org.jline.utils.AttributedStyle.RED)
                        .bold());
                    graphics.drawString(Math.max(1, centerX - message.length() / 2), centerY - 1, message);

                    // Draw requirement in yellow
                    graphics.setStyle(org.jline.utils.AttributedStyle.DEFAULT
                        .foreground(org.jline.utils.AttributedStyle.YELLOW));
                    graphics.drawString(Math.max(1, centerX - requirement.length() / 2), centerY, requirement);

                    // Draw current size in white
                    graphics.setStyle(org.jline.utils.AttributedStyle.DEFAULT
                        .foreground(org.jline.utils.AttributedStyle.WHITE));
                    graphics.drawString(Math.max(1, centerX - current.length() / 2), centerY + 1, current);
                } else {
                    // For very small terminals, fill with warning character
                    graphics.setStyle(org.jline.utils.AttributedStyle.DEFAULT
                        .foreground(org.jline.utils.AttributedStyle.BRIGHT + org.jline.utils.AttributedStyle.RED)
                        .bold());
                    graphics.fillRect(0, 0, getWidth(), getHeight(), '!');
                }
            }
        };
    }

    private void updateDisplay() {
        removeAllChildren();

        if (warningCanvas != null) {
            warningCanvas.setWidth(getWidth());
            warningCanvas.setHeight(getHeight());
        }

        if (meetsMinimumSize() && contentCanvas != null) {
            addChild(contentCanvas);
        } else if (warningCanvas != null) {
            addChild(warningCanvas);
        }
    }
}
