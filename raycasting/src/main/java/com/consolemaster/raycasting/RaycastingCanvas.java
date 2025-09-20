package com.consolemaster.raycasting;

import com.consolemaster.Canvas;
import com.consolemaster.Graphics;
import com.consolemaster.AnsiColor;
import com.consolemaster.StyledChar;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * A canvas that renders a 3D world using raycasting technique.
 * The world is represented by a MapProvider where '#' represents walls and ' ' represents empty space.
 */
@Getter
@Setter
public class RaycastingCanvas extends Canvas {

    private MapProvider mapProvider;
    private double playerX = 2.0;
    private double playerY = 2.0;
    private double playerAngle = 0.0;
    private double fov = Math.PI / 3; // 60 degrees field of view
    private char wallChar = '█';
    private char floorChar = '.';
    private char ceilingChar = ' ';
    private char wallEdgeChar = '│'; // Character for vertical wall edges
    private AnsiColor wallColor = AnsiColor.WHITE;
    private AnsiColor floorColor = AnsiColor.YELLOW;
    private AnsiColor ceilingColor = AnsiColor.BLUE;
    private AnsiColor wallEdgeColor = AnsiColor.BRIGHT_WHITE; // Color for wall edges
    private boolean drawWallEdges = true; // Enable/disable wall edge drawing
    private double wallEdgeThreshold = 0.3; // Threshold for detecting wall edges
    private TextureProvider textureProvider; // Texture provider for wall textures

    public RaycastingCanvas(String name, int width, int height) {
        super(name, width, height);
        this.mapProvider = new DefaultMapProvider();
    }

    public RaycastingCanvas(String name, int width, int height, MapProvider mapProvider) {
        super(name, width, height);
        this.mapProvider = mapProvider != null ? mapProvider : new DefaultMapProvider();
    }

    @Override
    public void paint(Graphics graphics) {
        if (!isVisible() || mapProvider == null) {
            return;
        }

        int mapWidth = mapProvider.getWidth();
        int mapHeight = mapProvider.getHeight();

        // Cache for textures per paint() call - key: texture name, value: texture
        Map<String, Texture> textureCache = new HashMap<>();

        // Store raycast hits with texture data for efficient rendering
        RaycastHit[] raycastHits = new RaycastHit[getWidth()];

        // First pass: Cast rays, load textures once per entry, and prepare hits
        for (int x = 0; x < getWidth(); x++) {
            // Calculate ray angle
            double rayAngle = playerAngle - (fov / 2) + ((double) x / getWidth()) * fov;

            // Ray direction
            double rayDirX = Math.cos(rayAngle);
            double rayDirY = Math.sin(rayAngle);

            // Perform ray casting using DDA algorithm
            RaycastResult result = castRayDDA(rayDirX, rayDirY, mapWidth, mapHeight);

            // Check if texture is available and load it once per entry
            Texture texture = null;
            int textureColumn = 0;

            if (textureProvider != null && result.hitEntry.getTexture() != null) {
                String textureKey = result.hitEntry.getTexture();

                // Get texture from cache or load it
                texture = textureCache.get(textureKey);
                if (texture == null) {
                    // Calculate wall height for texture loading using effective height
                    double correctedDistance = result.distance * Math.cos(rayAngle - playerAngle);
                    if (correctedDistance < 0.1) correctedDistance = 0.1;
                    int baseWallHeight = (int) (getHeight() / correctedDistance);
                    int wallHeight = (int) (baseWallHeight * result.hitEntry.getEffectiveHeight());

                    // Load texture and cache it
                    texture = textureProvider.getTexture(textureKey, 1, wallHeight, result.hitEntry, !result.isVerticalWall);
                    if (texture != null) {
                        textureCache.put(textureKey, texture);
                    }
                }

                // Calculate the correct texture column based on wall hit position
                if (texture != null) {
                    // Calculate exact hit position on the wall (0.0 to 1.0)
                    double wallX = calculateWallHitPosition(rayDirX, rayDirY, result);
                    textureColumn = (int) (wallX * texture.getWidth());
                    textureColumn = Math.max(0, Math.min(textureColumn, texture.getWidth() - 1));
                }
            }

            // Create raycast hit with texture data
            raycastHits[x] = new RaycastHit(result, texture, textureColumn);
        }

        // Second pass: Render columns with cached texture data
        for (int x = 0; x < getWidth(); x++) {
            RaycastHit hit = raycastHits[x];
            RaycastResult result = hit.result;

            // Calculate ray angle for fish-eye correction
            double rayAngle = playerAngle - (fov / 2) + ((double) x / getWidth()) * fov;

            // Ray direction (needed for floor rendering)
            double rayDirX = Math.cos(rayAngle);
            double rayDirY = Math.sin(rayAngle);

            // Apply fish-eye correction
            double correctedDistance = result.distance * Math.cos(rayAngle - playerAngle);

            // Prevent division by zero
            if (correctedDistance < 0.1) correctedDistance = 0.1;

            // Get entry info for this hit
            EntryInfo hitEntry = result.hitEntry;

            // Calculate wall height based on corrected distance and entry effective height
            int baseWallHeight = (int) (getHeight() / correctedDistance);
            int wallHeight = (int) (baseWallHeight * hitEntry.getEffectiveHeight());

            // Calculate wall start and end positions
            int wallStart = Math.max(0, (getHeight() - wallHeight) / 2);
            int wallEnd = Math.min(getHeight() - 1, (getHeight() + wallHeight) / 2);

            // Check if this column is a wall edge
            boolean isLeftEdge = isWallEdge(raycastHits, x, true);
            boolean isRightEdge = isWallEdge(raycastHits, x, false);

            // Draw ceiling
            for (int y = 0; y < wallStart; y++) {
                graphics.drawStyledChar(x, y, ceilingChar, ceilingColor, null);
            }

            // Draw wall with cached texture data
            renderWallColumn(graphics, x, wallStart, wallEnd, hit, correctedDistance, isLeftEdge, isRightEdge, wallHeight);

            // Draw floor with texture support
            renderFloorColumn(graphics, x, wallEnd + 1, getHeight() - 1, rayDirX, rayDirY, textureCache);
        }
    }

