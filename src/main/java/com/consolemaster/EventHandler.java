package com.consolemaster;

/**
 * Interface for components that can handle events.
 * Canvas components can implement this interface to receive keyboard events
 * and other types of events from the event system.
 */
public interface EventHandler {

    /**
     * Handles an event sent to this component.
     * The component should check the event type and handle it appropriately.
     * If the event is handled, it should be marked as consumed.
     *
     * @param event the event to handle
     */
    void handleEvent(Event event);

    /**
     * Checks if this handler can handle the specified event type.
     * Default implementation accepts all events.
     *
     * @param event the event to check
     * @return true if this handler can process the event
     */
    default boolean canHandle(Event event) {
        return true;
    }
}
