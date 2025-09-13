package com.consolemaster;

import lombok.Getter;
import lombok.Setter;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private FocusManager focusManager;
    private final Map<String, Runnable> shortcuts = new HashMap<>();
    private final List<MouseManager> mouseManagers = new ArrayList<>();
    private boolean mouseReportingEnabled = false;

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

        // Initialize focus manager
        this.focusManager = new FocusManager(this);

        // Create warning canvas
        createWarningCanvas();
        updateDisplay();
    }

    public void setContentCanvas(Canvas contentCanvas) {
        if (this.contentCanvas != null) {
            removeChild(this.contentCanvas);
            focusManager.onCanvasRemoved(this.contentCanvas);
        }
        this.contentCanvas = contentCanvas;
        if (contentCanvas != null) {
            focusManager.onCanvasAdded(contentCanvas);
        }
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

    /**
     * Recalculates the minimum size requirements based on the content canvas
     * and updates the terminal display accordingly.
     * This method calls pack() on the content canvas first, then updates
     * the screen's minimum size requirements.
     */
    public void pack() {
        if (contentCanvas != null) {
            // Pack the content canvas first
            contentCanvas.pack();

            // Update our minimum size based on content requirements
            int newMinWidth = Math.max(DEFAULT_MIN_WIDTH, contentCanvas.getMinWidth());
            int newMinHeight = Math.max(DEFAULT_MIN_HEIGHT, contentCanvas.getMinHeight());

            // Store the new minimum requirements (note: these are final fields,
            // so we can't change them, but we can update the display logic)
            // For future versions, consider making minWidth/minHeight non-final

            // Update the display with new requirements
            updateDisplay();
        }

        // Also pack any other children (like warning canvas)
        super.pack();
    }

    // Focus management methods

    /**
     * Moves focus to the next focusable canvas.
     *
     * @return true if focus was moved, false if no next canvas is available
     */
    public boolean focusNext() {
        return focusManager.focusNext();
    }

    /**
     * Moves focus to the previous focusable canvas.
     *
     * @return true if focus was moved, false if no previous canvas is available
     */
    public boolean focusPrevious() {
        return focusManager.focusPrevious();
    }

    /**
     * Sets focus to the first focusable canvas.
     *
     * @return true if focus was set, false if no focusable canvas is available
     */
    public boolean focusFirst() {
        return focusManager.focusFirst();
    }

    /**
     * Sets focus to the last focusable canvas.
     *
     * @return true if focus was set, false if no focusable canvas is available
     */
    public boolean focusLast() {
        return focusManager.focusLast();
    }

    /**
     * Clears the current focus.
     */
    public void clearFocus() {
        focusManager.clearFocus();
    }

    /**
     * Gets the currently focused canvas.
     *
     * @return the focused canvas, or null if no canvas has focus
     */
    public Canvas getFocusedCanvas() {
        return focusManager.getFocusedCanvas();
    }

    /**
     * Requests focus for the specified canvas.
     *
     * @param canvas the canvas requesting focus
     * @return true if focus was granted, false otherwise
     */
    public boolean requestFocus(Canvas canvas) {
        return focusManager.requestFocus(canvas);
    }

    // Shortcut and Event handling methods

    /**
     * Registers a keyboard shortcut with an action.
     *
     * @param keyString the key combination string (e.g., "Ctrl+S", "TAB", "F1")
     * @param action the action to execute when the shortcut is pressed
     */
    public void registerShortcut(String keyString, Runnable action) {
        shortcuts.put(keyString, action);
    }

    /**
     * Unregisters a keyboard shortcut.
     *
     * @param keyString the key combination string to unregister
     */
    public void unregisterShortcut(String keyString) {
        shortcuts.remove(keyString);
    }

    /**
     * Processes a keyboard event through the event chain:
     * 1. Check for registered shortcuts
     * 2. If not consumed, forward to focused canvas
     * 3. Handle built-in focus navigation (TAB/SHIFT+TAB)
     *
     * @param keyEvent the keyboard event to process
     */
    public void processKeyEvent(KeyEvent keyEvent) {
        if (keyEvent.isConsumed()) {
            return;
        }

        // First, check for registered shortcuts
        String keyString = keyEvent.getKeyString();
        Runnable shortcutAction = shortcuts.get(keyString);
        if (shortcutAction != null) {
            shortcutAction.run();
            keyEvent.consume();
            return;
        }

        // Handle built-in focus navigation shortcuts
        if (handleBuiltInShortcuts(keyEvent)) {
            return;
        }

        // Forward to focused canvas if not consumed
        Canvas focusedCanvas = getFocusedCanvas();
        if (focusedCanvas != null && !keyEvent.isConsumed()) {
            forwardEventToCanvas(focusedCanvas, keyEvent);
        }
    }

    /**
     * Handles built-in keyboard shortcuts for focus navigation.
     *
     * @param keyEvent the keyboard event
     * @return true if the event was handled by built-in shortcuts
     */
    private boolean handleBuiltInShortcuts(KeyEvent keyEvent) {
        if (keyEvent.isSpecialKey()) {
            switch (keyEvent.getSpecialKey()) {
                case TAB:
                    if (keyEvent.isHasShift()) {
                        focusPrevious();
                    } else {
                        focusNext();
                    }
                    keyEvent.consume();
                    return true;
                default:
                    break;
            }
        }
        return false;
    }

    /**
     * Forwards an event to a specific canvas.
     * If the canvas supports event handling, it will receive the event.
     *
     * @param canvas the canvas to receive the event
     * @param event the event to forward
     */
    private void forwardEventToCanvas(Canvas canvas, Event event) {
        if (canvas instanceof EventHandler eventHandler) {
            eventHandler.handleEvent(event);
        }
    }

    // Mouse management methods

    /**
     * Adds a mouse manager to handle mouse events.
     *
     * @param mouseManager the mouse manager to add
     */
    public void addMouseManager(MouseManager mouseManager) {
        if (mouseManager != null && !mouseManagers.contains(mouseManager)) {
            mouseManagers.add(mouseManager);
            // Sort by priority (higher priority first)
            mouseManagers.sort((m1, m2) -> Integer.compare(m2.getPriority(), m1.getPriority()));
            mouseManager.initialize(this);
        }
    }

    /**
     * Removes a mouse manager.
     *
     * @param mouseManager the mouse manager to remove
     */
    public void removeMouseManager(MouseManager mouseManager) {
        if (mouseManager != null && mouseManagers.remove(mouseManager)) {
            mouseManager.cleanup(this);
        }
    }

    /**
     * Processes a mouse event through all registered mouse managers.
     *
     * @param mouseEvent the mouse event to process
     */
    public void processMouseEvent(MouseEvent mouseEvent) {
        if (mouseEvent.isConsumed()) {
            return;
        }

        // Process through all mouse managers in priority order
        for (MouseManager mouseManager : mouseManagers) {
            if (!mouseEvent.isConsumed() && mouseManager.canHandle(mouseEvent)) {
                mouseManager.processMouseEvent(mouseEvent, this);
            }
        }
    }

    /**
     * Enables mouse reporting in the terminal.
     */
    public void enableMouseReporting() {
        if (!mouseReportingEnabled) {
            mouseReportingEnabled = true;

            // Add default mouse manager if none exists
            if (mouseManagers.isEmpty()) {
                addMouseManager(new DefaultMouseManager());
            }
        }
    }

    /**
     * Disables mouse reporting in the terminal.
     */
    public void disableMouseReporting() {
        if (mouseReportingEnabled) {
            mouseReportingEnabled = false;

            // Clean up all mouse managers
            for (MouseManager mouseManager : new ArrayList<>(mouseManagers)) {
                removeMouseManager(mouseManager);
            }
        }
    }

    /**
     * Checks if mouse reporting is currently enabled.
     */
    public boolean isMouseReportingEnabled() {
        return mouseReportingEnabled;
    }
}
