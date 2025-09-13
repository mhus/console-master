package com.consolemaster;

/**
 * Interface for input handling implementations.
 * Defines the contract for handling keyboard and mouse input events.
 */
public interface InputHandler {

    /**
     * Starts the input handler.
     */
    void start();

    /**
     * Stops the input handler.
     */
    void stop();

    /**
     * Polls for the next available event.
     *
     * @return the next event, or null if no events are available
     */
    Event pollEvent();

    /**
     * Checks if events are available.
     *
     * @return true if events are available, false otherwise
     */
    boolean hasEvents();

    /**
     * Clears all pending events.
     */
    void clearEvents();

    /**
     * Sets an event handler to process events.
     *
     * @param eventHandler the event handler
     */
    void setEventHandler(EventHandler eventHandler);
}
