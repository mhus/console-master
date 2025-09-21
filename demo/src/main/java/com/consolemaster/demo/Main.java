package com.consolemaster.demo;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Main class for the demo module that provides a console-based selection
 * interface to run different demo applications.
 */
public class Main {

    private static final Map<Integer, DemoEntry> DEMOS = new LinkedHashMap<>();

    static {
        // Core framework demos
        DEMOS.put(1, new DemoEntry("Console Master Demo", "Basic framework demonstration",
            () -> ConsoleMasterDemo.main(new String[0])));
        DEMOS.put(2, new DemoEntry("Graphics Demo", "Graphics system showcase",
            () -> GraphicsDemo.main(new String[0])));
        DEMOS.put(3, new DemoEntry("Text Demo", "Text component features demonstration",
            () -> TextDemo.main(new String[0])));
        DEMOS.put(4, new DemoEntry("Process Loop Demo", "Interactive ProcessLoop system demonstration",
            () -> ProcessLoopDemo.main(new String[0])));

        // Layout demos
        DEMOS.put(5, new DemoEntry("Layout Demo", "FlowLayout demonstration with modern components",
            () -> LayoutDemo.main(new String[0])));
        DEMOS.put(6, new DemoEntry("Border Layout Demo", "BorderLayout with 5 regions",
            () -> BorderLayoutDemo.main(new String[0])));
        DEMOS.put(7, new DemoEntry("Box Demo", "Border and Box component showcase",
            () -> BoxDemo.main(new String[0])));
        DEMOS.put(8, new DemoEntry("Scroller Demo", "Scrollable content demonstration",
            () -> {
                try {
                    ScrollerDemo.main(new String[0]);
                } catch (IOException e) {
                    System.err.println("Error running Scroller Demo: " + e.getMessage());
                }
            }));

        // Border and styling demos
        DEMOS.put(9, new DemoEntry("Beautiful Border Demo", "Advanced border styling showcase",
            () -> BeautifulBorderDemo.main(new String[0])));
        DEMOS.put(10, new DemoEntry("Border Style Demo", "Different border styles demonstration",
            () -> BorderStyleDemo.main(new String[0])));

        // Input and interaction demos
        DEMOS.put(11, new DemoEntry("Focus Demo", "Focus management system demonstration",
            () -> FocusDemo.main(new String[0])));
        DEMOS.put(12, new DemoEntry("Mouse Demo", "Mouse event handling demonstration",
            () -> MouseDemo.main(new String[0])));
        DEMOS.put(13, new DemoEntry("Console Input Demo", "Console input handling demonstration",
            () -> ConsoleInputDemo.main(new String[0])));

        // Graphics and advanced features
        DEMOS.put(14, new DemoEntry("Clipping Graphics Demo", "Graphics clipping demonstration",
            () -> ClippingGraphicsDemo.main(new String[0])));
        DEMOS.put(15, new DemoEntry("Native Terminal Demo", "Native terminal features demonstration",
            () -> NativeTerminalDemo.main(new String[0])));

        // Animation demos
        DEMOS.put(16, new DemoEntry("Animation Demo", "Animation system demonstration",
            () -> {
                try {
                    AnimationDemo.main(new String[0]);
                } catch (IOException e) {
                    System.err.println("Error running Animation Demo: " + e.getMessage());
                }
            }));
        DEMOS.put(17, new DemoEntry("Animation Throttle Demo", "Animation throttling demonstration",
            () -> {
                try {
                    AnimationThrottleDemo.main(new String[0]);
                } catch (IOException e) {
                    System.err.println("Error running Animation Throttle Demo: " + e.getMessage());
                }
            }));

        // 3D Graphics demos
        DEMOS.put(18, new DemoEntry("Graphic 3D Demo", "3D graphics system demonstration",
            () -> Graphic3DDemo.main(new String[0])));
        DEMOS.put(19, new DemoEntry("Test 3D Demo", "3D testing demonstration",
            () -> Test3DDemo.main(new String[0])));
        DEMOS.put(20, new DemoEntry("Terrain 3D Demo", "3D terrain demonstration",
            () -> Terrain3DDemo.main(new String[0])));

        // Raycasting demos
        DEMOS.put(21, new DemoEntry("Raycasting Demo", "Interactive first-person 3D raycasting demonstration",
            () -> RaycastingDemo.main(new String[0])));
        DEMOS.put(22, new DemoEntry("Raytracing vs Traditional Demo", "Comparison of rendering techniques",
            () -> RaytracingVsTraditionalDemo.main(new String[0])));

        // Advanced demos
        DEMOS.put(23, new DemoEntry("Advanced 8-Direction Demo", "Advanced directional movement demonstration",
            () -> Advanced8DirectionDemo.main(new String[0])));
        DEMOS.put(24, new DemoEntry("Background Demo", "Background rendering demonstration",
            () -> {
                try {
                    BackgroundDemo.main(new String[0]);
                } catch (IOException e) {
                    System.err.println("Error running Background Demo: " + e.getMessage());
                }
            }));
        DEMOS.put(25, new DemoEntry("Grid Overlay Demo", "Grid overlay demonstration",
            () -> GridOverlayDemo.main(new String[0])));
        DEMOS.put(26, new DemoEntry("Object Sprite Demo", "Object sprite demonstration",
            () -> ObjectSpriteDemo.main(new String[0])));
        DEMOS.put(27, new DemoEntry("Output Capture Demo", "Output capture system demonstration",
            () -> {
                try {
                    OutputCaptureDemo.main(new String[0]);
                } catch (IOException e) {
                    System.err.println("Error running Output Capture Demo: " + e.getMessage());
                }
            }));
        DEMOS.put(28, new DemoEntry("Logger Demo", "Logger system demonstration",
            () -> LoggerDemo.main(new String[0])));
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        printMenu();

        System.out.print("Wähle eine Demo (Zahl eingeben) oder 0 zum Beenden: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());

            if (choice == 0) {
                System.out.println("Auf Wiedersehen!");
                return;
            }

            DemoEntry demo = DEMOS.get(choice);
            if (demo == null) {
                System.out.println("Ungültige Auswahl. Bitte wähle eine Zahl zwischen 0 und " + DEMOS.size() + ".");
                return;
            }

            System.out.println("\n=== Starte " + demo.name + " ===");
            System.out.println(demo.description);
            System.out.println();

            try {
                demo.runnable.run();
            } catch (Exception e) {
                System.err.println("Fehler beim Ausführen der Demo: " + e.getMessage());
            }

            System.out.println("\n=== Demo beendet ===");

        } catch (NumberFormatException e) {
            System.out.println("Bitte gib eine gültige Zahl ein.");
        }

        scanner.close();
    }

