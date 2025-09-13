package com.consolemaster;

/**
 * Layout constraints for sizing canvases within a container.
 * This constraint specifies how a canvas should be sized relative to its container.
 */
public class SizeConstraint implements LayoutConstraint {

    public enum SizeType {
        FIXED,      // Use exact pixel values
        PERCENTAGE, // Use percentage of container size
        FILL,       // Fill remaining space
        PREFERRED   // Use canvas preferred size
    }

    private final SizeType widthType;
    private final SizeType heightType;
    private final int widthValue;
    private final int heightValue;

    /**
     * Creates a size constraint with fixed dimensions.
     *
     * @param width  the fixed width
     * @param height the fixed height
     */
    public SizeConstraint(int width, int height) {
        this(SizeType.FIXED, width, SizeType.FIXED, height);
    }

    /**
     * Creates a size constraint with specified types and values.
     *
     * @param widthType  the type of width constraint
     * @param widthValue the width value (interpretation depends on type)
     * @param heightType the type of height constraint
     * @param heightValue the height value (interpretation depends on type)
     */
    public SizeConstraint(SizeType widthType, int widthValue, SizeType heightType, int heightValue) {
        this.widthType = widthType;
        this.heightType = heightType;
        this.widthValue = widthValue;
        this.heightValue = heightValue;
    }

    /**
     * Creates a constraint to fill all available space.
     */
    public static SizeConstraint fill() {
        return new SizeConstraint(SizeType.FILL, 0, SizeType.FILL, 0);
    }

    /**
     * Creates a constraint using percentage of container size.
     *
     * @param widthPercent  width as percentage (0-100)
     * @param heightPercent height as percentage (0-100)
     */
    public static SizeConstraint percentage(int widthPercent, int heightPercent) {
        return new SizeConstraint(SizeType.PERCENTAGE, widthPercent, SizeType.PERCENTAGE, heightPercent);
    }

    /**
     * Creates a constraint using preferred size.
     */
    public static SizeConstraint preferred() {
        return new SizeConstraint(SizeType.PREFERRED, 0, SizeType.PREFERRED, 0);
    }

    public SizeType getWidthType() {
        return widthType;
    }

    public SizeType getHeightType() {
        return heightType;
    }

    public int getWidthValue() {
        return widthValue;
    }

    public int getHeightValue() {
        return heightValue;
    }

    /**
     * Calculates the actual width based on container size.
     *
     * @param containerWidth the width of the container
     * @param preferredWidth the preferred width of the canvas
     * @return the calculated width
     */
    public int calculateWidth(int containerWidth, int preferredWidth) {
        return switch (widthType) {
            case FIXED -> widthValue;
            case PERCENTAGE -> (containerWidth * widthValue) / 100;
            case FILL -> containerWidth;
            case PREFERRED -> preferredWidth;
        };
    }

    /**
     * Calculates the actual height based on container size.
     *
     * @param containerHeight the height of the container
     * @param preferredHeight the preferred height of the canvas
     * @return the calculated height
     */
    public int calculateHeight(int containerHeight, int preferredHeight) {
        return switch (heightType) {
            case FIXED -> heightValue;
            case PERCENTAGE -> (containerHeight * heightValue) / 100;
            case FILL -> containerHeight;
            case PREFERRED -> preferredHeight;
        };
    }

    @Override
    public String toString() {
        return String.format("SizeConstraint[width=%s(%d), height=%s(%d)]",
                           widthType, widthValue, heightType, heightValue);
    }
}
