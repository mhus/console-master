package com.consolemaster;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * A specialized canvas that provides scrolling functionality for a single child canvas.
 * Supports both horizontal and vertical scrolling with keyboard and mouse controls.
 * The child canvas can be larger than the viewport, and scrolling allows viewing different parts.
 */
@Getter
@Setter
public class ScrollerCanvas extends Canvas implements EventHandler, Composable {

    private Canvas child;
    private boolean horizontalScrollEnabled = true;
    private boolean verticalScrollEnabled = true;

    // Scroll position (offset into the child canvas)
    private int scrollX = 0;
    private int scrollY = 0;

    // Scroll speed (pixels per scroll step)
    private int scrollStepX = 1;
    private int scrollStepY = 1;

    // Mouse scroll sensitivity
    private int mouseScrollStepX = 3;
    private int mouseScrollStepY = 3;

    // Show scrollbars
    private boolean showHorizontalScrollbar = true;
    private boolean showVerticalScrollbar = true;

    // Scrollbar appearance
    private char horizontalScrollbarChar = '═';
    private char verticalScrollbarChar = '║';
    private char scrollbarThumbChar = '█';
    private char scrollbarTrackChar = '░';
    private AnsiColor scrollbarColor = AnsiColor.BRIGHT_BLACK;
    private AnsiColor scrollbarThumbColor = AnsiColor.WHITE;

    /**
     * Creates a new ScrollerCanvas with the specified dimensions.
     *
     * @param name the name of the canvas
     * @param x the x position
     * @param y the y position
     * @param width the width of the viewport
     * @param height the height of the viewport
     */
    public ScrollerCanvas(String name, int x, int y, int width, int height) {
        super(name, width, height);
        setCanFocus(true); // Enable focus for keyboard scrolling
    }

    /**
     * Creates a new ScrollerCanvas with the specified dimensions and child.
     *
     * @param name the name of the canvas
     * @param x the x position
     * @param y the y position
     * @param width the width of the viewport
     * @param height the height of the viewport
     * @param child the child canvas to scroll
     */
    public ScrollerCanvas(String name, int x, int y, int width, int height, Canvas child) {
        this(name, x, y, width, height);
        setChild(child);
    }

    /**
     * Sets the child canvas to be scrolled.
     *
     * @param child the child canvas
     */
    public void setChild(Canvas child) {
        this.child = child;
        if (child != null) {
            // Reset scroll position when setting new child
            scrollX = 0;
            scrollY = 0;
        }
    }

    /**
     * Sets both horizontal and vertical scroll enable states.
     *
     * @param horizontal whether horizontal scrolling is enabled
     * @param vertical whether vertical scrolling is enabled
     */
    public void setScrollEnabled(boolean horizontal, boolean vertical) {
        this.horizontalScrollEnabled = horizontal;
        this.verticalScrollEnabled = vertical;

        if (!horizontal) scrollX = 0;
        if (!vertical) scrollY = 0;
    }

    /**
     * Sets the scroll step size for keyboard scrolling.
     *
     * @param stepX horizontal scroll step
     * @param stepY vertical scroll step
     */
    public void setScrollStep(int stepX, int stepY) {
        this.scrollStepX = Math.max(1, stepX);
        this.scrollStepY = Math.max(1, stepY);
    }

    /**
     * Sets the mouse scroll step size.
     *
     * @param stepX horizontal mouse scroll step
     * @param stepY vertical mouse scroll step
     */
    public void setMouseScrollStep(int stepX, int stepY) {
        this.mouseScrollStepX = Math.max(1, stepX);
        this.mouseScrollStepY = Math.max(1, stepY);
    }

    /**
     * Sets scrollbar visibility.
     *
     * @param horizontal whether to show horizontal scrollbar
     * @param vertical whether to show vertical scrollbar
     */
    public void setScrollbarsVisible(boolean horizontal, boolean vertical) {
        this.showHorizontalScrollbar = horizontal;
        this.showVerticalScrollbar = vertical;
    }