    /**
     * Determines if a column represents a wall edge by comparing distances with neighboring columns.
     */
    private boolean isWallEdge(RaycastHit[] raycastHits, int x, boolean checkLeft) {
        if (!drawWallEdges || raycastHits == null) {
            return false;
        }

        int neighborX = checkLeft ? x - 1 : x + 1;

        // Check bounds
        if (neighborX < 0 || neighborX >= raycastHits.length) {
            return true; // Edge of screen is always an edge
        }

        RaycastHit current = raycastHits[x];
        RaycastHit neighbor = raycastHits[neighborX];

        if (current == null || neighbor == null) {
            return false;
        }

        // Calculate difference in corrected distances
        double currentDistance = current.result.distance * Math.cos(getCurrentRayAngle(x) - playerAngle);
        double neighborDistance = neighbor.result.distance * Math.cos(getCurrentRayAngle(neighborX) - playerAngle);

        // Edge detected if distance difference exceeds threshold
        double distanceDiff = Math.abs(currentDistance - neighborDistance);
        return distanceDiff > wallEdgeThreshold;
    }

    /**
     * Calculate the ray angle for a given column x.
     */
    private double getCurrentRayAngle(int x) {
        return playerAngle - (fov / 2) + ((double) x / getWidth()) * fov;
    }

    /**
     * Result of a raycast operation.
     */
    private static class RaycastResult {
        final double distance;
        final boolean isVerticalWall;
        final EntryInfo hitEntry;

        RaycastResult(double distance, boolean isVerticalWall, EntryInfo hitEntry) {
            this.distance = distance;
            this.isVerticalWall = isVerticalWall;
            this.hitEntry = hitEntry;
        }
    }

    /**
     * Internal class that holds raycast result and cached texture data.
     */
    private static class RaycastHit {
        final RaycastResult result;
        final Texture texture;
        final int textureColumn; // Pre-calculated texture column for this hit

