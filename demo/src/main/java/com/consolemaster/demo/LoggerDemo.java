package com.consolemaster.demo;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for logging in demo applications.
 * Provides convenience methods for different types of logging.
 */
@Slf4j
public class LoggerDemo {

    private static final Logger PERFORMANCE_LOGGER = LoggerFactory.getLogger("com.consolemaster.performance");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    /**
     * Logs the start of a demo application.
     */
    public static void logDemoStart(String demoName) {
        log.info("=== Starting Demo: {} ===", demoName);
        log.info("Log files location: {}", getLogDirectory());
        log.debug("Demo startup at: {}", LocalDateTime.now().format(TIME_FORMAT));
    }

    /**
     * Logs the end of a demo application.
     */
    public static void logDemoEnd(String demoName) {
        log.info("=== Demo Completed: {} ===", demoName);
        log.debug("Demo ended at: {}", LocalDateTime.now().format(TIME_FORMAT));
    }

    /**
     * Logs performance metrics.
     */
    public static void logPerformance(String operation, long durationMillis) {
        PERFORMANCE_LOGGER.info("Performance - {}: {} ms", operation, durationMillis);
    }

    /**
     * Logs an error with exception details.
     */
    public static void logError(String message, Throwable throwable) {
        log.error("Error: {} - Exception: {}", message, throwable.getMessage(), throwable);
    }

    /**
     * Logs a warning message.
     */
    public static void logWarning(String message) {
        log.warn("Warning: {}", message);
    }

    /**
     * Logs debug information.
     */
    public static void logDebug(String message, Object... args) {
        log.debug(message, args);
    }

    /**
     * Gets the log directory path.
     */
    public static String getLogDirectory() {
        File logDir = new File("logs");
        return logDir.getAbsolutePath();
    }

    /**
     * Ensures log directory exists.
     */
    public static void ensureLogDirectoryExists() {
        File logDir = new File("logs");
        if (!logDir.exists()) {
            if (logDir.mkdirs()) {
                log.info("Created log directory: {}", logDir.getAbsolutePath());
            } else {
                log.warn("Failed to create log directory: {}", logDir.getAbsolutePath());
            }
        }
    }

    /**
     * Main method to demonstrate logging functionality.
     */
    public static void main(String[] args) {
        // Ensure log directory exists
        ensureLogDirectoryExists();

        // Log demo start
        logDemoStart("LoggerDemo");

        // Demonstrate different log levels
        log.trace("This is a TRACE message");
        log.debug("This is a DEBUG message");
        log.info("This is an INFO message");
        log.warn("This is a WARN message");
        log.error("This is an ERROR message");

        // Demonstrate performance logging
        long startTime = System.currentTimeMillis();
        try {
            Thread.sleep(100); // Simulate some work
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        long endTime = System.currentTimeMillis();
        logPerformance("Demo Operation", endTime - startTime);

        // Demonstrate error logging
        try {
            throw new RuntimeException("This is a test exception");
        } catch (Exception e) {
            logError("Demonstrating error logging", e);
        }

        // Demonstrate warning and debug logging
        logWarning("This is a warning message");
        logDebug("Debug message with parameters: {} and {}", "param1", 42);

        // Log information about log configuration
        log.info("Logback configuration loaded from classpath");
        log.info("Check the following files for logged output:");
        log.info("  - Console output (visible above)");
        log.info("  - logs/console-master.log (general application logs)");
        log.info("  - logs/performance.log (performance metrics)");

        // Log demo end
        logDemoEnd("LoggerDemo");
    }
}
