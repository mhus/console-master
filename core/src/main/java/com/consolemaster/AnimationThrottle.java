package com.consolemaster;

import lombok.Getter;
import lombok.Setter;

/**
 * A throttling wrapper that controls the execution frequency of AnimationTicker implementations.
 * This class allows slowing down animations by introducing configurable delays between tick() executions.
 *
 * The throttle sits between the AnimationManager and the actual animation implementation,
 * enabling fine-grained control over animation speed without modifying the original ticker.
 */
@Getter
@Setter
public class AnimationThrottle implements AnimationTicker {

    private final AnimationTicker target;
    private long lastExecutionTime = 0;
    private long delayMillis;
    private boolean enabled = true;

    /**
     * Creates a new AnimationThrottle with the specified target and delay.
     *
     * @param target the actual AnimationTicker to wrap
     * @param delayMillis the minimum delay in milliseconds between tick() executions
     */
    public AnimationThrottle(AnimationTicker target, long delayMillis) {
        if (target == null) {
            throw new IllegalArgumentException("Target AnimationTicker cannot be null");
        }
        if (delayMillis < 0) {
            throw new IllegalArgumentException("Delay cannot be negative");
        }
        this.target = target;
        this.delayMillis = delayMillis;
    }

    /**
     * Creates a new AnimationThrottle with the specified target and no delay.
     *
     * @param target the actual AnimationTicker to wrap
     */
    public AnimationThrottle(AnimationTicker target) {
        this(target, 0);
    }

    /**
     * Executes the target's tick() method only if enough time has passed since the last execution.
     *
     * @return true if the target's tick() was executed and returned true, false otherwise
     */
    @Override
    public boolean tick() {
        if (!enabled) {
            return false;
        }

        long currentTime = System.currentTimeMillis();

        // Check if enough time has passed since last execution
        if (currentTime - lastExecutionTime >= delayMillis) {
            lastExecutionTime = currentTime;
            return target.tick();
        }

        return false; // Not enough time has passed, no update needed
    }

    /**
     * Sets the delay in milliseconds between tick() executions.
     *
     * @param delayMillis the minimum delay in milliseconds (must be >= 0)
     */
    public void setDelayMillis(long delayMillis) {
        if (delayMillis < 0) {
            throw new IllegalArgumentException("Delay cannot be negative");
        }
        this.delayMillis = delayMillis;
    }

    /**
     * Sets the delay using seconds (converted to milliseconds internally).
     *
     * @param delaySeconds the minimum delay in seconds (must be >= 0)
     */
    public void setDelaySeconds(double delaySeconds) {
        if (delaySeconds < 0) {
            throw new IllegalArgumentException("Delay cannot be negative");
        }
        this.delayMillis = Math.round(delaySeconds * 1000);
    }

    /**
     * Gets the delay in seconds.
     *
     * @return the current delay in seconds
     */
    public double getDelaySeconds() {
        return delayMillis / 1000.0;
    }

    /**
     * Enables or disables the throttle. When disabled, tick() always returns false.
     *
     * @param enabled true to enable the throttle, false to disable
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) {
            // Reset timing when disabled to avoid immediate execution when re-enabled
            lastExecutionTime = System.currentTimeMillis();
        }
    }

    /**
     * Resets the internal timing, causing the next tick() call to execute immediately
     * (if enabled and delay conditions are met).
     */
    public void resetTiming() {
        lastExecutionTime = 0;
    }

    /**
     * Gets the wrapped AnimationTicker.
     *
     * @return the target AnimationTicker
     */
    public AnimationTicker getTarget() {
        return target;
    }

    /**
     * Gets the time since the last execution in milliseconds.
     *
     * @return milliseconds since last execution
     */
    public long getTimeSinceLastExecution() {
        return System.currentTimeMillis() - lastExecutionTime;
    }

    /**
     * Checks if the throttle is ready to execute (enough time has passed since last execution).
     *
     * @return true if ready to execute, false otherwise
     */
    public boolean isReadyToExecute() {
        return enabled && (System.currentTimeMillis() - lastExecutionTime >= delayMillis);
    }

    /**
     * Convenience method to create a throttle with delay specified in seconds.
     *
     * @param target the AnimationTicker to wrap
     * @param delaySeconds the delay in seconds
     * @return a new AnimationThrottle
     */
    public static AnimationThrottle withDelaySeconds(AnimationTicker target, double delaySeconds) {
        AnimationThrottle throttle = new AnimationThrottle(target);
        throttle.setDelaySeconds(delaySeconds);
        return throttle;
    }

    /**
     * Convenience method to create a throttle with delay specified in milliseconds.
     *
     * @param target the AnimationTicker to wrap
     * @param delayMillis the delay in milliseconds
     * @return a new AnimationThrottle
     */
    public static AnimationThrottle withDelayMillis(AnimationTicker target, long delayMillis) {
        return new AnimationThrottle(target, delayMillis);
    }

    @Override
    public String toString() {
        return String.format("AnimationThrottle{target=%s, delayMillis=%d, enabled=%s}",
                           target.getClass().getSimpleName(), delayMillis, enabled);
    }
}
