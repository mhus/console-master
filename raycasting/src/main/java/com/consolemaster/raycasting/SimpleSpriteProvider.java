package com.consolemaster.raycasting;

/**
 * Simple implementation of SpriteProvider that manages 8 directional sprites.
 * The sprites are arranged in the following order:
 * 0: East (0°)     - facing right
 * 1: Southeast (45°) - facing down-right
 * 2: South (90°)   - facing down
 * 3: Southwest (135°) - facing down-left
 * 4: West (180°)   - facing left
 * 5: Northwest (225°) - facing up-left
 * 6: North (270°)  - facing up
 * 7: Northeast (315°) - facing up-right
 */
public class SimpleSpriteProvider implements SpriteProvider {

    private final Sprite[] sprites;
    private final double baseScale;

    /**
     * Creates a new SimpleSpriteProvider with 8 directional sprites.
     *
     * @param sprites array of 8 sprites for each direction
     */
    public SimpleSpriteProvider(Sprite[] sprites) {
        this(sprites, 1.0);
    }

    /**
     * Creates a new SimpleSpriteProvider with 8 directional sprites and base scale.
     *
     * @param sprites array of 8 sprites for each direction
     * @param baseScale base scale factor for all sprites
     */
    public SimpleSpriteProvider(Sprite[] sprites, double baseScale) {
        if (sprites == null || sprites.length != 8) {
            throw new IllegalArgumentException("SimpleSpriteProvider requires exactly 8 sprites");
        }
        this.sprites = sprites.clone();
        this.baseScale = baseScale;
    }

    /**
     * Creates a SimpleSpriteProvider using the same sprite for all directions.
     * Useful for symmetric objects like crates or barrels.
     *
     * @param sprite the sprite to use for all directions
     */
    public SimpleSpriteProvider(Sprite sprite) {
        this(sprite, 1.0);
    }

    /**
     * Creates a SimpleSpriteProvider using the same sprite for all directions with scale.
     *
     * @param sprite the sprite to use for all directions
     * @param baseScale base scale factor
     */
    public SimpleSpriteProvider(Sprite sprite, double baseScale) {
        this.sprites = new Sprite[8];
        for (int i = 0; i < 8; i++) {
            this.sprites[i] = sprite;
        }
        this.baseScale = baseScale;
    }

    @Override
    public Sprite getSpriteForAngle(double relativeAngle) {
        // Normalize angle to [0, 2π]
        while (relativeAngle < 0) relativeAngle += 2 * Math.PI;
        while (relativeAngle >= 2 * Math.PI) relativeAngle -= 2 * Math.PI;

        // Convert angle to direction index (0-7)
        // Each direction covers 45 degrees (π/4 radians)
        double angleStep = 2 * Math.PI / 8;
        int directionIndex = (int) Math.round(relativeAngle / angleStep) % 8;

        return sprites[directionIndex];
    }

    @Override
    public int getDirectionCount() {
        return 8;
    }

    @Override
    public double getBaseScale() {
        return baseScale;
    }

    @Override
    public boolean hasSprites() {
        for (Sprite sprite : sprites) {
            if (sprite != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the sprite for a specific direction index.
     *
     * @param directionIndex direction index (0-7)
     * @return sprite for the direction, or null if invalid index
     */
    public Sprite getSpriteForDirection(int directionIndex) {
        if (directionIndex >= 0 && directionIndex < sprites.length) {
            return sprites[directionIndex];
        }
        return null;
    }

    /**
     * Sets the sprite for a specific direction index.
     *
     * @param directionIndex direction index (0-7)
     * @param sprite sprite to set
     */
    public void setSpriteForDirection(int directionIndex, Sprite sprite) {
        if (directionIndex >= 0 && directionIndex < sprites.length) {
            sprites[directionIndex] = sprite;
        }
    }

    /**
     * Creates a SimpleSpriteProvider for a crate/box object.
     * Uses the same sprite for all directions since crates are symmetric.
     *
     * @return sprite provider for a crate
     */
    public static SimpleSpriteProvider createCrate() {
        String[] crateData = {
            "┌───┐",
            "│▓▓▓│",
            "│▓▓▓│",
            "└───┘"
        };

        Sprite crateSprite = new SimpleSprite(crateData,
            com.consolemaster.AnsiColor.YELLOW,
            com.consolemaster.AnsiColor.BLACK);

        return new SimpleSpriteProvider(crateSprite);
    }

    /**
     * Creates a SimpleSpriteProvider for a barrel object.
     * Uses the same sprite for all directions since barrels are round.
     *
     * @return sprite provider for a barrel
     */
    public static SimpleSpriteProvider createBarrel() {
        String[] barrelData = {
            " ╭─╮ ",
            "╭┴─┴╮",
            "│▓▓▓│",
            "│▓▓▓│",
            "╰───╯"
        };

        Sprite barrelSprite = new SimpleSprite(barrelData,
            com.consolemaster.AnsiColor.BRIGHT_YELLOW,
            com.consolemaster.AnsiColor.BLACK);

        return new SimpleSpriteProvider(barrelSprite);
    }

    /**
     * Creates a SimpleSpriteProvider for a simple enemy with 8 directions.
     * This is a basic example showing different sprites for each direction.
     *
     * @return sprite provider for an enemy
     */
    public static SimpleSpriteProvider createEnemy() {
        Sprite[] enemySprites = new Sprite[8];

        // East (0°) - facing right
        enemySprites[0] = new SimpleSprite(new String[]{"☻", "│", "╱╲"},
            com.consolemaster.AnsiColor.RED, null);

        // Southeast (45°) - facing down-right
        enemySprites[1] = new SimpleSprite(new String[]{"☻", "╲", " ╲"},
            com.consolemaster.AnsiColor.RED, null);

        // South (90°) - facing down
        enemySprites[2] = new SimpleSprite(new String[]{"☻", "│", "╱╲"},
            com.consolemaster.AnsiColor.RED, null);

        // Southwest (135°) - facing down-left
        enemySprites[3] = new SimpleSprite(new String[]{"☻", "╱", "╱ "},
            com.consolemaster.AnsiColor.RED, null);

        // West (180°) - facing left
        enemySprites[4] = new SimpleSprite(new String[]{"☻", "│", "╱╲"},
            com.consolemaster.AnsiColor.RED, null);

        // Northwest (225°) - facing up-left
        enemySprites[5] = new SimpleSprite(new String[]{"╲ ", "╲", "☻"},
            com.consolemaster.AnsiColor.RED, null);

        // North (270°) - facing up
        enemySprites[6] = new SimpleSprite(new String[]{"╱╲", "│", "☻"},
            com.consolemaster.AnsiColor.RED, null);

        // Northeast (315°) - facing up-right
        enemySprites[7] = new SimpleSprite(new String[]{" ╱", "╱", "☻"},
            com.consolemaster.AnsiColor.RED, null);

        return new SimpleSpriteProvider(enemySprites);
    }
}
