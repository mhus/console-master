package com.consolemaster;

/**
 * Interface for animation components that need regular updates.
 * Components implementing this interface can be registered with an AnimationManager
 * to receive periodic tick calls for animation updates.
 */
public interface AnimationTicker {

    /**
     * Called periodically to update the animation state.
     *
     * @return true if the animation requires a screen update (redraw), false otherwise
     */
    boolean tick();
}