    /**
     * Scrolls to the specified position.
     *
     * @param x the horizontal scroll position
     * @param y the vertical scroll position
     */
    public void scrollTo(int x, int y) {
        if (child == null) return;

        if (horizontalScrollEnabled) {
            scrollX = clampScrollX(x);
        }
        if (verticalScrollEnabled) {
            scrollY = clampScrollY(y);
        }
    }

    /**
     * Scrolls by the specified offset.
     *
     * @param deltaX horizontal scroll offset
     * @param deltaY vertical scroll offset
     */
    public void scrollBy(int deltaX, int deltaY) {
        scrollTo(scrollX + deltaX, scrollY + deltaY);
    }

    /**
     * Scrolls to ensure the specified rectangle is visible.
     *
     * @param x the x coordinate in child canvas
     * @param y the y coordinate in child canvas
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     */
    public void scrollToVisible(int x, int y, int width, int height) {
        if (child == null) return;

        int viewportWidth = getContentWidth();
        int viewportHeight = getContentHeight();

        // Calculate required scroll position
        int newScrollX = scrollX;
        int newScrollY = scrollY;

        // Horizontal scrolling
        if (horizontalScrollEnabled) {
            if (x < scrollX) {
                newScrollX = x;
            } else if (x + width > scrollX + viewportWidth) {
                newScrollX = x + width - viewportWidth;
            }
        }

        // Vertical scrolling
        if (verticalScrollEnabled) {
            if (y < scrollY) {
                newScrollY = y;
            } else if (y + height > scrollY + viewportHeight) {
                newScrollY = y + height - viewportHeight;
            }
        }

        scrollTo(newScrollX, newScrollY);
    }

    @Override
    public void paint(Graphics graphics) {
        graphics.clear();

        if (child == null) {
            // Draw empty message if no child
            graphics.drawStyledString(getWidth() / 2 - 5, getHeight() / 2,
                "No Content", AnsiColor.BRIGHT_BLACK, null);
            return;
        }

        // Get available content area (excluding scrollbars)
        int contentWidth = getContentWidth();
        int contentHeight = getContentHeight();

        // Create a ClippingGraphics context for the content area
        ClippingGraphics clippedGraphics = new ClippingGraphics(graphics, 0, 0, contentWidth, contentHeight);

        // Translate the child canvas position based on scroll offset
        int childOriginalX = child.getX();
        int childOriginalY = child.getY();

        child.setX(-scrollX);
        child.setY(-scrollY);

        // Paint the child canvas
        child.paint(clippedGraphics);

        // Restore original child position
        child.setX(childOriginalX);
        child.setY(childOriginalY);

        // Draw scrollbars
        drawScrollbars(graphics);
    }

    /**
     * Gets the content width (excluding vertical scrollbar).
     */
    private int getContentWidth() {
        return getWidth() - (showVerticalScrollbar && isVerticalScrollNeeded() ? 1 : 0);
    }

    /**
     * Gets the content height (excluding horizontal scrollbar).
     */
    private int getContentHeight() {
        return getHeight() - (showHorizontalScrollbar && isHorizontalScrollNeeded() ? 1 : 0);
    }

    /**
     * Checks if horizontal scrolling is needed.
     */
    private boolean isHorizontalScrollNeeded() {
        return child != null && horizontalScrollEnabled && child.getWidth() > getContentWidth();
    }

    /**
     * Checks if vertical scrolling is needed.
     */
    private boolean isVerticalScrollNeeded() {
        return child != null && verticalScrollEnabled && child.getHeight() > getContentHeight();
    }

    /**
     * Draws the scrollbars.
     */
    private void drawScrollbars(Graphics graphics) {
        if (child == null) return;

        int contentWidth = getContentWidth();
        int contentHeight = getContentHeight();

        // Draw horizontal scrollbar
        if (showHorizontalScrollbar && isHorizontalScrollNeeded()) {
            drawHorizontalScrollbar(graphics, contentWidth, contentHeight);
        }

        // Draw vertical scrollbar
        if (showVerticalScrollbar && isVerticalScrollNeeded()) {
            drawVerticalScrollbar(graphics, contentWidth, contentHeight);
        }
    }

