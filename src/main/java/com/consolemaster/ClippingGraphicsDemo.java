package com.consolemaster;

/**
 * Demo zur Demonstration des neuen ClippingGraphics-Systems.
 * Zeigt, wie Canvas-Komponenten jetzt bei (0,0) zeichnen können
 * und automatisch an die richtige Position übersetzt werden.
 */
public class ClippingGraphicsDemo {

    public static void main(String[] args) {
        // Create a simple demo canvas that draws starting at (0,0)
        Canvas demoCanvas = new Canvas(5, 3, 20, 8) {
            @Override
            public void paint(Graphics graphics) {
                // Zeichne immer bei (0,0) - wird automatisch übersetzt
                graphics.drawString(0, 0, "ClippingGraphics Demo");
                graphics.drawRect(0, 1, getWidth(), getHeight() - 1, '*');
                graphics.drawString(2, 3, "Local (0,0)!");
                graphics.drawString(2, 4, "Auto-translated");
                graphics.setForegroundColor(AnsiColor.BRIGHT_GREEN);
                graphics.drawStyledString(2, 6, "Styled Text", AnsiColor.BRIGHT_GREEN, null, AnsiFormat.BOLD);
            }
        };

        // Create a box with border that contains text
        Text innerText = new Text(0, 0, 15, 3, "Box Content\nLine 2\nLine 3");
        innerText.setForegroundColor(AnsiColor.CYAN);

        Box box = new Box(25, 5, 20, 8, new DefaultBorder());
        box.setChild(innerText);

        // Create a composite that contains both
        CompositeCanvas main = new CompositeCanvas(0, 0, 60, 20);
        main.addChild(demoCanvas);
        main.addChild(box);

        // Create screen and display
        ScreenCanvas screen = new ScreenCanvas(60, 20);
        screen.setContentCanvas(main);

        // Simple rendering to demonstrate the system
        System.out.println("=== ClippingGraphics Demo ===");
        System.out.println("Demonstrating local coordinate system where each Canvas draws starting at (0,0)");
        System.out.println("The ClippingGraphics automatically translates to the correct screen position.");
        System.out.println();

        // For a more complete demo, you would use ProcessLoop, but this shows the concept
        System.out.println("Demo created successfully!");
        System.out.println("- DemoCanvas draws at local (0,0) but appears at screen position (5,3)");
        System.out.println("- Box content draws at local (0,0) but appears within the box border");
        System.out.println("- All coordinate translation happens automatically through ClippingGraphics");
    }
}
