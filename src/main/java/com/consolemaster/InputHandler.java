package com.consolemaster;

import org.jline.terminal.Terminal;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Handles non-blocking input from the console terminal.
 * Runs in a separate thread to capture keyboard input without blocking the main game loop.
 */
public class InputHandler implements Runnable {

    private final Terminal terminal;
    private final BlockingQueue<KeyEvent> eventQueue;
    private final BlockingQueue<MouseEvent> mouseEventQueue;
    private volatile boolean running = false;
    private Thread inputThread;
    private boolean mouseReportingEnabled = false;

    public InputHandler(Terminal terminal) {
        this.terminal = terminal;
        this.eventQueue = new LinkedBlockingQueue<>();
        this.mouseEventQueue = new LinkedBlockingQueue<>();
    }

    /**
     * Starts the input handler in a separate thread.
     */
    public void start() {
        if (!running) {
            running = true;
            inputThread = new Thread(this, "InputHandler");
            inputThread.setDaemon(true);
            inputThread.start();
        }
    }

    /**
     * Stops the input handler.
     */
    public void stop() {
        running = false;
        if (inputThread != null) {
            inputThread.interrupt();
        }
    }

    /**
     * Polls for available key events without blocking.
     *
     * @return the next KeyEvent or null if none available
     */
    public KeyEvent pollEvent() {
        return eventQueue.poll();
    }

    /**
     * Polls for available mouse events without blocking.
     *
     * @return the next MouseEvent or null if none available
     */
    public MouseEvent pollMouseEvent() {
        return mouseEventQueue.poll();
    }

    /**
     * Checks if there are any pending events.
     */
    public boolean hasEvents() {
        return !eventQueue.isEmpty();
    }

    /**
     * Checks if there are any pending mouse events.
     */
    public boolean hasMouseEvents() {
        return !mouseEventQueue.isEmpty();
    }

    /**
     * Enables or disables mouse reporting.
     *
     * @param enabled true to enable mouse reporting, false to disable
     */
    public void setMouseReportingEnabled(boolean enabled) {
        this.mouseReportingEnabled = enabled;
    }

