package com.consolemaster;

import java.io.InputStream;
import java.io.PrintStream;

public class NativeTerminal extends Terminal {

    public NativeTerminal() {
        this(System.out, System.in);
    }

    public NativeTerminal(PrintStream writer, InputStream inputStream) {
        super(writer, inputStream);
    }
}
