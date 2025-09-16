package com.consolemaster.graphics25d;

import com.consolemaster.AnsiColor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a 2.5D object with position, texture, and color.
 * These objects are rendered by the Graphics25DCanvas.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Object25D {
    private Point25D position;
    private String texture;
    private AnsiColor color;

    /**
     * Creates a new Object25D at the specified position.
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @param z the z-coordinate
     * @param texture the texture string
     * @param color the ANSI color
     */
    public Object25D(double x, double y, double z, String texture, AnsiColor color) {
        this.position = new Point25D(x, y, z);
        this.texture = texture;
        this.color = color;
    }

    /**
     * Gets the distance from this object to a point.
     *
     * @param point the point to measure distance to
     * @return the distance
     */
    public double distanceTo(Point25D point) {
        return position.distanceTo(point);
    }

    /**
     * Creates a copy of this object.
     *
     * @return a new Object25D with the same properties
     */
    public Object25D copy() {
        return new Object25D(position.copy(), texture, color);
    }
}