        RaycastHit(RaycastResult result, Texture texture, int textureColumn) {
            this.result = result;
            this.texture = texture;
            this.textureColumn = textureColumn;
        }

        RaycastHit(RaycastResult result) {
            this(result, null, 0);
        }
    }

    /**
     * Cast a ray using DDA (Digital Differential Analyzer) algorithm for better performance and accuracy.
     */
    private RaycastResult castRayDDA(double rayDirX, double rayDirY, int mapWidth, int mapHeight) {
        // Current position
        int mapX = (int) playerX;
        int mapY = (int) playerY;

        // Length of ray from current position to next x or y side
        double deltaDistX = Math.abs(1.0 / rayDirX);
        double deltaDistY = Math.abs(1.0 / rayDirY);

        // Calculate step and initial sideDist
        int stepX, stepY;
        double sideDistX, sideDistY;

        if (rayDirX < 0) {
            stepX = -1;
            sideDistX = (playerX - mapX) * deltaDistX;
        } else {
            stepX = 1;
            sideDistX = (mapX + 1.0 - playerX) * deltaDistX;
        }

        if (rayDirY < 0) {
            stepY = -1;
            sideDistY = (playerY - mapY) * deltaDistY;
        } else {
            stepY = 1;
            sideDistY = (mapY + 1.0 - playerY) * deltaDistY;
        }

        // Perform DDA
        boolean hit = false;
        boolean side = false; // false = x-side, true = y-side
        EntryInfo hitEntry = EntryInfo.createWall(); // Default to wall

        while (!hit) {
            // Jump to next map square, either in x-direction, or in y-direction
            if (sideDistX < sideDistY) {
                sideDistX += deltaDistX;
                mapX += stepX;
                side = false;
            } else {
                sideDistY += deltaDistY;
                mapY += stepY;
                side = true;
            }

            // Check if ray is out of bounds
            if (mapX < 0 || mapX >= mapWidth || mapY < 0 || mapY >= mapHeight) {
                hit = true;
                hitEntry = EntryInfo.createWall(); // Out of bounds is treated as wall
                break;
            }

            // Get entry info at current position
            EntryInfo currentEntry = mapProvider.getEntry(mapX, mapY);

            // Check if ray has hit a solid entry (wall or non-transparent obstacle)
            if (currentEntry.isWall()) {
                hit = true;
                hitEntry = currentEntry;
            }
        }

        // Calculate distance
        double perpWallDist;
        if (!side) {
            perpWallDist = (mapX - playerX + (1 - stepX) / 2.0) / rayDirX;
        } else {
            perpWallDist = (mapY - playerY + (1 - stepY) / 2.0) / rayDirY;
        }

        return new RaycastResult(Math.abs(perpWallDist), !side, hitEntry);
    }

    /**
     * Get wall shading based on distance and wall orientation.
     */
    private AnsiColor getWallShade(double distance, boolean isVerticalWall) {
        // Darker shading for horizontal walls to create depth effect
        AnsiColor baseColor = isVerticalWall ? wallColor :
            (wallColor == AnsiColor.WHITE ? AnsiColor.BRIGHT_BLACK : wallColor);

        // Distance-based shading
        if (distance > 8.0) {
            return AnsiColor.BLACK;
        } else if (distance > 4.0) {
            return AnsiColor.BRIGHT_BLACK;
        } else {
            return baseColor;
        }
    }

    /**
     * Move the player forward/backward.
     */
    public void movePlayer(double distance) {
        double newX = playerX + Math.cos(playerAngle) * distance;
        double newY = playerY + Math.sin(playerAngle) * distance;

        // Check collision
        if (isValidPosition(newX, newY)) {
            playerX = newX;
            playerY = newY;
        }
    }

    /**
     * Strafe the player left/right.
     */
    public void strafePlayer(double distance) {
        double strafeAngle = playerAngle + Math.PI / 2;
        double newX = playerX + Math.cos(strafeAngle) * distance;
        double newY = playerY + Math.sin(strafeAngle) * distance;

        // Check collision
        if (isValidPosition(newX, newY)) {
            playerX = newX;
            playerY = newY;
        }
    }