    /**
     * Draws the horizontal scrollbar.
     */
    private void drawHorizontalScrollbar(Graphics graphics, int contentWidth, int contentHeight) {
        int scrollbarY = contentHeight;

        // Draw scrollbar track
        for (int x = 0; x < contentWidth; x++) {
            graphics.drawStyledString(x, scrollbarY, String.valueOf(scrollbarTrackChar),
                scrollbarColor, null);
        }

        // Calculate thumb position and size
        double ratio = (double) contentWidth / child.getWidth();
        int thumbSize = Math.max(1, (int) (contentWidth * ratio));
        int thumbPosition = (int) (scrollX * ratio);

        // Draw scrollbar thumb
        for (int x = thumbPosition; x < thumbPosition + thumbSize && x < contentWidth; x++) {
            graphics.drawStyledString(x, scrollbarY, String.valueOf(scrollbarThumbChar),
                scrollbarThumbColor, null);
        }
    }

    /**
     * Draws the vertical scrollbar.
     */
    private void drawVerticalScrollbar(Graphics graphics, int contentWidth, int contentHeight) {
        int scrollbarX = contentWidth;

        // Draw scrollbar track
        for (int y = 0; y < contentHeight; y++) {
            graphics.drawStyledString(scrollbarX, y, String.valueOf(scrollbarTrackChar),
                scrollbarColor, null);
        }

        // Calculate thumb position and size
        double ratio = (double) contentHeight / child.getHeight();
        int thumbSize = Math.max(1, (int) (contentHeight * ratio));
        int thumbPosition = (int) (scrollY * ratio);

        // Draw scrollbar thumb
        for (int y = thumbPosition; y < thumbPosition + thumbSize && y < contentHeight; y++) {
            graphics.drawStyledString(scrollbarX, y, String.valueOf(scrollbarThumbChar),
                scrollbarThumbColor, null);
        }
    }

    /**
     * Clamps the horizontal scroll position to valid bounds.
     */
    private int clampScrollX(int x) {
        if (child == null || !horizontalScrollEnabled) return 0;
        int maxScrollX = Math.max(0, child.getWidth() - getContentWidth());
        return Math.max(0, Math.min(maxScrollX, x));
    }

    /**
     * Clamps the vertical scroll position to valid bounds.
     */
    private int clampScrollY(int y) {
        if (child == null || !verticalScrollEnabled) return 0;
        int maxScrollY = Math.max(0, child.getHeight() - getContentHeight());
        return Math.max(0, Math.min(maxScrollY, y));
    }

    @Override
    public void handleEvent(Event event) {
        if (child == null) return;

        if (event instanceof KeyEvent keyEvent) {
            handleKeyEvent(keyEvent);
        } else if (event instanceof MouseEvent mouseEvent) {
            handleMouseEvent(mouseEvent);
        }
    }

