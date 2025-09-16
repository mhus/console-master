package com.consolemaster.demo;

import com.consolemaster.AnsiColor;
import com.consolemaster.AnsiFormat;
import com.consolemaster.BorderStyle;
import com.consolemaster.Box;
import com.consolemaster.Composite;
import com.consolemaster.DefaultBorder;
import com.consolemaster.FlowLayout;
import com.consolemaster.ScreenCanvas;
import com.consolemaster.Text;

import java.io.IOException;

/**
 * Demo application showcasing the Box component with native styling.
 * Now uses native terminal implementation instead of JLine.
 */
public class BoxDemo {

    public static void main(String[] args) {
        try {
            // Create the main screen canvas
            ScreenCanvas screen = new ScreenCanvas(80, 25);

            // Create a composite canvas with FlowLayout for multiple boxes
            Composite content = new Composite("content",
                    screen.getWidth() - 10,
                                                         screen.getHeight() - 6,
                                                         new FlowLayout(3, 2));

            // Create boxes with different border styles and colors

            // Simple border box
            Box simpleBox = new Box("simpleBox", 20, 5, new DefaultBorder());
            Text simpleText = new Text("simpleText", 0, 0, "Simple Border", Text.Alignment.CENTER);
            simpleText.setForegroundColor(AnsiColor.WHITE);
            simpleBox.setContent(simpleText);
            content.addChild(simpleBox);

            // Thick border box
            Box thickBox = new Box("thickBox", 20, 5, new DefaultBorder(BorderStyle.THICK));
            Text thickText = new Text("thickText", 0, 0, "Thick Border", Text.Alignment.CENTER);
            thickText.setForegroundColor(AnsiColor.YELLOW);
            thickText.setFormats(AnsiFormat.BOLD);
            thickBox.setContent(thickText);
            content.addChild(thickBox);

            // Colored border box
            DefaultBorder coloredBorder = new DefaultBorder();
            coloredBorder.setBorderColor(AnsiColor.RED);
            coloredBorder.setBorderFormats(AnsiFormat.BOLD);
            Box coloredBox = new Box("coloredBox", 20, 5, coloredBorder);
            Text coloredText = new Text("coloredText", 0, 0, "Colored Border", Text.Alignment.CENTER);
            coloredText.setForegroundColor(AnsiColor.RED);
            coloredBox.setContent(coloredText);
            content.addChild(coloredBox);

            // Multi-line content box
            Box multiBox = new Box("multiBox", 25, 6, new DefaultBorder());
            Text multiText = new Text("multiText", 0, 0, "Multi-line\nContent\nBox", Text.Alignment.CENTER);
            multiText.setForegroundColor(AnsiColor.GREEN);
            multiBox.setContent(multiText);
            content.addChild(multiBox);

            screen.setContent(content);

            // Simple render and wait
            screen.render();
            System.out.println("\nBox Demo - Press Enter to continue...");
            System.in.read();

            screen.close();

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
