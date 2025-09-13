package com.consolemaster;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages focus for Canvas components within a ScreenCanvas.
 * Handles focus traversal (forward/backward) and focus granting.
 */
@Getter
public class FocusManager {

    private final ScreenCanvas screenCanvas;
    private Canvas focusedCanvas;
    private final List<Canvas> focusableCanvases = new ArrayList<>();

    /**
     * Creates a new FocusManager for the specified ScreenCanvas.
     *
     * @param screenCanvas the screen canvas to manage focus for
     */
    public FocusManager(ScreenCanvas screenCanvas) {
        this.screenCanvas = screenCanvas;
    }

    /**
     * Sets focus to the specified canvas.
     *
     * @param canvas the canvas to focus, or null to clear focus
     * @return true if focus was successfully set, false otherwise
     */
    public boolean setFocus(Canvas canvas) {
        if (canvas != null && (!canvas.isCanFocus() || !canvas.isVisible())) {
            return false;
        }

        // Remove focus from current canvas
        if (focusedCanvas != null) {
            focusedCanvas.setHasFocus(false);
        }

        // Set focus to new canvas
        focusedCanvas = canvas;
        if (focusedCanvas != null) {
            focusedCanvas.setHasFocus(true);
        }

        return true;
    }

    /**
     * Moves focus to the next focusable canvas in the traversal order.
     *
     * @return true if focus was moved, false if no next canvas is available
     */
    public boolean focusNext() {
        updateFocusableCanvases();

        if (focusableCanvases.isEmpty()) {
            return false;
        }

        int currentIndex = focusedCanvas != null ? focusableCanvases.indexOf(focusedCanvas) : -1;
        int nextIndex = (currentIndex + 1) % focusableCanvases.size();

        return setFocus(focusableCanvases.get(nextIndex));
    }

    /**
     * Moves focus to the previous focusable canvas in the traversal order.
     *
     * @return true if focus was moved, false if no previous canvas is available
     */
    public boolean focusPrevious() {
        updateFocusableCanvases();

        if (focusableCanvases.isEmpty()) {
            return false;
        }

        int currentIndex = focusedCanvas != null ? focusableCanvases.indexOf(focusedCanvas) : -1;
        int previousIndex = currentIndex <= 0 ? focusableCanvases.size() - 1 : currentIndex - 1;

        return setFocus(focusableCanvases.get(previousIndex));
    }

    /**
     * Sets focus to the first focusable canvas.
     *
     * @return true if focus was set, false if no focusable canvas is available
     */
    public boolean focusFirst() {
        updateFocusableCanvases();

        if (focusableCanvases.isEmpty()) {
            return false;
        }

        return setFocus(focusableCanvases.get(0));
    }

    /**
     * Sets focus to the last focusable canvas.
     *
     * @return true if focus was set, false if no focusable canvas is available
     */
    public boolean focusLast() {
        updateFocusableCanvases();

        if (focusableCanvases.isEmpty()) {
            return false;
        }

        return setFocus(focusableCanvases.get(focusableCanvases.size() - 1));
    }

    /**
     * Clears the current focus.
     */
    public void clearFocus() {
        setFocus(null);
    }

    /**
     * Checks if any canvas currently has focus.
     *
     * @return true if a canvas has focus, false otherwise
     */
    public boolean hasFocus() {
        return focusedCanvas != null && focusedCanvas.isHasFocus();
    }

    /**
     * Gets the number of focusable canvases.
     *
     * @return the count of focusable canvases
     */
    public int getFocusableCount() {
        updateFocusableCanvases();
        return focusableCanvases.size();
    }

    /**
     * Requests focus for the specified canvas.
     * This is the main entry point for canvas focus requests.
     *
     * @param canvas the canvas requesting focus
     * @return true if focus was granted, false otherwise
     */
    public boolean requestFocus(Canvas canvas) {
        if (canvas == null || !canvas.requestFocus()) {
            return false;
        }

        return setFocus(canvas);
    }

    /**
     * Updates the list of focusable canvases by traversing the screen canvas tree.
     */
    private void updateFocusableCanvases() {
        focusableCanvases.clear();

        if (screenCanvas.getContentCanvas() != null) {
            collectFocusableCanvases(screenCanvas.getContentCanvas(), focusableCanvases);
        }

        // Remove any canvas that is no longer focusable or visible
        focusableCanvases.removeIf(canvas -> !canvas.isCanFocus() || !canvas.isVisible());

        // If currently focused canvas is no longer focusable, clear focus
        if (focusedCanvas != null && !focusableCanvases.contains(focusedCanvas)) {
            clearFocus();
        }
    }

    /**
     * Recursively collects all focusable canvases from the canvas tree.
     *
     * @param canvas the root canvas to start collection from
     * @param result the list to add focusable canvases to
     */
    private void collectFocusableCanvases(Canvas canvas, List<Canvas> result) {
        if (canvas == null || !canvas.isVisible()) {
            return;
        }

        // Add canvas if it can receive focus
        if (canvas.isCanFocus()) {
            result.add(canvas);
        }

        // Recursively check children if this is a composite canvas
        if (canvas instanceof CompositeCanvas composite) {
            for (Canvas child : composite.getChildren()) {
                collectFocusableCanvases(child, result);
            }
        }
    }

    /**
     * Called when a canvas is added to the screen.
     * Updates the focusable canvas list.
     *
     * @param canvas the canvas that was added
     */
    public void onCanvasAdded(Canvas canvas) {
        updateFocusableCanvases();

        // If no canvas has focus and this canvas can receive focus, focus it
        if (focusedCanvas == null && canvas.isCanFocus() && canvas.isVisible()) {
            setFocus(canvas);
        }
    }

    /**
     * Called when a canvas is removed from the screen.
     * Updates the focusable canvas list and handles focus changes.
     *
     * @param canvas the canvas that was removed
     */
    public void onCanvasRemoved(Canvas canvas) {
        // If the removed canvas had focus, move focus to next available canvas
        if (focusedCanvas == canvas) {
            focusedCanvas = null;
            updateFocusableCanvases();
            if (!focusableCanvases.isEmpty()) {
                setFocus(focusableCanvases.get(0));
            }
        } else {
            updateFocusableCanvases();
        }
    }

    /**
     * Called when canvas properties change (visibility, focusability).
     * Updates the focus state accordingly.
     *
     * @param canvas the canvas that changed
     */
    public void onCanvasChanged(Canvas canvas) {
        updateFocusableCanvases();
    }
}