    /**
     * Rotate the player.
     */
    public void rotatePlayer(double angle) {
        playerAngle += angle;
        // Normalize angle to [0, 2π]
        while (playerAngle < 0) playerAngle += 2 * Math.PI;
        while (playerAngle >= 2 * Math.PI) playerAngle -= 2 * Math.PI;
    }

    /**
     * Check if a position is valid for player movement.
     * Uses EntryInfo properties to determine walkability.
     */
    public boolean isValidPosition(double x, double y) {
        if (mapProvider == null) return false;

        int mapX = (int) Math.floor(x);
        int mapY = (int) Math.floor(y);

        // Check bounds
        if (mapX < 0 || mapX >= mapProvider.getWidth() || mapY < 0 || mapY >= mapProvider.getHeight()) {
            return false;
        }

        // Check if the position allows movement using EntryInfo
        EntryInfo entry = mapProvider.getEntry(mapX, mapY);
        return entry.isWalkThrough();
    }

    /**
     * Set a new map provider for the raycasting world.
     */
    public void setMapProvider(MapProvider mapProvider) {
        this.mapProvider = mapProvider != null ? mapProvider : new DefaultMapProvider();

        // Reset player position to a safe location if current position is invalid
        if (!isValidPosition(playerX, playerY)) {
            // Find first walkable space
            for (int y = 0; y < this.mapProvider.getHeight(); y++) {
                for (int x = 0; x < this.mapProvider.getWidth(); x++) {
                    EntryInfo entry = this.mapProvider.getEntry(x, y);
                    if (entry.isWalkThrough()) {
                        playerX = x + 0.5;
                        playerY = y + 0.5;
                        return;
                    }
                }
            }
        }
    }

    /**
     * Set a new map for the raycasting world (creates a DefaultMapProvider).
     */
    public void setMap(String[] map) {
        setMapProvider(new DefaultMapProvider("Custom Map", map));
    }

    /**
     * Set player position.
     */
    public void setPlayerPosition(double x, double y) {
        if (isValidPosition(x, y)) {
            this.playerX = x;
            this.playerY = y;
        }
    }

    /**
     * Get the current map as string array for backward compatibility.
     */
    public String[] getMap() {
        if (mapProvider == null) return new String[0];

        if (mapProvider instanceof DefaultMapProvider defaultProvider) {
            return defaultProvider.toStringArray();
        }

        // Fallback: convert EntryInfo back to characters
        String[] map = new String[mapProvider.getHeight()];
        for (int y = 0; y < mapProvider.getHeight(); y++) {
            StringBuilder row = new StringBuilder();
            for (int x = 0; x < mapProvider.getWidth(); x++) {
                row.append(mapProvider.getEntry(x, y).toCharacter());
            }
            map[y] = row.toString();
        }
        return map;
    }

    /**
     * Render a column of the wall with support for textures and EntryInfo properties.
     */
    private void renderWallColumn(Graphics graphics, int x, int wallStart, int wallEnd, RaycastHit hit,
                                  double distance, boolean isLeftEdge, boolean isRightEdge, double actualWallHeight) {

        // Check if texture is available
        Texture texture = hit.texture;
        boolean isVertical = hit.result.isVerticalWall;

        // If texture is available, render using texture
        if (texture != null) {
            renderTexturedWallColumn(graphics, x, wallStart, wallEnd, hit, !isVertical, isLeftEdge, isRightEdge, actualWallHeight);
        } else {
            // Render without texture using EntryInfo properties
            renderPlainWallColumn(graphics, x, wallStart, wallEnd, hit.result.hitEntry, isVertical, distance, isLeftEdge, isRightEdge);
        }
    }

