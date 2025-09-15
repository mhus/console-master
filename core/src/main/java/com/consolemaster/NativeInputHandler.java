package com.consolemaster;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Native input handler that replaces JLine dependency.
 * Handles keyboard and mouse input using ANSI escape sequences.
 */
@Slf4j
@Getter
@Setter
public class NativeInputHandler implements InputHandler {

    private final Terminal terminal;
    private final BlockingQueue<Event> eventQueue = new LinkedBlockingQueue<>();
    private volatile boolean running = false;
    private Thread inputThread;
    private EventHandler eventHandler;

    // Mouse state tracking
    private int lastMouseX = -1;
    private int lastMouseY = -1;
    private long lastClickTime = 0;
    private static final long DOUBLE_CLICK_THRESHOLD = 500; // milliseconds

    public NativeInputHandler(Terminal terminal) {
        this.terminal = terminal;
    }

    @Override
    public void start() {
        if (running) return;

        running = true;
        inputThread = new Thread(this::inputLoop, "NativeInputHandler");
        inputThread.setDaemon(true);
        inputThread.start();
    }

    @Override
    public void stop() {
        running = false;
        if (inputThread != null) {
            inputThread.interrupt();
        }
    }

    @Override
    public Event pollEvent() {
        return eventQueue.poll();
    }

    @Override
    public boolean hasEvents() {
        return !eventQueue.isEmpty();
    }

    @Override
    public void clearEvents() {
        eventQueue.clear();
    }

    @Override
    public void setEventHandler(EventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }

    /**
     * Enables mouse reporting.
     */
    public void enableMouseReporting() {
        terminal.enableMouseTracking();
    }

    /**
     * Disables mouse reporting.
     */
    public void disableMouseReporting() {
        terminal.disableMouseTracking();
    }

    private void inputLoop() {
        try {
            while (running) {
                if (terminal.isInputAvailable()) {
                    int ch = terminal.read();
                    if (ch != -1) {
                        processInput(ch);
                    }
                } else {
                    // Short sleep to prevent busy waiting
                    Thread.sleep(10);
                }
            }
        } catch (InterruptedException e) {
            // Thread was interrupted, exit gracefully
        } catch (IOException e) {
            // Handle input errors
            log.warn("Input error: {}", e.getMessage(), e);
        }
    }

    private void processInput(int ch) throws IOException {
        if (ch == 27) { // ESC character - start of escape sequence
            processEscapeSequence();
        } else {
            // Regular character input
            processCharacterInput(ch);
        }
    }

    private void processEscapeSequence() throws IOException {
        // Read the next character to determine sequence type
        if (!terminal.isInputAvailable()) {
            // Just ESC key pressed
            KeyEvent escEvent = new KeyEvent(KeyEvent.SpecialKey.ESC, '\0');
            eventQueue.offer(escEvent);
            return;
        }

        int next = terminal.read();
        if (next == '[') {
            // CSI sequence (Control Sequence Introducer)
            processCSISequence();
        } else if (next == 'O') {
            // SS3 sequence (Single Shift Three) - typically function keys
            processSS3Sequence();
        } else {
            // Alt + character
            processAltSequence(next);
        }
    }

    private void processCSISequence() throws IOException {
        StringBuilder sequence = new StringBuilder();
        int ch;

        // Read until we get a letter (sequence terminator)
        while (terminal.isInputAvailable()) {
            ch = terminal.read();
            sequence.append((char) ch);

            if (Character.isLetter(ch)) {
                break;
            }
        }

        String seq = sequence.toString();
        log.trace("CSI Sequence: {}", seq);

        // Parse common sequences format <?;?;?M or <?;?;?m for mouse events
        if (seq.matches("<?\\d+;\\d+;\\d+[Mm]")) {
            log.trace("Mouse event detected: {}", seq);
            // Mouse event: ESC[M followed by button;x;y or ESC[<button;x;yM/m
            parseMouseEvent(seq);
        } else if (seq.equals("A")) {
            // Up arrow
            eventQueue.offer(new KeyEvent(KeyEvent.SpecialKey.ARROW_UP, '\0'));
        } else if (seq.equals("B")) {
            // Down arrow
            eventQueue.offer(new KeyEvent(KeyEvent.SpecialKey.ARROW_DOWN, '\0'));
        } else if (seq.equals("C")) {
            // Right arrow
            eventQueue.offer(new KeyEvent(KeyEvent.SpecialKey.ARROW_RIGHT, '\0'));
        } else if (seq.equals("D")) {
            // Left arrow
            eventQueue.offer(new KeyEvent(KeyEvent.SpecialKey.ARROW_LEFT, '\0'));
        } else if (seq.equals("H")) {
            // Home
            eventQueue.offer(new KeyEvent(KeyEvent.SpecialKey.HOME, '\0'));
        } else if (seq.equals("F")) {
            // End
            eventQueue.offer(new KeyEvent(KeyEvent.SpecialKey.END, '\0'));
        } else if (seq.matches("\\d+~")) {
            // Function keys and other special keys
            parseTildeSequence(seq);
        }
    }

    private void processSS3Sequence() throws IOException {
        if (terminal.isInputAvailable()) {
            int ch = terminal.read();
            // Function keys F1-F4 in some terminals
            switch (ch) {
                case 'P' -> eventQueue.offer(new KeyEvent(KeyEvent.SpecialKey.F1, '\0'));
                case 'Q' -> eventQueue.offer(new KeyEvent(KeyEvent.SpecialKey.F2, '\0'));
                case 'R' -> eventQueue.offer(new KeyEvent(KeyEvent.SpecialKey.F3, '\0'));
                case 'S' -> eventQueue.offer(new KeyEvent(KeyEvent.SpecialKey.F4, '\0'));
            }
        }
    }

