package com.consolemaster;

/**
 * Demo zur Demonstration der verschiedenen BorderStyle-Optionen.
 * Zeigt alle verfügbaren Border-Stile mit UTF-8 Box-Drawing-Zeichen.
 */
public class BorderStyleDemo {

    public static void main(String[] args) {
        try {
            // Create main screen
            ScreenCanvas screen = new ScreenCanvas("BorderDemo", 80, 30);

            // Create main composite with spacing
            Composite main = new Composite("MainComposite", 0, 0, 80, 30);

            // Title
            Text title = new Text("Title", 80, 1, "BorderStyle Demo - Beautiful UTF-8 Box-Drawing Characters");
            title.setForegroundColor(AnsiColor.BRIGHT_WHITE);
            title.setFormats(AnsiFormat.BOLD);
            main.addChild(title);

            // Row 1: Basic styles
            createBorderExample(main, 2, 3, "SINGLE\n(Default)", BorderStyle.SINGLE, AnsiColor.WHITE);
            createBorderExample(main, 20, 3, "DOUBLE\n(Elegant)", BorderStyle.DOUBLE, AnsiColor.BRIGHT_CYAN);
            createBorderExample(main, 38, 3, "THICK\n(Heavy)", BorderStyle.THICK, AnsiColor.BRIGHT_GREEN);
            createBorderExample(main, 56, 3, "ROUNDED\n(Soft)", BorderStyle.ROUNDED, AnsiColor.BRIGHT_YELLOW);

            // Row 2: Special styles
            createBorderExample(main, 2, 12, "ASCII\n(Compatible)", BorderStyle.ASCII, AnsiColor.BRIGHT_RED);
            createBorderExample(main, 20, 12, "DOTTED\n(Subtle)", BorderStyle.DOTTED, AnsiColor.BRIGHT_MAGENTA);

            // Large demonstration box with double border
            Text demoText = new Text("DemoText", 30, 6,
                "Large Demonstration Box\n\n" +
                "This shows how the DOUBLE\n" +
                "border style looks with\n" +
                "multiple lines of content\n" +
                "and professional styling.");
            demoText.setForegroundColor(AnsiColor.BRIGHT_WHITE);

            DefaultBorder demoBorder = new DefaultBorder(BorderStyle.DOUBLE, AnsiColor.BRIGHT_BLUE, AnsiFormat.BOLD);
            Box demoBox = new Box("DemoBox", 34, 8, demoBorder);
            demoBox.setChild(demoText);
            main.addChild(demoBox);

            // Character showcase
            Text charInfo = new Text("CharInfo", 76, 6,
                "UTF-8 Box-Drawing Characters used in BorderStyles:\n" +
                "SINGLE: ─│┌┐└┘    DOUBLE: ═║╔╗╚╝    THICK: ━┃┏┓┗┛    ROUNDED: ─│╭╮╰╯\n" +
                "ASCII:  -|++++    DOTTED: ┈┊┌┐└┘\n\n" +
                "Usage: new SimpleBorder(BorderStyle.DOUBLE, AnsiColor.CYAN)\n" +
                "Press any key to exit...");
            charInfo.setForegroundColor(AnsiColor.BRIGHT_BLACK);

            DefaultBorder infoBorder = new DefaultBorder(BorderStyle.SINGLE, AnsiColor.BRIGHT_BLACK);
            Box infoBox = new Box("InfoBox", 76, 6, infoBorder);
            infoBox.setChild(charInfo);
            main.addChild(infoBox);

            screen.setContent(main);

            // Render and wait for input
            screen.render();
            System.in.read();

        } catch (Exception e) {
            System.err.println("Demo failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Creates a small example box with the specified border style.
     */
    private static void createBorderExample(Composite parent, int x, int y, String label, BorderStyle style, AnsiColor color) {
        Text text = new Text("ExampleText_" + style.name(), 14, 2, label);
        text.setForegroundColor(color);
        text.setAlignment(Text.Alignment.CENTER);

        DefaultBorder border = new DefaultBorder(style, color);
        Box box = new Box("ExampleBox_" + style.name(), 16, 4, border);
        box.setChild(text);

        parent.addChild(box);
    }
}
