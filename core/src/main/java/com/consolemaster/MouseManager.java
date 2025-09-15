package com.consolemaster;

/**
 * Interface for mouse event management strategies.
 * Different implementations can handle mouse events in various ways,
 * such as forwarding to canvas hierarchy or global handling.
 */
public interface MouseManager {

    /**
     * Processes a mouse event according to the manager's strategy.
     *
     * @param mouseEvent the mouse event to process
     * @param screenCanvas the screen canvas context
     */
    void processMouseEvent(MouseEvent mouseEvent, ScreenCanvas screenCanvas);

    /**
     * Called when the mouse manager is initialized.
     *
     * @param screenCanvas the screen canvas context
     */
    default void initialize(ScreenCanvas screenCanvas) {
        // Default implementation does nothing
    }

    /**
     * Called when the mouse manager is being cleaned up.
     *
     * @param screenCanvas the screen canvas context
     */
    default void cleanup(ScreenCanvas screenCanvas) {
        // Default implementation does nothing
    }

    /**
     * Checks if this manager can handle the specified mouse event.
     *
     * @param mouseEvent the mouse event to check
     * @return true if this manager can process the event
     */
    default boolean canHandle(MouseEvent mouseEvent) {
        return true; // Default: handle all events
    }

    /**
     * Gets the priority of this mouse manager.
     * Higher priority managers are processed first.
     *
     * @return the priority value (higher = more priority)
     */
    default int getPriority() {
        return 0; // Default priority
    }
}