    private void processAltSequence(int ch) {
        // Alt + character combination
        KeyEvent.Modifier[] modifiers = {KeyEvent.Modifier.ALT};
        KeyEvent altEvent = new KeyEvent(null, (char) ch, modifiers);
        eventQueue.offer(altEvent);
    }

    private void processCharacterInput(int ch) {
        KeyEvent.Modifier[] modifiers = null;
        KeyEvent.SpecialKey specialKey = null;
        char character = (char) ch;

        // Handle special characters
        switch (ch) {
            case 9 -> specialKey = KeyEvent.SpecialKey.TAB;
            case 10, 13 -> specialKey = KeyEvent.SpecialKey.ENTER;
            case 127 -> specialKey = KeyEvent.SpecialKey.BACKSPACE;
            case 8 -> specialKey = KeyEvent.SpecialKey.BACKSPACE; // Alternative backspace
            default -> {
                // Check for Ctrl combinations
                if (ch < 32) {
                    modifiers = new KeyEvent.Modifier[]{KeyEvent.Modifier.CTRL};
                    character = (char) (ch + 64); // Convert Ctrl+A (1) to 'A'
                }
            }
        }

        KeyEvent keyEvent = new KeyEvent(specialKey, character, modifiers);
        eventQueue.offer(keyEvent);
    }

    private void parseTildeSequence(String seq) {
        try {
            int code = Integer.parseInt(seq.substring(0, seq.length() - 1));
            KeyEvent.SpecialKey specialKey = switch (code) {
                case 2 -> KeyEvent.SpecialKey.INSERT;
                case 3 -> KeyEvent.SpecialKey.DELETE;
                case 5 -> KeyEvent.SpecialKey.PAGE_UP;
                case 6 -> KeyEvent.SpecialKey.PAGE_DOWN;
                case 11 -> KeyEvent.SpecialKey.F1;
                case 12 -> KeyEvent.SpecialKey.F2;
                case 13 -> KeyEvent.SpecialKey.F3;
                case 14 -> KeyEvent.SpecialKey.F4;
                case 15 -> KeyEvent.SpecialKey.F5;
                case 17 -> KeyEvent.SpecialKey.F6;
                case 18 -> KeyEvent.SpecialKey.F7;
                case 19 -> KeyEvent.SpecialKey.F8;
                case 20 -> KeyEvent.SpecialKey.F9;
                case 21 -> KeyEvent.SpecialKey.F10;
                case 23 -> KeyEvent.SpecialKey.F11;
                case 24 -> KeyEvent.SpecialKey.F12;
                default -> null;
            };

            if (specialKey != null) {
                eventQueue.offer(new KeyEvent(specialKey, '\0'));
            }
        } catch (NumberFormatException e) {
            // Ignore malformed sequences
            log.debug("Malformed tilde sequence: {}", seq);
        }
    }

    private void parseMouseEvent(String seq) {
        try {
            // Parse mouse sequences like "0;33;10M" or "<0;33;10M"
            String[] parts;
            boolean isPress = seq.endsWith("M");

            if (seq.startsWith("<")) {
                // SGR mouse format
                parts = seq.substring(1, seq.length() - 1).split(";");
            } else {
                // Standard mouse format
                parts = seq.substring(0, seq.length() - 1).split(";");
            }

            if (parts.length >= 3) {
                int button = Integer.parseInt(parts[0]);
                int x = Integer.parseInt(parts[1]) - 1; // Convert to 0-based
                int y = Integer.parseInt(parts[2]) - 1; // Convert to 0-based

                MouseEvent.Button mouseButton = parseMouseButton(button);
                MouseEvent.Action action = parseMouseAction(button, isPress, x, y);

                MouseEvent mouseEvent = new MouseEvent(mouseButton, action, x, y);
                eventQueue.offer(mouseEvent);

                lastMouseX = x;
                lastMouseY = y;
            }
        } catch (NumberFormatException e) {
            // Ignore malformed mouse sequences
            log.debug("Malformed mouse event sequence: {}", seq);
        }
    }

    private MouseEvent.Button parseMouseButton(int button) {
        return switch (button & 3) {
            case 0 -> MouseEvent.Button.LEFT;
            case 1 -> MouseEvent.Button.MIDDLE;
            case 2 -> MouseEvent.Button.RIGHT;
            default -> MouseEvent.Button.NONE;
        };
    }

    private MouseEvent.Action parseMouseAction(int button, boolean isPress, int x, int y) {
        if ((button & 32) != 0) {
            // Mouse movement/drag
            return (button & 3) != 3 ? MouseEvent.Action.DRAG : MouseEvent.Action.MOVE;
        }

        if ((button & 64) != 0) {
            // Mouse wheel
            return (button & 1) == 0 ? MouseEvent.Action.WHEEL_UP : MouseEvent.Action.WHEEL_DOWN;
        }

        if (isPress) {
            // Check for double-click
            long currentTime = System.currentTimeMillis();
            if (x == lastMouseX && y == lastMouseY &&
                currentTime - lastClickTime < DOUBLE_CLICK_THRESHOLD) {
                lastClickTime = 0; // Reset to prevent triple-click
                return MouseEvent.Action.DOUBLE_CLICK;
            }
            lastClickTime = currentTime;
            return MouseEvent.Action.PRESS;
        } else {
            return MouseEvent.Action.RELEASE;
        }
    }
}
