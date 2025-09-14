package com.consolemaster;

/**
 * Default mouse manager implementation that forwards mouse events to the canvas hierarchy.
 * Events are processed by finding the topmost canvas at the mouse position and forwarding
 * the event through the component tree.
 */
public class DefaultMouseManager implements MouseManager {

    private Canvas hoveredCanvas = null;
    private Canvas pressedCanvas = null;
    private long lastClickTime = 0;
    private int lastClickX = -1;
    private int lastClickY = -1;
    private static final long DOUBLE_CLICK_THRESHOLD = 300; // milliseconds
    private static final int DOUBLE_CLICK_DISTANCE = 5; // pixels

    @Override
    public void processMouseEvent(MouseEvent mouseEvent, ScreenCanvas screenCanvas) {
        if (mouseEvent.isConsumed()) {
            return;
        }

        // Find the canvas at the mouse position
        Canvas targetCanvas = findCanvasAt(mouseEvent.getX(), mouseEvent.getY(), screenCanvas);

        // Handle different mouse actions
        switch (mouseEvent.getAction()) {
            case MOVE -> handleMouseMove(mouseEvent, targetCanvas, screenCanvas);
            case PRESS -> handleMousePress(mouseEvent, targetCanvas, screenCanvas);
            case RELEASE -> handleMouseRelease(mouseEvent, targetCanvas, screenCanvas);
            case WHEEL_UP, WHEEL_DOWN -> handleMouseWheel(mouseEvent, targetCanvas, screenCanvas);
            case DRAG -> handleMouseDrag(mouseEvent, targetCanvas, screenCanvas);
        }
    }

    /**
     * Handles mouse move events.
     */
    private void handleMouseMove(MouseEvent mouseEvent, Canvas targetCanvas, ScreenCanvas screenCanvas) {
        // Handle mouse enter/leave events
        if (hoveredCanvas != targetCanvas) {
            // Mouse leave previous canvas
            if (hoveredCanvas != null) {
                MouseEvent leaveEvent = new MouseEvent(
                    mouseEvent.getX(), mouseEvent.getY(),
                    MouseEvent.Button.NONE, MouseEvent.Action.MOVE,
                    mouseEvent.isHasShift(), mouseEvent.isHasCtrl(), mouseEvent.isHasAlt()
                );
                forwardEventToCanvas(hoveredCanvas, leaveEvent);
            }

            // Mouse enter new canvas
            if (targetCanvas != null) {
                MouseEvent enterEvent = new MouseEvent(
                    mouseEvent.getX(), mouseEvent.getY(),
                    MouseEvent.Button.NONE, MouseEvent.Action.MOVE,
                    mouseEvent.isHasShift(), mouseEvent.isHasCtrl(), mouseEvent.isHasAlt()
                );
                forwardEventToCanvas(targetCanvas, enterEvent);
            }

            hoveredCanvas = targetCanvas;
        }

        // Forward move event to current canvas
        if (targetCanvas != null) {
            forwardEventToCanvas(targetCanvas, mouseEvent);
        }
    }

    /**
     * Handles mouse press events.
     */
    private void handleMousePress(MouseEvent mouseEvent, Canvas targetCanvas, ScreenCanvas screenCanvas) {
        pressedCanvas = targetCanvas;

        // Check for double click
        long currentTime = System.currentTimeMillis();
        boolean isDoubleClick = false;

        if (currentTime - lastClickTime <= DOUBLE_CLICK_THRESHOLD &&
            Math.abs(mouseEvent.getX() - lastClickX) <= DOUBLE_CLICK_DISTANCE &&
            Math.abs(mouseEvent.getY() - lastClickY) <= DOUBLE_CLICK_DISTANCE) {
            isDoubleClick = true;
        }

        lastClickTime = currentTime;
        lastClickX = mouseEvent.getX();
        lastClickY = mouseEvent.getY();

        if (targetCanvas != null) {
            // Request focus for the clicked canvas if it can receive focus
            if (targetCanvas.isCanFocus()) {
                screenCanvas.requestFocus(targetCanvas);
            }

            // Forward press event
            forwardEventToCanvas(targetCanvas, mouseEvent);

            // Generate double click event if applicable
            if (isDoubleClick) {
                MouseEvent doubleClickEvent = new MouseEvent(
                    mouseEvent.getX(), mouseEvent.getY(),
                    mouseEvent.getButton(), MouseEvent.Action.DOUBLE_CLICK,
                    mouseEvent.isHasShift(), mouseEvent.isHasCtrl(), mouseEvent.isHasAlt()
                );
                forwardEventToCanvas(targetCanvas, doubleClickEvent);
            }
        }
    }

