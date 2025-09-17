package com.consolemaster.raycasting;

import com.consolemaster.Canvas;
import com.consolemaster.Graphics;
import com.consolemaster.AnsiColor;
import lombok.Getter;
import lombok.Setter;

/**
 * A canvas that renders a 3D world using raycasting technique.
 * The world is represented as a 2D map where '#' represents walls and ' ' represents empty space.
 */
@Getter
@Setter
public class RaycastingCanvas extends Canvas {

    private String[] map;
    private double playerX = 2.0;
    private double playerY = 2.0;
    private double playerAngle = 0.0;
    private double fov = Math.PI / 3; // 60 degrees field of view
    private char wallChar = '█';
    private char floorChar = '.';
    private char ceilingChar = ' ';
    private AnsiColor wallColor = AnsiColor.WHITE;
    private AnsiColor floorColor = AnsiColor.YELLOW;
    private AnsiColor ceilingColor = AnsiColor.BLUE;

    // Default map if none is provided
    private static final String[] DEFAULT_MAP = {
        "########",
        "#      #",
        "#  ##  #",
        "#      #",
        "########"
    };

    public RaycastingCanvas(String name, int width, int height) {
        super(name, width, height);
        this.map = DEFAULT_MAP;
    }

    public RaycastingCanvas(String name, int width, int height, String[] map) {
        super(name, width, height);
        this.map = map != null ? map : DEFAULT_MAP;
    }

    @Override
    public void paint(Graphics graphics) {
        if (!isVisible() || map == null || map.length == 0) {
            return;
        }

        int mapWidth = map[0].length();
        int mapHeight = map.length;

        // Cast rays for each column of the screen
        for (int x = 0; x < getWidth(); x++) {
            // Calculate ray angle
            double rayAngle = playerAngle - (fov / 2) + ((double) x / getWidth()) * fov;

            // Ray direction
            double rayDirX = Math.cos(rayAngle);
            double rayDirY = Math.sin(rayAngle);

            // Perform ray casting using DDA algorithm
            RaycastResult result = castRayDDA(rayDirX, rayDirY, mapWidth, mapHeight);

            // Apply fish-eye correction
            double correctedDistance = result.distance * Math.cos(rayAngle - playerAngle);

            // Prevent division by zero
            if (correctedDistance < 0.1) correctedDistance = 0.1;

            // Calculate wall height based on corrected distance
            int wallHeight = (int) (getHeight() / correctedDistance);

            // Calculate wall start and end positions
            int wallStart = Math.max(0, (getHeight() - wallHeight) / 2);
            int wallEnd = Math.min(getHeight() - 1, (getHeight() + wallHeight) / 2);

            // Draw ceiling
            for (int y = 0; y < wallStart; y++) {
                graphics.drawStyledChar(x, y, ceilingChar, ceilingColor, null);
            }

            // Draw wall with distance-based shading
            for (int y = wallStart; y <= wallEnd; y++) {
                AnsiColor wallShade = getWallShade(correctedDistance, result.isVerticalWall);
                graphics.drawStyledChar(x, y, wallChar, wallShade, null);
            }

            // Draw floor
            for (int y = wallEnd + 1; y < getHeight(); y++) {
                graphics.drawStyledChar(x, y, floorChar, floorColor, null);
            }
        }
    }

    /**
     * Result of a raycast operation.
     */
    private static class RaycastResult {
        final double distance;
        final boolean isVerticalWall;

        RaycastResult(double distance, boolean isVerticalWall) {
            this.distance = distance;
            this.isVerticalWall = isVerticalWall;
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
                break;
            }

            // Check if ray has hit a wall
            if (map[mapY].charAt(mapX) == '#') {
                hit = true;
            }
        }

        // Calculate distance
        double perpWallDist;
        if (!side) {
            perpWallDist = (mapX - playerX + (1 - stepX) / 2) / rayDirX;
        } else {
            perpWallDist = (mapY - playerY + (1 - stepY) / 2) / rayDirY;
        }

        return new RaycastResult(Math.abs(perpWallDist), !side);
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
     * Check if a position is valid (not a wall).
     * Made public for testing purposes.
     */
    public boolean isValidPosition(double x, double y) {
        if (map == null || map.length == 0) return false;

        int mapX = (int) Math.floor(x);
        int mapY = (int) Math.floor(y);

        // Check bounds
        if (mapX < 0 || mapX >= map[0].length() || mapY < 0 || mapY >= map.length) {
            return false;
        }

        // Check if the position is in a wall
        return map[mapY].charAt(mapX) != '#';
    }

    /**
     * Set a new map for the raycasting world.
     */
    public void setMap(String[] map) {
        this.map = map != null ? map : DEFAULT_MAP;

        // Reset player position to a safe location if current position is invalid
        if (!isValidPosition(playerX, playerY)) {
            // Find first empty space
            for (int y = 0; y < this.map.length; y++) {
                for (int x = 0; x < this.map[y].length(); x++) {
                    if (this.map[y].charAt(x) == ' ') {
                        playerX = x + 0.5;
                        playerY = y + 0.5;
                        return;
                    }
                }
            }
        }
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
     * Get the current map.
     */
    public String[] getMap() {
        return map.clone();
    }
}