    /**
     * Handles keyboard events for scrolling.
     */
    private void handleKeyEvent(KeyEvent keyEvent) {
        if (!keyEvent.isSpecialKey()) return;

        boolean handled = false;

        switch (keyEvent.getSpecialKey()) {
            case ARROW_LEFT:
                if (horizontalScrollEnabled) {
                    scrollBy(-scrollStepX, 0);
                    handled = true;
                }
                break;
            case ARROW_RIGHT:
                if (horizontalScrollEnabled) {
                    scrollBy(scrollStepX, 0);
                    handled = true;
                }
                break;
            case ARROW_UP:
                if (verticalScrollEnabled) {
                    scrollBy(0, -scrollStepY);
                    handled = true;
                }
                break;
            case ARROW_DOWN:
                if (verticalScrollEnabled) {
                    scrollBy(0, scrollStepY);
                    handled = true;
                }
                break;
            case PAGE_UP:
                if (verticalScrollEnabled) {
                    scrollBy(0, -getContentHeight());
                    handled = true;
                }
                break;
            case PAGE_DOWN:
                if (verticalScrollEnabled) {
                    scrollBy(0, getContentHeight());
                    handled = true;
                }
                break;
            case HOME:
                if (keyEvent.hasModifier(KeyEvent.Modifier.CTRL)) {
                    scrollTo(0, 0);
                    handled = true;
                } else if (horizontalScrollEnabled) {
                    scrollTo(0, scrollY);
                    handled = true;
                }
                break;
            case END:
                if (keyEvent.hasModifier(KeyEvent.Modifier.CTRL)) {
                    scrollTo(child.getWidth(), child.getHeight());
                    handled = true;
                } else if (horizontalScrollEnabled) {
                    scrollTo(child.getWidth(), scrollY);
                    handled = true;
                }
                break;
        }

        if (handled) {
            keyEvent.consume();
        }
    }

    /**
     * Handles mouse events for scrolling.
     */
    private void handleMouseEvent(MouseEvent mouseEvent) {
        // Handle mouse wheel scrolling
        if (mouseEvent.getAction() == MouseEvent.Action.WHEEL_UP) {
            if (mouseEvent.hasModifier(KeyEvent.Modifier.SHIFT) && horizontalScrollEnabled) {
                // Shift + wheel = horizontal scroll
                scrollBy(-mouseScrollStepX, 0);
            } else if (verticalScrollEnabled) {
                // Normal wheel = vertical scroll
                scrollBy(0, -mouseScrollStepY);
            }
            mouseEvent.consume();
        } else if (mouseEvent.getAction() == MouseEvent.Action.WHEEL_DOWN) {
            if (mouseEvent.hasModifier(KeyEvent.Modifier.SHIFT) && horizontalScrollEnabled) {
                // Shift + wheel = horizontal scroll
                scrollBy(mouseScrollStepX, 0);
            } else if (verticalScrollEnabled) {
                // Normal wheel = vertical scroll
                scrollBy(0, mouseScrollStepY);
            }
            mouseEvent.consume();
        }

        // Handle scrollbar clicks
        if (mouseEvent.getAction() == MouseEvent.Action.CLICK) {
            handleScrollbarClick(mouseEvent);
        }
    }

    /**
     * Handles clicks on scrollbars.
     */
    private void handleScrollbarClick(MouseEvent mouseEvent) {
        int mouseX = mouseEvent.getX() - getX();
        int mouseY = mouseEvent.getY() - getY();
        int contentWidth = getContentWidth();
        int contentHeight = getContentHeight();

        // Check horizontal scrollbar click
        if (showHorizontalScrollbar && isHorizontalScrollNeeded() &&
            mouseY == contentHeight && mouseX < contentWidth) {

            // Calculate scroll position based on click position
            double ratio = (double) mouseX / contentWidth;
            int newScrollX = (int) (ratio * child.getWidth());
            scrollTo(newScrollX, scrollY);
            mouseEvent.consume();
        }

        // Check vertical scrollbar click
        else if (showVerticalScrollbar && isVerticalScrollNeeded() &&
                 mouseX == contentWidth && mouseY < contentHeight) {

            // Calculate scroll position based on click position
            double ratio = (double) mouseY / contentHeight;
            int newScrollY = (int) (ratio * child.getHeight());
            scrollTo(scrollX, newScrollY);
            mouseEvent.consume();
        }
    }

    @Override
    protected void onFocusChanged(boolean focused) {
        super.onFocusChanged(focused);
        // Visual feedback for focus state could be added here
        // For example, change scrollbar colors when focused
    }

    @Override
    public List<Canvas> getChildren() {
        return child != null ? List.of(child) : List.of();
    }

    @Override
    public int getChildCount() {
        return child != null ? 1 : 0;
    }
}