    /**
     * Render a textured wall column.
     */
    private void renderTexturedWallColumn(Graphics graphics, int x, int wallStart, int wallEnd, RaycastHit hit,
                                         boolean isDarkSide, boolean isLeftEdge, boolean isRightEdge, double actualWallHeight) {
        Texture texture = hit.texture;
        EntryInfo hitEntry = hit.result.hitEntry;
        int textureColumn = hit.textureColumn;

        // Calculate the actual wall center position
        double screenCenter = getHeight() / 2.0;
        double actualWallStart = screenCenter - actualWallHeight / 2.0;
        double actualWallEnd = screenCenter + actualWallHeight / 2.0;

        for (int y = wallStart; y <= wallEnd; y++) {
            // Calculate position relative to the actual (unclipped) wall
            double actualY = y;

            // Calculate progress along the actual wall height (not just visible portion)
            double wallProgress = (actualY - actualWallStart) / actualWallHeight;

            // Ensure wallProgress is within valid range
            wallProgress = Math.max(0.0, Math.min(1.0, wallProgress));

            // Map to texture Y coordinate
            int textureY = (int) (wallProgress * texture.getHeight());
            textureY = Math.max(0, Math.min(textureY, texture.getHeight() - 1));

            // Get character and color from texture using the correct column
            StyledChar textureChar = texture.getCharAt(textureColumn, textureY);

            char charToDraw = hitEntry.getCharacter();
            AnsiColor colorToDraw = hitEntry.getColor(isDarkSide);
            AnsiColor backgroundColorToDraw = hitEntry.getBackgroundColor(isDarkSide);

            if (textureChar != null) {
                charToDraw = textureChar.getCharacter();
                colorToDraw = textureChar.getForegroundColor();
                // Use texture background color if available, otherwise use EntryInfo background color
                if (textureChar.getBackgroundColor() != null) {
                    backgroundColorToDraw = textureChar.getBackgroundColor();
                }
            }

            // Apply edge rendering if enabled
            if (drawWallEdges && (isLeftEdge || isRightEdge)) {
                charToDraw = wallEdgeChar;
                colorToDraw = wallEdgeColor;
                // Keep background color from EntryInfo for edges
            }

            graphics.drawStyledChar(x, y, charToDraw, colorToDraw, backgroundColorToDraw);
        }
    }

    /**
     * Render a plain wall column without texture.
     */
    private void renderPlainWallColumn(Graphics graphics, int x, int wallStart, int wallEnd, EntryInfo hitEntry,
                                      boolean isVertical, double distance, boolean isLeftEdge, boolean isRightEdge) {
        // Use EntryInfo color based on wall orientation (light for vertical, dark for horizontal)
        boolean isDarkSide = !isVertical;
        AnsiColor entryColor = hitEntry.getColor(isDarkSide);
        AnsiColor colorToDraw = entryColor != null ? entryColor : getWallShade(distance, isVertical);

        // Use EntryInfo background color based on wall orientation
        AnsiColor backgroundColorToDraw = hitEntry.getBackgroundColor(isDarkSide);

        // Use EntryInfo character or fall back to default wall char
        char charToDraw = hitEntry.getCharacter() != ' ' ? hitEntry.getCharacter() : wallChar;

        // Draw edge lines if enabled and this is an edge
        if (drawWallEdges && (isLeftEdge || isRightEdge)) {
            charToDraw = wallEdgeChar;
            colorToDraw = wallEdgeColor;
            // Keep background color from EntryInfo for edges
        }

        for (int y = wallStart; y <= wallEnd; y++) {
            graphics.drawStyledChar(x, y, charToDraw, colorToDraw, backgroundColorToDraw);
        }
    }