    private static void printMenu() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("                    CONSOLE MASTER - DEMO AUSWAHL");
        System.out.println("=".repeat(80));
        System.out.println();

        // Group demos by category
        System.out.println("🎯 KERN-FRAMEWORK DEMOS:");
        printDemoRange(1, 4);

        System.out.println("\n📐 LAYOUT DEMOS:");
        printDemoRange(5, 8);

        System.out.println("\n🎨 STIL UND RAHMEN DEMOS:");
        printDemoRange(9, 10);

        System.out.println("\n⌨️  EINGABE UND INTERAKTION DEMOS:");
        printDemoRange(11, 13);

        System.out.println("\n🖼️  GRAFIK UND ERWEITERTE FUNKTIONEN:");
        printDemoRange(14, 15);

        System.out.println("\n🎬 ANIMATION DEMOS:");
        printDemoRange(16, 17);

        System.out.println("\n🎮 3D GRAFIK DEMOS:");
        printDemoRange(18, 20);

        System.out.println("\n🏗️  RAYCASTING DEMOS:");
        printDemoRange(21, 22);

        System.out.println("\n🚀 ERWEITERTE DEMOS:");
        printDemoRange(23, 28);

        System.out.println("\n0. Beenden");
        System.out.println("\n" + "=".repeat(80));
    }

    private static void printDemoRange(int start, int end) {
        for (int i = start; i <= end; i++) {
            DemoEntry demo = DEMOS.get(i);
            if (demo != null) {
                System.out.printf("%2d. %-30s - %s%n", i, demo.name, demo.description);
            }
        }
    }

    /**
     * Represents a demo entry with name, description and runnable.
     */
    private static class DemoEntry {
        final String name;
        final String description;
        final Runnable runnable;

        DemoEntry(String name, String description, Runnable runnable) {
            this.name = name;
            this.description = description;
            this.runnable = runnable;
        }
    }
}
