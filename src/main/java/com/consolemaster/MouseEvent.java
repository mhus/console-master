package com.consolemaster;

import lombok.Getter;

/**
 * Event representing mouse input including position, button states, and actions.
 */
@Getter
public class MouseEvent implements Event {

    /**
     * Mouse button constants.
     */
    public enum Button {
        LEFT, RIGHT, MIDDLE, NONE
    }

    /**
     * Mouse action types.
     */
    public enum Action {
        PRESS, RELEASE, CLICK, DOUBLE_CLICK, MOVE, DRAG, WHEEL_UP, WHEEL_DOWN
    }

    private final int x;
    private final int y;
    private final Button button;
    private final Action action;
    private final boolean hasShift;
    private final boolean hasCtrl;
    private final boolean hasAlt;
    private final long timestamp;
    private boolean consumed = false;

    /**
     * Creates a MouseEvent with all parameters.
     */
    public MouseEvent(int x, int y, Button button, Action action,
                     boolean hasShift, boolean hasCtrl, boolean hasAlt) {
        this.x = x;
        this.y = y;
        this.button = button;
        this.action = action;
        this.hasShift = hasShift;
        this.hasCtrl = hasCtrl;
        this.hasAlt = hasAlt;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * Creates a MouseEvent without modifier keys.
     */
    public MouseEvent(int x, int y, Button button, Action action) {
        this(x, y, button, action, false, false, false);
    }

    /**
     * Checks if this is a button press event.
     */
    public boolean isPress() {
        return action == Action.PRESS;
    }

    /**
     * Checks if this is a button release event.
     */
    public boolean isRelease() {
        return action == Action.RELEASE;
    }

    /**
     * Checks if this is a click event.
     */
    public boolean isClick() {
        return action == Action.CLICK;
    }

    /**
     * Checks if this is a double click event.
     */
    public boolean isDoubleClick() {
        return action == Action.DOUBLE_CLICK;
    }

    /**
     * Checks if this is a mouse move event.
     */
    public boolean isMove() {
        return action == Action.MOVE;
    }

    /**
     * Checks if this is a mouse drag event.
     */
    public boolean isDrag() {
        return action == Action.DRAG;
    }

    /**
     * Checks if this is a wheel event.
     */
    public boolean isWheel() {
        return action == Action.WHEEL_UP || action == Action.WHEEL_DOWN;
    }

    /**
     * Checks if the specified modifier is pressed.
     */
    public boolean hasModifier(KeyEvent.Modifier modifier) {
        return switch (modifier) {
            case SHIFT -> hasShift;
            case CTRL -> hasCtrl;
            case ALT -> hasAlt;
        };
    }

    @Override
    public boolean isConsumed() {
        return consumed;
    }

    @Override
    public void consume() {
        this.consumed = true;
    }

    @Override
    public String toString() {
        return String.format("MouseEvent{x=%d, y=%d, button=%s, action=%s, modifiers=%s%s%s}",
                           x, y, button, action,
                           hasShift ? "Shift " : "",
                           hasCtrl ? "Ctrl " : "",
                           hasAlt ? "Alt " : "");
    }
}