    /**
     * Render the floor column with support for textures.
     */
    private void renderFloorColumn(Graphics graphics, int x, int floorStart, int floorEnd, double rayDirX, double rayDirY,
                                   Map<String, Texture> textureCache) {
        for (int y = floorStart; y <= floorEnd; y++) {
            // Calculate floor position using ray casting for floor rendering
            double floorDistance = getHeight() / (2.0 * y - getHeight());

            // Calculate floor world position
            double floorX = playerX + floorDistance * rayDirX;
            double floorY = playerY + floorDistance * rayDirY;

            // Get floor EntryInfo at calculated position
            int floorMapX = (int) Math.floor(floorX);
            int floorMapY = (int) Math.floor(floorY);

            // Default values
            AnsiColor defaultFloorColor = floorColor;
            char defaultFloorChar = floorChar;
            EntryInfo floorEntry = null;

            // Check if floor position is within map bounds and get EntryInfo
            if (floorMapX >= 0 && floorMapX < mapProvider.getWidth() && floorMapY >= 0 && floorMapY < mapProvider.getHeight()) {
                floorEntry = mapProvider.getEntry(floorMapX, floorMapY);

                // Use floor entry colors if available
                if (floorEntry.getColor(false) != null) {
                    defaultFloorColor = floorEntry.getColor(false); // Use light color for floor
                }

                // Use floor entry character if it's not a wall
                if (!floorEntry.isWall() && floorEntry.getCharacter() != ' ') {
                    defaultFloorChar = floorEntry.getCharacter();
                }
            }

            // Check for floor texture - use EntryInfo texture if available, otherwise try generic "floor"
            Texture texture = null;
            String textureKey = null;

            if (textureProvider != null && textureCache != null) {
                // First try to get texture from the specific floor entry
                if (floorEntry != null && floorEntry.getTexture() != null) {
                    textureKey = floorEntry.getTexture();
                }

                texture = textureCache.get(textureKey);

                if (texture == null && textureKey != null) {
                    // Texture not cached, create a new one
                    int floorHeight = 1; // Fixed height for floor texture
                    texture = textureProvider.getTexture(textureKey, 1, floorHeight, floorEntry, false);

                    if (texture != null) {
                        // Cache the newly created texture
                        textureCache.put(textureKey, texture);
                    }
                }
            }

            // Draw the floor pixel
            StyledChar floorPixel = null;
            if (texture != null) {
                // Calculate both texture coordinates based on floor position
                double floorXFrac = floorX - Math.floor(floorX);
                double floorYFrac = floorY - Math.floor(floorY);

                int textureX = (int) (floorXFrac * texture.getWidth());
                int textureY = (int) (floorYFrac * texture.getHeight());

                textureX = Math.max(0, Math.min(textureX, texture.getWidth() - 1));
                textureY = Math.max(0, Math.min(textureY, texture.getHeight() - 1));

                floorPixel = texture.getCharAt(textureX, textureY);
            }

            char charToDraw = defaultFloorChar;
            AnsiColor colorToDraw = defaultFloorColor;
            AnsiColor backgroundColorToDraw = null;

            if (floorPixel != null) {
                charToDraw = floorPixel.getCharacter();
                colorToDraw = floorPixel.getForegroundColor();
                backgroundColorToDraw = floorPixel.getBackgroundColor();
            }

            // Check if floor entry has background color and use it if texture doesn't provide one
            if (backgroundColorToDraw == null && floorEntry != null) {
                backgroundColorToDraw = floorEntry.getBackgroundColor(false); // Use light background for floor
            }

            graphics.drawStyledChar(x, y, charToDraw, colorToDraw, backgroundColorToDraw);
        }
    }

    /**
     * Calculate the exact hit position on the wall (0.0 to 1.0).
     */
    private double calculateWallHitPosition(double rayDirX, double rayDirY, RaycastResult result) {
        // Calculate the perpendicular wall distance
        double perpWallDist = result.distance;

        // Calculate exact hit position in world coordinates
        double hitWorldX = playerX + perpWallDist * rayDirX;
        double hitWorldY = playerY + perpWallDist * rayDirY;

        // Calculate wall texture coordinate (0.0 to 1.0)
        double wallX;
        if (result.isVerticalWall) {
            // Hit on vertical wall (NS side)
            wallX = hitWorldY - Math.floor(hitWorldY);
            if (rayDirX > 0) wallX = 1.0 - wallX; // Flip texture for consistency
        } else {
            // Hit on horizontal wall (EW side)
            wallX = hitWorldX - Math.floor(hitWorldX);
            if (rayDirY < 0) wallX = 1.0 - wallX; // Flip texture for consistency
        }

        return wallX;
    }
}
