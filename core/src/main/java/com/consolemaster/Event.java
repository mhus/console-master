package com.consolemaster;

/**
 * Base interface for all events in the console framework.
 */
public interface Event {

    /**
     * Checks if this event has been consumed.
     * Consumed events will not be passed to further handlers.
     *
     * @return true if the event has been consumed
     */
    boolean isConsumed();

    /**
     * Marks this event as consumed.
     * This prevents further handlers from processing the event.
     */
    void consume();

    /**
     * Gets the timestamp when this event was created.
     *
     * @return the event timestamp in milliseconds
     */
    long getTimestamp();
}