    @Override
    public void run() {
        try {
            terminal.enterRawMode();

            while (running && !Thread.currentThread().isInterrupted()) {
                try {
                    // Non-blocking read with timeout
                    if (terminal.reader().ready()) {
                        int ch = terminal.reader().read();
                        KeyEvent event = parseInput(ch);
                        if (event != null) {
                            eventQueue.offer(event);
                        }
                    } else {
                        // Small sleep to prevent busy waiting
                        Thread.sleep(10);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    if (running) {
                        System.err.println("Error reading input: " + e.getMessage());
                    }
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("Error setting up terminal: " + e.getMessage());
        }
    }

    /**
     * Parses raw input into KeyEvent objects.
     */
    private KeyEvent parseInput(int ch) {
        boolean hasShift = false;
        boolean hasCtrl = false;
        boolean hasAlt = false;

        // Handle escape sequences for special keys
        if (ch == 27) { // ESC
            try {
                if (terminal.reader().ready()) {
                    int next = terminal.reader().read();
                    if (next == '[') {
                        // ANSI escape sequence
                        return parseAnsiSequence();
                    } else if (next == 'O') {
                        // Alternative escape sequence
                        return parseAlternativeSequence();
                    } else {
                        // Alt modifier + character
                        hasAlt = true;
                        ch = next;
                    }
                } else {
                    // Just ESC key
                    return new KeyEvent(KeyEvent.SpecialKey.ESCAPE, false, false, false);
                }
            } catch (Exception e) {
                return new KeyEvent(KeyEvent.SpecialKey.ESCAPE, false, false, false);
            }
        }

        // Handle control characters
        if (ch < 32) {
            return parseControlCharacter(ch);
        }

        // Handle printable characters
        if (ch >= 32 && ch < 127) {
            char character = (char) ch;
            // Check for shift (uppercase letters and shifted symbols)
            if (Character.isUpperCase(character) || isShiftedSymbol(character)) {
                hasShift = true;
            }
            return new KeyEvent(character, hasShift, hasCtrl, hasAlt);
        }

        // Handle extended ASCII or other characters
        return new KeyEvent((char) ch, hasShift, hasCtrl, hasAlt);
    }

    /**
     * Parses ANSI escape sequences (ESC[...)
     */
    private KeyEvent parseAnsiSequence() {
        try {
            if (terminal.reader().ready()) {
                int code = terminal.reader().read();

                // Check for mouse reporting sequences first
                if (code == 'M' && mouseReportingEnabled) {
                    MouseEvent mouseEvent = parseMouseSequence();
                    if (mouseEvent != null) {
                        mouseEventQueue.offer(mouseEvent);
                        return null; // Mouse event handled, no key event
                    }
                }

                return switch (code) {
                    case 'A' -> new KeyEvent(KeyEvent.SpecialKey.ARROW_UP, false, false, false);
                    case 'B' -> new KeyEvent(KeyEvent.SpecialKey.ARROW_DOWN, false, false, false);
                    case 'C' -> new KeyEvent(KeyEvent.SpecialKey.ARROW_RIGHT, false, false, false);
                    case 'D' -> new KeyEvent(KeyEvent.SpecialKey.ARROW_LEFT, false, false, false);
                    case 'H' -> new KeyEvent(KeyEvent.SpecialKey.HOME, false, false, false);
                    case 'F' -> new KeyEvent(KeyEvent.SpecialKey.END, false, false, false);
                    default -> {
                        // Handle sequences like [5~ for Page Up, [6~ for Page Down
                        if (code >= '1' && code <= '6') {
                            if (terminal.reader().ready() && terminal.reader().read() == '~') {
                                yield switch (code) {
                                    case '5' -> new KeyEvent(KeyEvent.SpecialKey.PAGE_UP, false, false, false);
                                    case '6' -> new KeyEvent(KeyEvent.SpecialKey.PAGE_DOWN, false, false, false);
                                    case '3' -> new KeyEvent(KeyEvent.SpecialKey.DELETE, false, false, false);
                                    default -> null;
                                };
                            }
                        }
                        yield null;
                    }
                };
            }
        } catch (Exception e) {
            // Return null on parsing errors
        }
        return null;
    }

    /**
     * Parses alternative escape sequences (ESCO...)
     */
    private KeyEvent parseAlternativeSequence() {
        try {
            if (terminal.reader().ready()) {
                int code = terminal.reader().read();
                return switch (code) {
                    case 'P' -> new KeyEvent(KeyEvent.SpecialKey.F1, false, false, false);
                    case 'Q' -> new KeyEvent(KeyEvent.SpecialKey.F2, false, false, false);
                    case 'R' -> new KeyEvent(KeyEvent.SpecialKey.F3, false, false, false);
                    case 'S' -> new KeyEvent(KeyEvent.SpecialKey.F4, false, false, false);
                    default -> null;
                };
            }
        } catch (Exception e) {
            // Return null on parsing errors
        }
        return null;
    }

    /**
     * Parses control characters (0-31).
     */
    private KeyEvent parseControlCharacter(int ch) {
        return switch (ch) {
            case 9 -> new KeyEvent(KeyEvent.SpecialKey.TAB, false, false, false);
            case 10, 13 -> new KeyEvent(KeyEvent.SpecialKey.ENTER, false, false, false);
            case 8, 127 -> new KeyEvent(KeyEvent.SpecialKey.BACKSPACE, false, false, false);
            default -> {
                // Other control characters (Ctrl+A = 1, Ctrl+B = 2, etc.)
                if (ch >= 1 && ch <= 26) {
                    char character = (char) ('A' + ch - 1);
                    yield new KeyEvent(character, false, true, false);
                }
                yield null;
            }
        };
    }

    /**
     * Parses mouse reporting sequences (ESC[M...)
     * Mouse format: ESC[M<button><x><y>
     */
    private MouseEvent parseMouseSequence() {
        try {
            if (terminal.reader().ready()) {
                int buttonCode = terminal.reader().read();
                if (terminal.reader().ready()) {
                    int x = terminal.reader().read() - 33; // Convert from terminal coordinates
                    if (terminal.reader().ready()) {
                        int y = terminal.reader().read() - 33; // Convert from terminal coordinates

                        return parseMouseButton(buttonCode, x, y);
                    }
                }
            }
        } catch (Exception e) {
            // Return null on parsing errors
        }
        return null;
    }

    /**
     * Parses the mouse button code and creates appropriate MouseEvent.
     */
    private MouseEvent parseMouseButton(int buttonCode, int x, int y) {
        // Extract modifiers
        boolean hasShift = (buttonCode & 4) != 0;
        boolean hasCtrl = (buttonCode & 16) != 0;
        boolean hasAlt = (buttonCode & 8) != 0;

        // Extract button and action
        int baseCode = buttonCode & 3;
        MouseEvent.Button button;
        MouseEvent.Action action;

        // Check for wheel events
        if ((buttonCode & 64) != 0) {
            button = MouseEvent.Button.NONE;
            action = (baseCode == 0) ? MouseEvent.Action.WHEEL_UP : MouseEvent.Action.WHEEL_DOWN;
        } else {
            // Regular button events
            button = switch (baseCode) {
                case 0 -> MouseEvent.Button.LEFT;
                case 1 -> MouseEvent.Button.MIDDLE;
                case 2 -> MouseEvent.Button.RIGHT;
                default -> MouseEvent.Button.NONE;
            };

            // Determine action based on button code
            if ((buttonCode & 32) != 0) {
                action = MouseEvent.Action.DRAG;
            } else if (baseCode == 3) {
                action = MouseEvent.Action.RELEASE;
                button = MouseEvent.Button.NONE; // Release doesn't specify which button
            } else {
                action = MouseEvent.Action.PRESS;
            }
        }

        return new MouseEvent(x, y, button, action, hasShift, hasCtrl, hasAlt);
    }

    /**
     * Enables mouse reporting in the terminal.
     */
    public void enableMouseReporting() {
        try {
            // Enable mouse tracking
            terminal.writer().print("\033[?1000h"); // Basic mouse reporting
            terminal.writer().print("\033[?1002h"); // Button event tracking
            terminal.writer().print("\033[?1015h"); // Extended mouse mode
            terminal.writer().print("\033[?1006h"); // SGR mouse mode
            terminal.flush();
            mouseReportingEnabled = true;
        } catch (Exception e) {
            System.err.println("Error enabling mouse reporting: " + e.getMessage());
        }
    }

    /**
     * Disables mouse reporting in the terminal.
     */
    public void disableMouseReporting() {
        try {
            // Disable mouse tracking
            terminal.writer().print("\033[?1006l"); // SGR mouse mode
            terminal.writer().print("\033[?1015l"); // Extended mouse mode
            terminal.writer().print("\033[?1002l"); // Button event tracking
            terminal.writer().print("\033[?1000l"); // Basic mouse reporting
            terminal.flush();
            mouseReportingEnabled = false;
        } catch (Exception e) {
            System.err.println("Error disabling mouse reporting: " + e.getMessage());
        }
    }

    /**
     * Checks if a character requires shift to type.
     */
    private boolean isShiftedSymbol(char ch) {
        return "!@#$%^&*()_+{}|:\"<>?".indexOf(ch) != -1;
    }
}
