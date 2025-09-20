package com.consolemaster.raycasting;

import com.consolemaster.Canvas;
import com.consolemaster.Graphics;
import com.consolemaster.AnsiColor;
import lombok.Getter;
import lombok.Setter;

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

        // Store raycast results for edge detection
        RaycastResult[] raycastResults = new RaycastResult[getWidth()];

        // First pass: Cast rays and store results
        for (int x = 0; x < getWidth(); x++) {
            // Calculate ray angle
            double rayAngle = playerAngle - (fov / 2) + ((double) x / getWidth()) * fov;

            // Ray direction
            double rayDirX = Math.cos(rayAngle);
            double rayDirY = Math.sin(rayAngle);

            // Perform ray casting using DDA algorithm
            raycastResults[x] = castRayDDA(rayDirX, rayDirY, mapWidth, mapHeight);
        }

        // Second pass: Render columns with edge detection
        for (int x = 0; x < getWidth(); x++) {
            RaycastResult result = raycastResults[x];

            // Calculate ray angle for fish-eye correction
            double rayAngle = playerAngle - (fov / 2) + ((double) x / getWidth()) * fov;

            // Apply fish-eye correction
            double correctedDistance = result.distance * Math.cos(rayAngle - playerAngle);

            // Prevent division by zero
            if (correctedDistance < 0.1) correctedDistance = 0.1;

            // Get entry info for this hit
            EntryInfo hitEntry = result.hitEntry;

            // Calculate wall height based on corrected distance and entry height
            int baseWallHeight = (int) (getHeight() / correctedDistance);
            int wallHeight = (int) (baseWallHeight * hitEntry.getHeight());

            // Calculate wall start and end positions
            int wallStart = Math.max(0, (getHeight() - wallHeight) / 2);
            int wallEnd = Math.min(getHeight() - 1, (getHeight() + wallHeight) / 2);

            // Check if this column is a wall edge
            boolean isLeftEdge = isWallEdge(raycastResults, x, true);
            boolean isRightEdge = isWallEdge(raycastResults, x, false);

            // Draw ceiling
            for (int y = 0; y < wallStart; y++) {
                graphics.drawStyledChar(x, y, ceilingChar, ceilingColor, null);
            }

            // Draw wall with EntryInfo properties
            for (int y = wallStart; y <= wallEnd; y++) {
                // Use EntryInfo color based on wall orientation (light for vertical, dark for horizontal)
                boolean isDarkSide = !result.isVerticalWall;
                AnsiColor entryColor = hitEntry.getColor(isDarkSide);
                AnsiColor colorToDraw = entryColor != null ? entryColor : getWallShade(correctedDistance, result.isVerticalWall);

                // Use EntryInfo character or fall back to default wall char
                char charToDraw = hitEntry.getCharacter() != ' ' ? hitEntry.getCharacter() : wallChar;

                // Draw edge lines if enabled and this is an edge
                if (drawWallEdges && (isLeftEdge || isRightEdge)) {
                    charToDraw = wallEdgeChar;
                    colorToDraw = wallEdgeColor;
                }

                graphics.drawStyledChar(x, y, charToDraw, colorToDraw, null);
            }

            // Draw floor
            for (int y = wallEnd + 1; y < getHeight(); y++) {
                graphics.drawStyledChar(x, y, floorChar, floorColor, null);
            }
        }
    }

    /**
     * Determines if a column represents a wall edge by comparing distances with neighboring columns.
     */
    private boolean isWallEdge(RaycastResult[] raycastResults, int x, boolean checkLeft) {
        if (!drawWallEdges || raycastResults == null) {
            return false;
        }

        int neighborX = checkLeft ? x - 1 : x + 1;

        // Check bounds
        if (neighborX < 0 || neighborX >= raycastResults.length) {
            return true; // Edge of screen is always an edge
        }

        RaycastResult current = raycastResults[x];
        RaycastResult neighbor = raycastResults[neighborX];

        if (current == null || neighbor == null) {
            return false;
        }

        // Calculate difference in corrected distances
        double currentDistance = current.distance * Math.cos(getCurrentRayAngle(x) - playerAngle);
        double neighborDistance = neighbor.distance * Math.cos(getCurrentRayAngle(neighborX) - playerAngle);

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
            perpWallDist = (mapX - playerX + (1 - stepX) / 2) / rayDirX;
        } else {
            perpWallDist = (mapY - playerY + (1 - stepY) / 2) / rayDirY;
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
        return entry.isFallthrough();
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
                    if (entry.isFallthrough()) {
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
}
