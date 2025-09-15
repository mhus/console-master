package com.consolemaster.graphic3d;

import com.consolemaster.AnsiColor;
import lombok.Data;
import lombok.AllArgsConstructor;

/**
 * Represents a 3D texture with pattern and color information.
 * Can be applied to Face3D objects for enhanced visual appearance.
 */
@Data
@AllArgsConstructor
public class Texture3D {

    /**
     * Texture patterns for 3D faces.
     */
    public enum TexturePattern {
        SOLID('█'),           // Solid block
        DENSE('▓'),           // Dense pattern
        MEDIUM('▒'),          // Medium pattern
        LIGHT('░'),           // Light pattern
        DOTS('·'),            // Dots pattern
        CROSS('+'),           // Cross pattern
        DIAGONAL('/'),        // Diagonal lines
        VERTICAL('|'),        // Vertical lines
        HORIZONTAL('-'),      // Horizontal lines
        GRID('#'),            // Grid pattern
        STARS('*'),           // Stars pattern
        CIRCLES('○'),         // Circles pattern
        SQUARES('□'),         // Squares pattern
        TRIANGLES('△');       // Triangles pattern

        private final char character;

        TexturePattern(char character) {
            this.character = character;
        }

        public char getCharacter() {
            return character;
        }
    }

    private TexturePattern pattern;
    private AnsiColor primaryColor;
    private AnsiColor secondaryColor; // For patterns that need two colors
    private double intensity; // 0.0 to 1.0 for texture blending

    /**
     * Creates a simple solid texture with one color.
     */
    public Texture3D(TexturePattern pattern, AnsiColor color) {
        this(pattern, color, null, 1.0);
    }

    /**
     * Creates a solid color texture.
     */
    public static Texture3D solid(AnsiColor color) {
        return new Texture3D(TexturePattern.SOLID, color);
    }

    /**
     * Creates a metallic texture.
     */
    public static Texture3D metallic(AnsiColor baseColor) {
        return new Texture3D(TexturePattern.DENSE, baseColor, AnsiColor.BRIGHT_WHITE, 0.8);
    }

    /**
     * Creates a wood texture.
     */
    public static Texture3D wood() {
        return new Texture3D(TexturePattern.HORIZONTAL, AnsiColor.YELLOW, AnsiColor.BLUE, 0.6);
    }

    /**
     * Creates a stone texture.
     */
    public static Texture3D stone() {
        return new Texture3D(TexturePattern.DOTS, AnsiColor.WHITE, AnsiColor.BLACK, 0.7);
    }

    /**
     * Creates a fabric texture.
     */
    public static Texture3D fabric(AnsiColor color) {
        return new Texture3D(TexturePattern.CROSS, color, null, 0.9);
    }

    /**
     * Creates a glass texture.
     */
    public static Texture3D glass(AnsiColor tint) {
        return new Texture3D(TexturePattern.LIGHT, tint, AnsiColor.BRIGHT_WHITE, 0.3);
    }

    /**
     * Gets the character to render based on texture coordinates and lighting.
     *
     * @param u texture U coordinate (0.0 to 1.0)
     * @param v texture V coordinate (0.0 to 1.0)
     * @param lightIntensity lighting intensity (0.0 to 1.0)
     * @return character to render
     */
    public char getCharacterAt(double u, double v, double lightIntensity) {
        // Apply lighting to intensity
        double finalIntensity = intensity * lightIntensity;

        // For simple implementation, return pattern character
        // More complex textures could use u,v coordinates for procedural patterns
        if (finalIntensity < 0.3) {
            return '·'; // Very dark
        } else if (finalIntensity < 0.6) {
            return '░'; // Medium
        } else {
            return pattern.getCharacter();
        }
    }

    /**
     * Gets the color to render based on texture coordinates and lighting.
     *
     * @param u texture U coordinate (0.0 to 1.0)
     * @param v texture V coordinate (0.0 to 1.0)
     * @param lightIntensity lighting intensity (0.0 to 1.0)
     * @return color to render
     */
    public AnsiColor getColorAt(double u, double v, double lightIntensity) {
        // Simple implementation - could be enhanced with complex color blending
        if (secondaryColor != null && (u + v) % 0.5 < 0.25) {
            return secondaryColor;
        }
        return primaryColor;
    }
}
