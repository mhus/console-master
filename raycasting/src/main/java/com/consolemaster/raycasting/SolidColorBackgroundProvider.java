package com.consolemaster.raycasting;

import com.consolemaster.StyledChar;
import com.consolemaster.AnsiColor;
import lombok.Getter;
import lombok.Setter;

/**
 * A simple background provider that renders a solid color background.
 */
@Getter
@Setter
public class SolidColorBackgroundProvider implements BackgroundProvider {

    private StyledChar character;

    public SolidColorBackgroundProvider(char character, AnsiColor foregroundColor, AnsiColor backgroundColor) {
        this.character = new StyledChar(character, foregroundColor, backgroundColor, null);
    }

    public SolidColorBackgroundProvider(char character, AnsiColor color) {
        this(character, color, null);
    }

    public SolidColorBackgroundProvider(AnsiColor backgroundColor) {
        this(' ', null, backgroundColor);
    }

    @Override
    public StyledChar getBackground(int x, int y) {
        return character;
    }

    @Override
    public void setDimensionAndAngle(int width, int height, double playerAngle) {

    }
}
