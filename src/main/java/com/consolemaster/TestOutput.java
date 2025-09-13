package com.consolemaster;

import java.io.IOException;

public class TestOutput {

    public static void main(String[] args) throws IOException {

        System.out.println("This is a test output.");
        System.out.println("Testing ANSI colors:");
        for (AnsiColor color : AnsiColor.values()) {
            System.out.println(color.getForeground() + color.name() + AnsiColor.RESET.getForeground());
        }
//
//        ScreenCanvas screen = new ScreenCanvas(40, 10);
//        Text text = new Text(0, 0, 0, 1, "Hello, ConsoleMaster!", Text.Alignment.CENTER);
//        text.setForegroundColor(AnsiColor.BRIGHT_GREEN);
//        text.setBold(true);
//        screen.setContentCanvas(text);
//        screen.render();

        NativeTerminal terminal = new NativeTerminal();
        terminal.writeStyled("Hello, World in Green Bold!\n", AnsiColor.BRIGHT_GREEN, null, AnsiFormat.BOLD);
        terminal.writeStyled("This is a test of ConsoleMaster.\n", AnsiColor.BRIGHT_CYAN, null);
        terminal.writeStyled("Goodbye!\n", AnsiColor.BRIGHT_RED, null, AnsiFormat.ITALIC);
    }
}
