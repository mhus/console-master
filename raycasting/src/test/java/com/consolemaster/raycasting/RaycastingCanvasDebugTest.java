package com.consolemaster.raycasting;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Debug-Test für RaycastingCanvas Position-Probleme.
 */
class RaycastingCanvasDebugTest {

    @Test
    void debugPositionSetting() {
        String[] testMap = {
            "########",
            "#      #",
            "#  ##  #",
            "#      #",
            "########"
        };

        RaycastingCanvas canvas = new RaycastingCanvas("Debug", 80, 40, testMap);

        System.out.println("Initial position: (" + canvas.getPlayerX() + ", " + canvas.getPlayerY() + ")");

        // Teste Position (1, 1) - sollte gültig sein
        System.out.println("Testing position (1.5, 1.5)");
        canvas.setPlayerPosition(1.5, 1.5);
        System.out.println("After setting to (1.5, 1.5): (" + canvas.getPlayerX() + ", " + canvas.getPlayerY() + ")");

        // Teste was bei (1, 1) in der Map steht
        System.out.println("Map at [1][1]: '" + testMap[1].charAt(1) + "'");

        // Teste isValidPosition direkt
        System.out.println("Is (1.5, 1.5) valid: " + isValidPositionDebug(canvas, testMap, 1.5, 1.5));
        System.out.println("Is (2.0, 2.0) valid: " + isValidPositionDebug(canvas, testMap, 2.0, 2.0));
    }

    private boolean isValidPositionDebug(RaycastingCanvas canvas, String[] map, double x, double y) {
        if (map == null || map.length == 0) return false;

        int mapX = (int) Math.floor(x);
        int mapY = (int) Math.floor(y);

        System.out.println("Position (" + x + ", " + y + ") -> map indices [" + mapY + "][" + mapX + "]");

        // Check bounds
        if (mapX < 0 || mapX >= map[0].length() || mapY < 0 || mapY >= map.length) {
            System.out.println("Out of bounds!");
            return false;
        }

        char tile = map[mapY].charAt(mapX);
        System.out.println("Tile at [" + mapY + "][" + mapX + "]: '" + tile + "'");

        // Check if the position is in a wall
        return tile != '#';
    }
}
