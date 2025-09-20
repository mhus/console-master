package com.consolemaster.raycasting;

import com.consolemaster.Canvas;
import com.consolemaster.Graphics;
import com.consolemaster.AnsiColor;
import com.consolemaster.StyledChar;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * A canvas that renders a 3D world using raycasting technique.
 * The world is represented by a MapProvider where '#' represents walls and ' ' represents empty space.
 * Supports 8-directional sprite rendering for game objects.
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

    // Background rendering configuration
    private BackgroundProvider backgroundProvider; // Provider for background rendering
    private boolean renderBackground = true; // Enable/disable background rendering

    // Ceiling rendering configuration
    private boolean renderCeilings = true; // Enable/disable ceiling rendering from EntryInfo
    private double defaultCeilingHeight = 1.0; // Default ceiling height when not specified in EntryInfo

    // GameObject support
    private List<GameObject> gameObjects = new ArrayList<>();
    private boolean renderGameObjects = true; // Enable/disable object rendering
    private double[] zBuffer; // Z-buffer for depth testing with objects

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

        // Initialize Z-buffer for depth testing
        if (zBuffer == null || zBuffer.length != getWidth()) {
            zBuffer = new double[getWidth()];
        }

        // Cache for textures per paint() call - key: texture name, value: texture
        Map<String, Texture> textureCache = new HashMap<>();

        // Store raycast hits with texture data for efficient rendering
        RaycastHit[] raycastHits = new RaycastHit[getWidth()];

        // Render background first if enabled - it will be overwritten by walls, floors, and ceilings
        if (renderBackground && backgroundProvider != null) {
            backgroundProvider.setDimensionAndAngle(getWidth(), getHeight(), playerAngle);
        }

        // First pass: Cast rays, load textures once per entry, and prepare hits
        for (int x = 0; x < getWidth(); x++) {
            // Calculate ray angle
            double rayAngle = playerAngle - (fov / 2) + ((double) x / getWidth()) * fov;

            // Ray direction
            double rayDirX = Math.cos(rayAngle);
            double rayDirY = Math.sin(rayAngle);

            // Perform ray casting using DDA algorithm
            RaycastResult result = castRayDDA(rayDirX, rayDirY, mapWidth, mapHeight);

            // Store distance in Z-buffer for object rendering
            double correctedDistance = result.distance * Math.cos(rayAngle - playerAngle);
            zBuffer[x] = correctedDistance;

            // Check if texture is available and load it once per entry
            Texture texture = null;
            int textureColumn = 0;

            if (textureProvider != null && result.hitEntry.getTexture() != null) {
                String textureKey = result.hitEntry.getTexture();

                // Get texture from cache or load it
                texture = textureCache.get(textureKey);
                if (texture == null) {
                    // Calculate wall height for texture loading using effective height
                    double effectiveHeight = result.hitEntry.getEffectiveHeight();
                    double correctedDistanceForHeight = correctedDistance;
                    if (correctedDistanceForHeight < 0.1) correctedDistanceForHeight = 0.1;
                    int baseWallHeight = (int) (getHeight() / correctedDistanceForHeight);
                    int wallHeight = (int) (baseWallHeight * effectiveHeight);

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

            // Draw ceiling with EntryInfo support
            renderCeilingColumn(graphics, x, 0, wallStart - 1, rayDirX, rayDirY, textureCache);

            // Draw wall with cached texture data
            renderWallColumn(graphics, x, wallStart, wallEnd, hit, correctedDistance, isLeftEdge, isRightEdge, wallHeight);

            // Draw floor with texture support
            renderFloorColumn(graphics, x, wallEnd + 1, getHeight() - 1, rayDirX, rayDirY, textureCache);
        }

        // Third pass: Render game objects with sprite support
        if (renderGameObjects && !gameObjects.isEmpty()) {
            renderGameObjects(graphics);
        }
    }

    /**
     * Renders all game objects using 8-directional sprites with proper depth sorting.
     */
    private void renderGameObjects(Graphics graphics) {
        List<ObjectRenderInfo> objectsToRender = new ArrayList<>();

        // Calculate render info for all visible objects
        for (GameObject obj : gameObjects) {
            if (!obj.shouldRender(playerX, playerY)) {
                continue;
            }

            double distance = obj.getDistanceTo(playerX, playerY);
            double angleToObject = Math.atan2(obj.getY() - playerY, obj.getX() - playerX);

            // Check if object is within field of view
            double relativeAngle = angleToObject - playerAngle;
            while (relativeAngle < -Math.PI) relativeAngle += 2 * Math.PI;
            while (relativeAngle > Math.PI) relativeAngle -= 2 * Math.PI;

            // Only render objects within FOV (with some margin)
            double fovMargin = fov / 2 + 0.5; // Add margin for sprite width
            if (Math.abs(relativeAngle) <= fovMargin) {
                // Get appropriate sprite for viewing angle
                Sprite sprite = obj.getSpriteForViewAngle(angleToObject);
                if (sprite != null) {
                    objectsToRender.add(new ObjectRenderInfo(obj, sprite, distance, angleToObject));
                }
            }
        }

        // Sort objects by distance (far to near) for proper rendering order
        objectsToRender.sort(Comparator.comparingDouble(info -> -info.distance));

        // Render each object
        for (ObjectRenderInfo renderInfo : objectsToRender) {
            renderGameObject(graphics, renderInfo);
        }
    }

    /**
     * Renders a single game object sprite with proper depth testing.
     */
    private void renderGameObject(Graphics graphics, ObjectRenderInfo renderInfo) {
        GameObject obj = renderInfo.gameObject;
        Sprite sprite = renderInfo.sprite;
        double distance = renderInfo.distance;

        // Apply fish-eye correction
        double correctedDistance = distance; // Objects don't need fish-eye correction

        // Calculate sprite position on screen
        double angleToObject = renderInfo.angleToObject;
        double relativeAngle = angleToObject - playerAngle;
        while (relativeAngle < -Math.PI) relativeAngle += 2 * Math.PI;
        while (relativeAngle > Math.PI) relativeAngle -= 2 * Math.PI;

        // Calculate screen X position
        int screenX = (int) ((relativeAngle / fov + 0.5) * getWidth());

        // Calculate sprite size based on distance and sprite scale
        double baseScale = obj.getSpriteProvider().getBaseScale() * sprite.getScale();
        int spriteScreenHeight = (int) (getHeight() / correctedDistance * baseScale);
        int spriteScreenWidth = (int) (sprite.getWidth() * spriteScreenHeight / (double) sprite.getHeight());

        // Calculate sprite position (centered on object)
        int spriteStartX = screenX - spriteScreenWidth / 2;
        int spriteStartY = (getHeight() - spriteScreenHeight) / 2 + (int) (obj.getZOffset() * spriteScreenHeight);

        // Render sprite pixels
        for (int sy = 0; sy < spriteScreenHeight; sy++) {
            int screenY = spriteStartY + sy;
            if (screenY < 0 || screenY >= getHeight()) continue;

            // Calculate sprite Y coordinate
            int spriteY = (sy * sprite.getHeight()) / spriteScreenHeight;
            spriteY = Math.max(0, Math.min(spriteY, sprite.getHeight() - 1));

            for (int sx = 0; sx < spriteScreenWidth; sx++) {
                int screenXPos = spriteStartX + sx;
                if (screenXPos < 0 || screenXPos >= getWidth()) continue;

                // Depth test against Z-buffer
                if (correctedDistance >= zBuffer[screenXPos]) continue;

                // Calculate sprite X coordinate
                int spriteX = (sx * sprite.getWidth()) / spriteScreenWidth;
                spriteX = Math.max(0, Math.min(spriteX, sprite.getWidth() - 1));

                // Check if sprite pixel is transparent
                if (sprite.isTransparentAt(spriteX, spriteY)) continue;

                // Get sprite pixel data
                char spriteChar = sprite.getCharAt(spriteX, spriteY);
                AnsiColor fgColor = sprite.getForegroundColorAt(spriteX, spriteY);
                AnsiColor bgColor = sprite.getBackgroundColorAt(spriteX, spriteY);

                // Apply distance-based shading
                fgColor = applySpriteShading(fgColor, correctedDistance);

                // Render sprite pixel
                graphics.drawStyledChar(screenXPos, screenY, spriteChar, fgColor, bgColor);
            }
        }
    }

    /**
     * Applies distance-based shading to sprite colors.
     */
    private AnsiColor applySpriteShading(AnsiColor color, double distance) {
        if (color == null) return null;

        // Apply distance-based darkening
        if (distance > 8.0) {
            return AnsiColor.BLACK;
        } else if (distance > 4.0) {
            // Darken the color
            return switch (color) {
                case WHITE, BRIGHT_WHITE -> AnsiColor.BRIGHT_BLACK;
                case YELLOW, BRIGHT_YELLOW -> AnsiColor.YELLOW;
                case RED, BRIGHT_RED -> AnsiColor.RED;
                case GREEN, BRIGHT_GREEN -> AnsiColor.GREEN;
                case BLUE, BRIGHT_BLUE -> AnsiColor.BLUE;
                case MAGENTA, BRIGHT_MAGENTA -> AnsiColor.MAGENTA;
                case CYAN, BRIGHT_CYAN -> AnsiColor.CYAN;
                default -> color;
            };
        }
        return color;
    }

    /**
     * Internal class for storing object render information.
     */
    private static class ObjectRenderInfo {
        final GameObject gameObject;
        final Sprite sprite;
        final double distance;
        final double angleToObject;

        ObjectRenderInfo(GameObject gameObject, Sprite sprite, double distance, double angleToObject) {
            this.gameObject = gameObject;
            this.sprite = sprite;
            this.distance = distance;
            this.angleToObject = angleToObject;
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
     * Render the ceiling column with support for EntryInfo-based ceilings and textures.
     */
    private void renderCeilingColumn(Graphics graphics, int x, int ceilingStart, int ceilingEnd, double rayDirX, double rayDirY,
                                   Map<String, Texture> textureCache) {
        if (!renderCeilings || ceilingEnd < ceilingStart) {
            // Fill with default ceiling if ceiling rendering is disabled or invalid range
            for (int y = ceilingStart; y <= ceilingEnd; y++) {
                if (renderBackground && backgroundProvider != null) {
                    // Let background provider handle ceiling area
                    var backgroundChar = backgroundProvider.getBackground(x, y);
                    graphics.drawStyledChar(x, y, backgroundChar.getCharacter(), backgroundChar.getForegroundColor(), backgroundChar.getBackgroundColor());
                    continue;
                }
                graphics.drawStyledChar(x, y, ceilingChar, ceilingColor, null);
            }
            return;
        }

        for (int y = ceilingStart; y <= ceilingEnd; y++) {
            // Calculate ceiling position using ray casting (similar to floor but inverted)
            // For ceiling, we calculate distance based on height above player eye level
            double ceilingDistance = getHeight() / (getHeight() - 2.0 * y);

            // Calculate ceiling world position
            double ceilingX = playerX + ceilingDistance * rayDirX;
            double ceilingY = playerY + ceilingDistance * rayDirY;

            // Get ceiling EntryInfo at calculated position
            int ceilingMapX = (int) Math.floor(ceilingX);
            int ceilingMapY = (int) Math.floor(ceilingY);

            // Default values
            AnsiColor defaultCeilingColor = ceilingColor;
            char defaultCeilingChar = ceilingChar;
            EntryInfo ceilingEntry = null;
            boolean hasCeilingAtPosition = false;

            // Check if ceiling position is within map bounds and get EntryInfo
            if (ceilingMapX >= 0 && ceilingMapX < mapProvider.getWidth() &&
                ceilingMapY >= 0 && ceilingMapY < mapProvider.getHeight()) {
                ceilingEntry = mapProvider.getEntry(ceilingMapX, ceilingMapY);

                // Check if this entry has a ceiling defined
                if (ceilingEntry.isHasCeiling()) {
                    hasCeilingAtPosition = true;

                    // Use ceiling-specific colors if available
                    AnsiColor entryColor = ceilingEntry.getCeilingColor(false); // Use light color for ceiling
                    if (entryColor != null) {
                        defaultCeilingColor = entryColor;
                    }

                    // Use ceiling character if available
                    defaultCeilingChar = ceilingEntry.getCeilingCharacter();
                }
            }

            // If no ceiling is defined at this position, use default ceiling
            if (!hasCeilingAtPosition) {
                if (renderBackground && backgroundProvider != null) {
                    // Let background provider handle ceiling area
                    var backgroundChar = backgroundProvider.getBackground(x, y);
                    graphics.drawStyledChar(x, y, backgroundChar.getCharacter(), backgroundChar.getForegroundColor(), backgroundChar.getBackgroundColor());
                    continue;
                }
                graphics.drawStyledChar(x, y, ceilingChar, ceilingColor, null);
                continue;
            }

            // Check for ceiling texture - similar to floor texture handling
            Texture texture = null;
            String textureKey = null;

            if (textureProvider != null && textureCache != null && ceilingEntry != null) {
                // Check if ceiling entry has a texture
                if (ceilingEntry.getTexture() != null) {
                    textureKey = "ceiling_" + ceilingEntry.getTexture(); // Prefix to distinguish from wall/floor textures
                }

                texture = textureCache.get(textureKey);

                if (texture == null && textureKey != null) {
                    // Texture not cached, create a new one
                    int ceilingHeight = 1; // Fixed height for ceiling texture
                    texture = textureProvider.getTexture(ceilingEntry.getTexture(), 1, ceilingHeight, ceilingEntry, false);

                    if (texture != null) {
                        // Cache the newly created texture with ceiling prefix
                        textureCache.put(textureKey, texture);
                    }
                }
            }

            // Draw the ceiling pixel
            StyledChar ceilingPixel = null;
            if (texture != null) {
                // Calculate both texture coordinates based on ceiling position
                double ceilingXFrac = ceilingX - Math.floor(ceilingX);
                double ceilingYFrac = ceilingY - Math.floor(ceilingY);

                int textureX = (int) (ceilingXFrac * texture.getWidth());
                int textureY = (int) (ceilingYFrac * texture.getHeight());

                textureX = Math.max(0, Math.min(textureX, texture.getWidth() - 1));
                textureY = Math.max(0, Math.min(textureY, texture.getHeight() - 1));

                ceilingPixel = texture.getCharAt(textureX, textureY);
            }

            char charToDraw = defaultCeilingChar;
            AnsiColor colorToDraw = defaultCeilingColor;
            AnsiColor backgroundColorToDraw = null;

            if (ceilingPixel != null) {
                charToDraw = ceilingPixel.getCharacter();
                colorToDraw = ceilingPixel.getForegroundColor();
                backgroundColorToDraw = ceilingPixel.getBackgroundColor();
            }

            // Check if ceiling entry has background color and use it if texture doesn't provide one
            if (backgroundColorToDraw == null && ceilingEntry != null) {
                backgroundColorToDraw = ceilingEntry.getCeilingBackgroundColor(false); // Use light background for ceiling
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
     * Adds a game object to the world.
     */
    public void addGameObject(GameObject obj) {
        if (obj != null && !gameObjects.contains(obj)) {
            gameObjects.add(obj);
        }
    }

    /**
     * Removes a game object from the world.
     */
    public void removeGameObject(GameObject obj) {
        gameObjects.remove(obj);
    }

    /**
     * Gets all game objects in the world.
     */
    public List<GameObject> getGameObjects() {
        return new ArrayList<>(gameObjects);
    }

    /**
     * Clears all game objects from the world.
     */
    public void clearGameObjects() {
        gameObjects.clear();
    }

    /**
     * Finds the closest game object to the player within interaction range.
     */
    public GameObject getClosestInteractableObject(double maxDistance) {
        GameObject closest = null;
        double closestDistance = maxDistance;

        for (GameObject obj : gameObjects) {
            if (!obj.isInteractable()) continue;

            double distance = obj.getDistanceTo(playerX, playerY);
            if (distance < closestDistance) {
                closest = obj;
                closestDistance = distance;
            }
        }

        return closest;
    }

    /**
     * Checks collision with game objects during player movement.
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
        if (!entry.isWalkThrough()) {
            return false;
        }

        // Check collision with solid game objects
        for (GameObject obj : gameObjects) {
            if (!obj.isSolid()) continue;

            double distance = Math.sqrt((x - obj.getX()) * (x - obj.getX()) +
                                      (y - obj.getY()) * (y - obj.getY()));

            // Simple collision detection - objects have a radius of 0.3 units
            if (distance < 0.3) {
                return false;
            }
        }

        return true;
    }

    /**
     * Render the background using the configured BackgroundProvider.
     * The background is rendered for areas where no floor or ceiling is displayed.
     * XXX
     */
    private void renderBackground(Graphics graphics) {
        if (backgroundProvider == null) return;

        // Render background for the entire canvas - it will be overwritten by walls, floors, and ceilings
        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                StyledChar bgChar = backgroundProvider.getBackground(x, y);
                if (bgChar != null) {
                    graphics.drawStyledChar(x, y, bgChar.getCharacter(),
                                          bgChar.getForegroundColor(),
                                          bgChar.getBackgroundColor());
                }
            }
        }
    }
}
