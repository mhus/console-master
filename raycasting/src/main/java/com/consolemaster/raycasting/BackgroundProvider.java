package com.consolemaster.raycasting;

import com.consolemaster.StyledChar;

/**
 * Interface for providing background rendering for areas where no floor or ceiling is rendered.
 * Background providers can create various effects like solid colors, clouds, or starfields.
 */
public interface BackgroundProvider {

    /**
     * Gets the background character and styling at the specified screen coordinates.
     *
     * @param x Screen x coordinate
     * @param y Screen y coordinate
     * @return StyledChar representing the background at this position
     */
    StyledChar getBackground(int x, int y);

    void setDimensionAndAngle(int width, int height, double playerAngle);
}
