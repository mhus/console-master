package com.consolemaster;

/**
 * Demo zur Demonstration der schönen UTF-8 Box-Drawing-Rahmen.
 * Zeigt verschiedene Border-Stile und Farben.
 */
public class BeautifulBorderDemo {

    public static void main(String[] args) {
        try {
            // Create main screen
            ScreenCanvas screen = new ScreenCanvas(80, 25);

            // Create main composite with some spacing
            CompositeCanvas main = new CompositeCanvas(0, 0, 80, 25);

            // Standard UTF-8 Border
            Text text1 = new Text(0, 0, 16, 3, "Standard UTF-8\nBorder with\nbeautiful lines");
            text1.setForegroundColor(AnsiColor.BRIGHT_CYAN);
            Box box1 = new Box(2, 2, 20, 5, new DefaultBorder());
            box1.setChild(text1);

            // Colored Border
            Text text2 = new Text(0, 0, 16, 3, "Colored Border\nin bright green\nwith style");
            text2.setForegroundColor(AnsiColor.WHITE);
            DefaultBorder greenBorder = new DefaultBorder(AnsiColor.BRIGHT_GREEN);
            Box box2 = new Box(25, 2, 20, 5, greenBorder);
            box2.setChild(text2);

            // Bold Border
            Text text3 = new Text(0, 0, 16, 3, "Bold Border\nwith formatting\nand emphasis");
            text3.setForegroundColor(AnsiColor.BRIGHT_YELLOW);
            DefaultBorder boldBorder = new DefaultBorder(AnsiColor.BRIGHT_BLUE, AnsiFormat.BOLD);
            Box box3 = new Box(48, 2, 20, 5, boldBorder);
            box3.setChild(text3);

            // Nested Boxes
            Text innerText = new Text(0, 0, 12, 2, "Inner Box\nNested!");
            innerText.setForegroundColor(AnsiColor.BRIGHT_MAGENTA);
            DefaultBorder innerBorder = new DefaultBorder(AnsiColor.CYAN);
            Box innerBox = new Box(1, 1, 16, 4, innerBorder);
            innerBox.setChild(innerText);

            Text outerText = new Text(0, 0, 20, 1, "Outer Box Container");
            outerText.setForegroundColor(AnsiColor.WHITE);
            CompositeCanvas outerContent = new CompositeCanvas(0, 0, 20, 7);
            outerContent.addChild(outerText);
            outerContent.addChild(innerBox);

            DefaultBorder outerBorder = new DefaultBorder(AnsiColor.BRIGHT_RED, AnsiFormat.BOLD);
            Box outerBox = new Box(2, 10, 24, 9, outerBorder);
            outerBox.setChild(outerContent);

            // Multiple styles showcase
            Text styleText = new Text(0, 0, 20, 5,
                "UTF-8 Box Drawing:\n" +
                "┌─ Top-left\n" +
                "│  Vertical lines\n" +
                "─  Horizontal lines\n" +
                "└─ Bottom corners");
            styleText.setForegroundColor(AnsiColor.BRIGHT_WHITE);
            DefaultBorder styleBorder = new DefaultBorder(AnsiColor.YELLOW, AnsiFormat.DIM);
            Box styleBox = new Box(30, 10, 24, 7, styleBorder);
            styleBox.setChild(styleText);

            // Add all boxes to main
            main.addChild(box1);
            main.addChild(box2);
            main.addChild(box3);
            main.addChild(outerBox);
            main.addChild(styleBox);

            // Title
            Text title = new Text(0, 0, 80, 1, "Beautiful UTF-8 Box-Drawing Borders Demo - Press any key to exit");
            title.setForegroundColor(AnsiColor.BRIGHT_WHITE);
            title.setFormats(AnsiFormat.BOLD);
            main.addChild(title);

            screen.setContentCanvas(main);

            // Simple render and wait for input
            screen.render();
            System.in.read();

        } catch (Exception e) {
            System.err.println("Demo failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