    /**
     * Handles mouse release events.
     */
    private void handleMouseRelease(MouseEvent mouseEvent, Canvas targetCanvas, ScreenCanvas screenCanvas) {
        // Forward release event to the canvas that was pressed
        if (pressedCanvas != null) {
            forwardEventToCanvas(pressedCanvas, mouseEvent);

            // Generate click event if release is on the same canvas as press
            if (pressedCanvas == targetCanvas) {
                MouseEvent clickEvent = new MouseEvent(
                    mouseEvent.getX(), mouseEvent.getY(),
                    mouseEvent.getButton(), MouseEvent.Action.CLICK,
                    mouseEvent.isHasShift(), mouseEvent.isHasCtrl(), mouseEvent.isHasAlt()
                );
                forwardEventToCanvas(targetCanvas, clickEvent);
            }
        }

        pressedCanvas = null;
    }

    /**
     * Handles mouse wheel events.
     */
    private void handleMouseWheel(MouseEvent mouseEvent, Canvas targetCanvas, ScreenCanvas screenCanvas) {
        if (targetCanvas != null) {
            forwardEventToCanvas(targetCanvas, mouseEvent);
        }
    }

    /**
     * Handles mouse drag events.
     */
    private void handleMouseDrag(MouseEvent mouseEvent, Canvas targetCanvas, ScreenCanvas screenCanvas) {
        // Forward drag event to the canvas that was pressed, not necessarily the current one
        if (pressedCanvas != null) {
            forwardEventToCanvas(pressedCanvas, mouseEvent);
        }
    }

    /**
     * Finds the topmost canvas at the specified coordinates.
     */
    private Canvas findCanvasAt(int x, int y, ScreenCanvas screenCanvas) {
        Canvas contentCanvas = screenCanvas.getContentCanvas();
        if (contentCanvas != null) {
            return findCanvasAtRecursive(x, y, contentCanvas);
        }
        return null;
    }

    /**
     * Recursively searches for the canvas at the specified coordinates.
     */
    private Canvas findCanvasAtRecursive(int x, int y, Canvas canvas) {
        if (!canvas.isVisible() || !canvas.contains(x, y)) {
            return null;
        }

        // Check children first (they are on top)
        if (canvas instanceof Composite composite) {
            // Iterate in reverse order to check topmost children first
            var children = composite.getChildren();
            for (int i = children.size() - 1; i >= 0; i--) {
                Canvas child = children.get(i);
                Canvas found = findCanvasAtRecursive(x, y, child);
                if (found != null) {
                    return found;
                }
            }
        }

        // If no child contains the point, return this canvas
        return canvas;
    }

    /**
     * Forwards a mouse event to a canvas if it implements EventHandler.
     */
    private void forwardEventToCanvas(Canvas canvas, MouseEvent mouseEvent) {
        if (canvas instanceof EventHandler eventHandler) {
            eventHandler.handleEvent(mouseEvent);
        }
    }

    @Override
    public void cleanup(ScreenCanvas screenCanvas) {
        hoveredCanvas = null;
        pressedCanvas = null;
        lastClickTime = 0;
        lastClickX = -1;
        lastClickY = -1;
    }

    @Override
    public int getPriority() {
        return 100; // Default priority for canvas hierarchy handling
    }
}
