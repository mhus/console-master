package com.consolemaster.raycasting;

import com.consolemaster.StyledChar;
import com.consolemaster.AnsiColor;
import com.consolemaster.AnimationTicker;
import lombok.Getter;
import lombok.Setter;

/**
 * A background provider that simulates a sky with moving clouds using Perlin noise.
 * The clouds move slowly across the screen and provide a realistic sky effect.
 * Note: This provider must be manually registered with the AnimationManager for animation.
 */
@Getter
@Setter
public class CloudsBackgroundProvider implements BackgroundProvider, AnimationTicker {

    private double cloudSpeed = 0.02; // Speed of cloud movement
    private double cloudScale = 0.05; // Scale of the cloud pattern
    private double cloudDensity = 0.3; // Density threshold for cloud visibility
    private double time = 0.0; // Current time for animation

    private AnsiColor skyColor = AnsiColor.BLUE;
    private AnsiColor cloudColorLight = AnsiColor.WHITE;
    private AnsiColor cloudColorDark = AnsiColor.BRIGHT_BLACK;

    private char[] cloudChars = {' ', '░', '▒', '▓', '█'};
    private int width;
    private int height;
    private StyledChar[] buffer;
    private double playerAngle;

    public CloudsBackgroundProvider() {
        // No automatic registration - let application handle it
    }

    public CloudsBackgroundProvider(double cloudSpeed, double cloudScale, double cloudDensity) {
        this.cloudSpeed = cloudSpeed;
        this.cloudScale = cloudScale;
        this.cloudDensity = cloudDensity;
        // No automatic registration - let application handle it
    }

    @Override
    public StyledChar getBackground(int x, int y) {
        if (buffer == null) {
            return new StyledChar(' ', null, skyColor, null);
        }
        return buffer[y * width + x];
    }

    @Override
    public void setDimensionAndAngle(int width, int height, double playerAngle) {
        this.width = width;
        this.height = height;
        this.playerAngle = playerAngle;
        if (buffer == null || buffer.length != width * height) {
            buffer = new StyledChar[width * height];
            createScene();
        }
    }

