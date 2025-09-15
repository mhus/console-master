package com.consolemaster;

import lombok.Getter;

/**
 * Event representing keyboard input with support for special keys and modifiers.
 */
@Getter
public class KeyEvent implements Event {

    /**
     * Special key constants for non-printable keys.
     */
    public enum SpecialKey {
        TAB, ENTER, ESCAPE, ESC, BACKSPACE, DELETE, INSERT,
        ARROW_UP, ARROW_DOWN, ARROW_LEFT, ARROW_RIGHT,
        HOME, END, PAGE_UP, PAGE_DOWN,
        F1, F2, F3, F4, F5, F6, F7, F8, F9, F10, F11, F12
    }

    /**
     * Key modifiers.
     */
    public enum Modifier {
        SHIFT, CTRL, ALT
    }

    private final char character;
    private final SpecialKey specialKey;
    private final boolean hasShift;
    private final boolean hasCtrl;
    private final boolean hasAlt;
    private final long timestamp;
    private boolean consumed = false;

    /**
     * Creates a KeyEvent for a printable character.
     */
    public KeyEvent(char character, boolean hasShift, boolean hasCtrl, boolean hasAlt) {
        this.character = character;
        this.specialKey = null;
        this.hasShift = hasShift;
        this.hasCtrl = hasCtrl;
        this.hasAlt = hasAlt;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * Creates a KeyEvent for a special key.
     */
    public KeyEvent(SpecialKey specialKey, boolean hasShift, boolean hasCtrl, boolean hasAlt) {
        this.character = '\0';
        this.specialKey = specialKey;
        this.hasShift = hasShift;
        this.hasCtrl = hasCtrl;
        this.hasAlt = hasAlt;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * Simplified constructor for special keys without modifiers.
     */
    public KeyEvent(SpecialKey specialKey, char character) {
        this.character = character;
        this.specialKey = specialKey;
        this.hasShift = false;
        this.hasCtrl = false;
        this.hasAlt = false;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * Constructor with modifiers array.
     */
    public KeyEvent(SpecialKey specialKey, char character, Modifier[] modifiers) {
        this.character = character;
        this.specialKey = specialKey;
        this.hasShift = modifiers != null && java.util.Arrays.asList(modifiers).contains(Modifier.SHIFT);
        this.hasCtrl = modifiers != null && java.util.Arrays.asList(modifiers).contains(Modifier.CTRL);
        this.hasAlt = modifiers != null && java.util.Arrays.asList(modifiers).contains(Modifier.ALT);
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * Checks if this is a printable character event.
     */
    public boolean isCharacter() {
        return specialKey == null && character != '\0';
    }

    /**
     * Checks if this is a special key event.
     */
    public boolean isSpecialKey() {
        return specialKey != null;
    }

    /**
     * Checks if the specified modifier is pressed.
     */
    public boolean hasModifier(Modifier modifier) {
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

    /**
     * Creates a string representation of the key combination for shortcuts.
     */
    public String getKeyString() {
        StringBuilder sb = new StringBuilder();

        if (hasCtrl) sb.append("Ctrl+");
        if (hasAlt) sb.append("Alt+");
        if (hasShift) sb.append("Shift+");

        if (isSpecialKey()) {
            sb.append(specialKey.name());
        } else if (isCharacter()) {
            sb.append(Character.toUpperCase(character));
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        return "KeyEvent{" + getKeyString() + "}";
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Alias for hasShift.
     */
    public boolean isHasShift() {
        return hasShift;
    }

    /**
     * Gets the special key.
     */
    public SpecialKey getSpecialKey() {
        return specialKey;
    }
}
