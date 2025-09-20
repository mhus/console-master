package com.consolemaster.raycasting;

import com.consolemaster.AnsiColor;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * Information about a map entry including visual representation,
 * collision properties, and rendering characteristics.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EntryInfo {

    /**
     * True if this entry represents a wall that blocks vision and movement.
     */
    @Builder.Default
    private boolean isWall = false;

    /**
     * True if the player can walk through this entry (opposite of collision).
     */
    @Builder.Default
    private boolean isFallthrough = true;

    /**
     * True if this entry is transparent and allows vision through it.
     */
    @Builder.Default
    private boolean isTransparent = true;

    /**
     * The character used to render this entry.
     * Default: '█' for walls, '.' for floors.
     */
    @Builder.Default
    private char character = ' ';

    /**
     * Name of this entry type for debugging and display purposes.
     */
    @Builder.Default
    private String name = "Empty";

    /**
     * Light color for rendering this entry (used for vertical walls or well-lit surfaces).
     * Null means use default color.
     */
    private AnsiColor colorLight;

    /**
     * Dark color for rendering this entry (used for horizontal walls or shadowed surfaces).
     * Null means use default color or derive from colorLight.
     */
    private AnsiColor colorDark;

    /**
     * Height of the wall (used for rendering). Default is 1.0 (full height).
     */
    @Builder.Default
    private double height = 1.0;

    /**
     * Path to texture file. Null means no texture.
     */
    private String texture;

    /**
     * Get the appropriate color based on lighting conditions.
     *
     * @param isDark true if dark color should be used (e.g., horizontal walls)
     * @return the appropriate color or null if no color is set
     */
    public AnsiColor getColor(boolean isDark) {
        if (isDark && colorDark != null) {
            return colorDark;
        } else if (!isDark && colorLight != null) {
            return colorLight;
        } else if (colorLight != null) {
            // Fall back to light color if dark is not available
            return colorLight;
        } else if (colorDark != null) {
            // Fall back to dark color if light is not available
            return colorDark;
        }
        return null;
    }

    /**
     * Get the light color (for backward compatibility).
     *
     * @return the light color
     */
    public AnsiColor getColor() {
        return getColor(false);
    }

    /**
     * Set both light and dark colors to the same value (for backward compatibility).
     *
     * @param color the color to set for both light and dark
     */
    public void setColor(AnsiColor color) {
        this.colorLight = color;
        this.colorDark = color;
    }

    // Predefined common entry types

    /**
     * Creates a standard wall entry with light and dark colors.
     */
    public static EntryInfo createWall() {
        return EntryInfo.builder()
                .isWall(true)
                .isFallthrough(false)
                .isTransparent(false)
                .character('█')
                .name("Wall")
                .colorLight(AnsiColor.WHITE)
                .colorDark(AnsiColor.BRIGHT_BLACK)
                .height(1.0)
                .build();
    }

    /**
     * Creates a standard floor entry.
     */
    public static EntryInfo createFloor() {
        return EntryInfo.builder()
                .isWall(false)
                .isFallthrough(true)
                .isTransparent(true)
                .character('.')
                .name("Floor")
                .colorLight(AnsiColor.YELLOW)
                .colorDark(AnsiColor.YELLOW)
                .height(0.0)
                .build();
    }

    /**
     * Creates an empty space entry.
     */
    public static EntryInfo createEmpty() {
        return EntryInfo.builder()
                .isWall(false)
                .isFallthrough(true)
                .isTransparent(true)
                .character(' ')
                .name("Empty")
                .colorLight(null)
                .colorDark(null)
                .height(0.0)
                .build();
    }

    /**
     * Creates a glass wall (transparent but blocking movement).
     */
    public static EntryInfo createGlass() {
        return EntryInfo.builder()
                .isWall(true)
                .isFallthrough(false)
                .isTransparent(true)
                .character('|')
                .name("Glass")
                .colorLight(AnsiColor.CYAN)
                .colorDark(AnsiColor.BLUE)
                .height(1.0)
                .build();
    }

    /**
     * Creates a half-height wall with appropriate colors.
     */
    public static EntryInfo createLowWall() {
        return EntryInfo.builder()
                .isWall(true)
                .isFallthrough(false)
                .isTransparent(false)
                .character('▄')
                .name("Low Wall")
                .colorLight(AnsiColor.BRIGHT_BLACK)
                .colorDark(AnsiColor.BLACK)
                .height(0.5)
                .build();
    }

    /**
     * Creates a stone wall with realistic stone colors.
     */
    public static EntryInfo createStoneWall() {
        return EntryInfo.builder()
                .isWall(true)
                .isFallthrough(false)
                .isTransparent(false)
                .character('█')
                .name("Stone Wall")
                .colorLight(AnsiColor.WHITE)
                .colorDark(AnsiColor.BRIGHT_BLACK)
                .height(1.0)
                .build();
    }

    /**
     * Creates a brick wall with warm brick colors.
     */
    public static EntryInfo createBrickWall() {
        return EntryInfo.builder()
                .isWall(true)
                .isFallthrough(false)
                .isTransparent(false)
                .character('▓')
                .name("Brick Wall")
                .colorLight(AnsiColor.RED)
                .colorDark(AnsiColor.BRIGHT_RED)
                .height(1.0)
                .build();
    }

    /**
     * Creates a metal wall with metallic colors.
     */
    public static EntryInfo createMetalWall() {
        return EntryInfo.builder()
                .isWall(true)
                .isFallthrough(false)
                .isTransparent(false)
                .character('▒')
                .name("Metal Wall")
                .colorLight(AnsiColor.BRIGHT_WHITE)
                .colorDark(AnsiColor.WHITE)
                .height(1.0)
                .build();
    }

    /**
     * Creates a tree wall with natural colors.
     */
    public static EntryInfo createTree() {
        return EntryInfo.builder()
                .isWall(true)
                .isFallthrough(false)
                .isTransparent(false)
                .character('♠')
                .name("Tree")
                .colorLight(AnsiColor.BRIGHT_GREEN)
                .colorDark(AnsiColor.GREEN)
                .height(1.3)
                .build();
    }

    /**
     * Creates an entry from a legacy character representation.
     *
     * @param character the legacy character
     * @return appropriate EntryInfo
     */
    public static EntryInfo fromCharacter(char character) {
        return switch (character) {
            case '#' -> createWall();
            case '.' -> createFloor();
            case ' ' -> createEmpty();
            default -> EntryInfo.builder()
                    .isWall(false)
                    .isFallthrough(true)
                    .isTransparent(true)
                    .character(character)
                    .name("Custom")
                    .build();
        };
    }

    /**
     * Converts this EntryInfo to a legacy character representation.
     *
     * @return character representation
     */
    public char toCharacter() {
        if (isWall && !isTransparent) {
            return '#';
        } else if (!isWall && character == '.') {
            return '.';
        } else if (!isWall && character == ' ') {
            return ' ';
        } else {
            return character;
        }
    }

    @Override
    public String toString() {
        return String.format("EntryInfo{name='%s', char='%c', wall=%s, fallthrough=%s, transparent=%s, height=%.1f, colors=[%s,%s]}",
                name, character, isWall, isFallthrough, isTransparent, height, colorLight, colorDark);
    }
}