    private void createScene() {

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                buffer[y * width + x] = generateCloudChar(x, y);
            }
        }
    }

    private StyledChar generateCloudChar(int x, int y) {
        // Calculate noise value with time offset for movement
        double noiseValue = perlinNoise((x + time) * cloudScale, y * cloudScale);

        // Normalize noise value to 0-1 range
        noiseValue = (noiseValue + 1.0) / 2.0;

        // Determine cloud intensity
        if (noiseValue < cloudDensity) {
            // Sky - no clouds
            return new StyledChar(' ', null, skyColor, null);
        } else {
            // Clouds with varying intensity
            double cloudIntensity = (noiseValue - cloudDensity) / (1.0 - cloudDensity);

            // Select character based on cloud intensity
            int charIndex = (int) (cloudIntensity * (cloudChars.length - 1));
            charIndex = Math.max(0, Math.min(charIndex, cloudChars.length - 1));

            // Select color based on cloud intensity
            AnsiColor cloudColor = cloudIntensity > 0.7 ? cloudColorLight : cloudColorDark;

            return new StyledChar(cloudChars[charIndex], cloudColor, skyColor, null);
        }

    }

    @Override
    public boolean tick() {
        time += cloudSpeed;
        createScene();
        return true; // Continue animation
    }

    /**
     * Simple Perlin noise implementation for cloud generation.
     * Based on Ken Perlin's improved noise function.
     */
    private double perlinNoise(double x, double y) {
        // Integer coordinates
        int xi = (int) Math.floor(x) & 255;
        int yi = (int) Math.floor(y) & 255;

        // Fractional coordinates
        double xf = x - Math.floor(x);
        double yf = y - Math.floor(y);

        // Smooth the fractional coordinates
        double u = fade(xf);
        double v = fade(yf);

        // Hash coordinates of the 4 corners
        int aa = permutation[xi] + yi;
        int ab = permutation[xi] + yi + 1;
        int ba = permutation[xi + 1] + yi;
        int bb = permutation[xi + 1] + yi + 1;

        // Interpolate between the 4 corners
        double x1 = lerp(grad(permutation[aa], xf, yf),
                         grad(permutation[ba], xf - 1, yf), u);
        double x2 = lerp(grad(permutation[ab], xf, yf - 1),
                         grad(permutation[bb], xf - 1, yf - 1), u);

        return lerp(x1, x2, v);
    }

    private double fade(double t) {
        return t * t * t * (t * (t * 6 - 15) + 10);
    }

    private double lerp(double a, double b, double t) {
        return a + t * (b - a);
    }

    private double grad(int hash, double x, double y) {
        int h = hash & 15;
        double u = h < 8 ? x : y;
        double v = h < 4 ? y : h == 12 || h == 14 ? x : 0;
        return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
    }

    // Permutation table for Perlin noise
    private static final int[] permutation = {
        151, 160, 137, 91, 90, 15, 131, 13, 201, 95, 96, 53, 194, 233, 7, 225,
        140, 36, 103, 30, 69, 142, 8, 99, 37, 240, 21, 10, 23, 190, 6, 148,
        247, 120, 234, 75, 0, 26, 197, 62, 94, 252, 219, 203, 117, 35, 11, 32,
        57, 177, 33, 88, 237, 149, 56, 87, 174, 20, 125, 136, 171, 168, 68, 175,
        74, 165, 71, 134, 139, 48, 27, 166, 77, 146, 158, 231, 83, 111, 229, 122,
        60, 211, 133, 230, 220, 105, 92, 41, 55, 46, 245, 40, 244, 102, 143, 54,
        65, 25, 63, 161, 1, 216, 80, 73, 209, 76, 132, 187, 208, 89, 18, 169,
        200, 196, 135, 130, 116, 188, 159, 86, 164, 100, 109, 198, 173, 186, 3, 64,
        52, 217, 226, 250, 124, 123, 5, 202, 38, 147, 118, 126, 255, 82, 85, 212,
        207, 206, 59, 227, 47, 16, 58, 17, 182, 189, 28, 42, 223, 183, 170, 213,
        119, 248, 152, 2, 44, 154, 163, 70, 221, 153, 101, 155, 167, 43, 172, 9,
        129, 22, 39, 253, 19, 98, 108, 110, 79, 113, 224, 232, 178, 185, 112, 104,
        218, 246, 97, 228, 251, 34, 242, 193, 238, 210, 144, 12, 191, 179, 162, 241,
        81, 51, 145, 235, 249, 14, 239, 107, 49, 192, 214, 31, 181, 199, 106, 157,
        184, 84, 204, 176, 115, 121, 50, 45, 127, 4, 150, 254, 138, 236, 205, 93,
        222, 114, 67, 29, 24, 72, 243, 141, 128, 195, 78, 66, 215, 61, 156, 180,
        // Duplicate the permutation table to avoid overflow
        151, 160, 137, 91, 90, 15, 131, 13, 201, 95, 96, 53, 194, 233, 7, 225,
        140, 36, 103, 30, 69, 142, 8, 99, 37, 240, 21, 10, 23, 190, 6, 148,
        247, 120, 234, 75, 0, 26, 197, 62, 94, 252, 219, 203, 117, 35, 11, 32,
        57, 177, 33, 88, 237, 149, 56, 87, 174, 20, 125, 136, 171, 168, 68, 175,
        74, 165, 71, 134, 139, 48, 27, 166, 77, 146, 158, 231, 83, 111, 229, 122,
        60, 211, 133, 230, 220, 105, 92, 41, 55, 46, 245, 40, 244, 102, 143, 54,
        65, 25, 63, 161, 1, 216, 80, 73, 209, 76, 132, 187, 208, 89, 18, 169,
        200, 196, 135, 130, 116, 188, 159, 86, 164, 100, 109, 198, 173, 186, 3, 64,
        52, 217, 226, 250, 124, 123, 5, 202, 38, 147, 118, 126, 255, 82, 85, 212,
        207, 206, 59, 227, 47, 16, 58, 17, 182, 189, 28, 42, 223, 183, 170, 213,
        119, 248, 152, 2, 44, 154, 163, 70, 221, 153, 101, 155, 167, 43, 172, 9,
        129, 22, 39, 253, 19, 98, 108, 110, 79, 113, 224, 232, 178, 185, 112, 104,
        218, 246, 97, 228, 251, 34, 242, 193, 238, 210, 144, 12, 191, 179, 162, 241,
        81, 51, 145, 235, 249, 14, 239, 107, 49, 192, 214, 31, 181, 199, 106, 157,
        184, 84, 204, 176, 115, 121, 50, 45, 127, 4, 150, 254, 138, 236, 205, 93,
        222, 114, 67, 29, 24, 72, 243, 141, 128, 195, 78, 66, 215, 61, 156, 180
    };
}
