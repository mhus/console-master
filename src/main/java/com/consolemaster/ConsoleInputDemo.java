package com.consolemaster;

import java.io.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Demo application that reads raw console input non-blocking and displays
 * character codes, ASCII characters, special keys, and mouse events.
 * Shows a 10-second timer message in the main thread.
 * Exits when Ctrl+Q is pressed.
 */
public class ConsoleInputDemo {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static volatile boolean running = true;
    private static final BlockingQueue<String> outputQueue = new LinkedBlockingQueue<>();
    private static int inputCounter = 0;

    public static void main(String[] args) {
        System.out.println("=== Console Input Demo gestartet ===");
        System.out.println("Drücke beliebige Tasten, bewege die Maus oder verwende Sondertasten.");
        System.out.println("Ctrl+Q zum Beenden.");
        System.out.println("================================================");
        System.out.flush();

        // Enable raw mode to capture all input
        enableRawMode();

        // Start input reading thread
        Thread inputThread = new Thread(ConsoleInputDemo::readRawInput);
        inputThread.setDaemon(true);
        inputThread.start();

        // Start output processing thread
        Thread outputThread = new Thread(ConsoleInputDemo::processOutput);
        outputThread.setDaemon(true);
        outputThread.start();

        // Main timer loop
        int secondsCounter = 0;
        while (running) {
            try {
                Thread.sleep(1000);
                secondsCounter++;

                // Every 10 seconds, show special message
                if (secondsCounter % 10 == 0) {
                    // Get terminal dimensions and cursor position
                    TerminalInfo terminalInfo = getTerminalInfo();

                    addOutput(String.format("*** 10-Sekunden-Meldung: %s - %d Sekunden gelaufen ***",
                            LocalTime.now().format(TIME_FORMAT), secondsCounter));
                    addOutput(String.format("    Terminal: %d Columns x %d Lines",
                            terminalInfo.columns, terminalInfo.lines));
                    addOutput(String.format("    Cursor Position: Column %d, Line %d",
                            terminalInfo.cursorColumn, terminalInfo.cursorLine));
                }

            } catch (InterruptedException e) {
                break;
            }
        }

        // Restore normal terminal mode
        disableRawMode();
        System.out.println("\nConsole Input Demo beendet.");
    }

    /**
     * Reads raw input from System.in in a separate thread.
     */
    private static void readRawInput() {
        try (InputStream in = System.in) {
            byte[] buffer = new byte[1024];

            while (running) {
                if (in.available() > 0) {
                    int bytesRead = in.read(buffer);
                    if (bytesRead > 0) {
                        analyzeRawInput(buffer, bytesRead);
                    }
                } else {
                    // Short sleep to prevent busy waiting
                    Thread.sleep(10);
                }
            }
        } catch (IOException | InterruptedException e) {
            if (running) {
                addOutput("Fehler beim Lesen der Eingabe: " + e.getMessage());
            }
        }
    }

    /**
     * Analyzes raw input bytes and creates formatted output.
     */
    private static void analyzeRawInput(byte[] buffer, int length) {
        inputCounter++;
        StringBuilder analysis = new StringBuilder();
        analysis.append(String.format("[%03d] ", inputCounter));

        if (length == 1) {
            // Single byte - likely a character
            byte b = buffer[0];
            int code = b & 0xFF;
            char ch = (char) code;

            // Check for Ctrl+Q (ASCII 17)
            if (code == 17) {
                addOutput("Ctrl+Q erkannt - Beende Anwendung...");
                running = false;
                return;
            }

            analysis.append("Einzelzeichen: ");
            if (isPrintable(ch)) {
                analysis.append("'").append(ch).append("'");
            } else {
                analysis.append("(nicht druckbar)");
            }
            analysis.append(" | ASCII: ").append(code);
            analysis.append(" | Hex: 0x").append(String.format("%02X", code));

            String description = getCharacterDescription(ch);
            if (description != null) {
                analysis.append(" | ").append(description);
            }

        } else {
            // Multiple bytes - likely escape sequence or special key
            analysis.append("Byte-Sequenz (").append(length).append(" Bytes): ");
            for (int i = 0; i < length; i++) {
                if (i > 0) analysis.append(" ");
                analysis.append(String.format("0x%02X", buffer[i] & 0xFF));
            }

            // Try to interpret as escape sequence
            String interpretation = interpretEscapeSequence(buffer, length);
            if (interpretation != null) {
                analysis.append(" | ").append(interpretation);
            }
        }

        addOutput(analysis.toString());
    }

    /**
     * Interprets escape sequences and special key combinations.
     */
    private static String interpretEscapeSequence(byte[] buffer, int length) {
        if (length < 2) return null;

        // ESC sequences start with 0x1B (27)
        if ((buffer[0] & 0xFF) == 27) {
            if (length == 2) {
                char second = (char) (buffer[1] & 0xFF);
                return "ESC + '" + second + "' (Alt+" + second + ")";
            } else if (length >= 3 && (buffer[1] & 0xFF) == '[') {
                // ANSI escape sequence
                return interpretAnsiSequence(buffer, length);
            }
        }

        // Control characters
        if (length == 1) {
            int code = buffer[0] & 0xFF;
            if (code < 32) {
                return "Ctrl+" + (char) ('A' + code - 1);
            }
        }

        return "Unbekannte Sequenz";
    }

    /**
     * Interprets ANSI escape sequences.
     */
    private static String interpretAnsiSequence(byte[] buffer, int length) {
        if (length < 3) return "Unvollständige ANSI-Sequenz";

        StringBuilder seq = new StringBuilder();
        for (int i = 2; i < length; i++) {
            seq.append((char) (buffer[i] & 0xFF));
        }

        String sequence = seq.toString();

        // Common ANSI sequences
        return switch (sequence) {
            case "A" -> "Pfeil HOCH";
            case "B" -> "Pfeil RUNTER";
            case "C" -> "Pfeil RECHTS";
            case "D" -> "Pfeil LINKS";
            case "H" -> "POS1 (Home)";
            case "F" -> "ENDE (End)";
            case "1~" -> "POS1";
            case "2~" -> "EINFG (Insert)";
            case "3~" -> "ENTF (Delete)";
            case "4~" -> "ENDE";
            case "5~" -> "BILD HOCH (Page Up)";
            case "6~" -> "BILD RUNTER (Page Down)";
            case "11~" -> "F1";
            case "12~" -> "F2";
            case "13~" -> "F3";
            case "14~" -> "F4";
            case "15~" -> "F5";
            case "17~" -> "F6";
            case "18~" -> "F7";
            case "19~" -> "F8";
            case "20~" -> "F9";
            case "21~" -> "F10";
            case "23~" -> "F11";
            case "24~" -> "F12";
            default -> {
                if (sequence.contains("M")) {
                    yield "Maus-Event: " + sequence;
                } else {
                    yield "ANSI-Sequenz: ESC[" + sequence;
                }
            }
        };
    }

    /**
     * Checks if a character is printable.
     */
    private static boolean isPrintable(char ch) {
        return ch >= 32 && ch <= 126;
    }

    /**
     * Returns a description for special characters.
     */
    private static String getCharacterDescription(char ch) {
        return switch (ch) {
            case 9 -> "TAB";
            case 10 -> "LINE FEED (LF)";
            case 13 -> "CARRIAGE RETURN (CR)";
            case ' ' -> "LEERZEICHEN";
            case 0 -> "NULL";
            case 7 -> "BELL";
            case 8 -> "BACKSPACE";
            case 27 -> "ESCAPE";
            case 127 -> "DELETE";
            default -> {
                if (ch < 32) {
                    yield "CTRL+" + (char) ('A' + ch - 1);
                }
                yield null;
            }
        };
    }

    /**
     * Adds output to the queue for processing.
     */
    private static void addOutput(String message) {
        try {
            outputQueue.put(String.format("[%s] %s",
                    LocalTime.now().format(TIME_FORMAT), message));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Processes output queue and displays messages.
     */
    private static void processOutput() {
        while (running) {
            try {
                String message = outputQueue.take();
                System.out.print(message);
                System.out.print("\n\r");
                System.out.flush();
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    /**
     * Enables raw mode for terminal input (Unix/Linux/macOS).
     */
    private static void enableRawMode() {
        try {
            // Try to enable raw mode using stty
            ProcessBuilder pb = new ProcessBuilder("/bin/sh", "-c", "stty raw -echo < /dev/tty");
            Process process = pb.start();
            process.waitFor();

            // Enable mouse reporting
            System.out.print("\033[?1000h\033[?1002h\033[?1015h\033[?1006h");
            System.out.flush();

        } catch (Exception e) {
            addOutput("Warnung: Raw-Modus konnte nicht aktiviert werden: " + e.getMessage());
        }
    }

    /**
     * Disables raw mode and restores normal terminal behavior.
     */
    private static void disableRawMode() {
        try {
            // Disable mouse reporting
            System.out.print("\033[?1006l\033[?1015l\033[?1002l\033[?1000l");
            System.out.flush();

            // Restore normal terminal mode
            ProcessBuilder pb = new ProcessBuilder("/bin/sh", "-c", "stty cooked echo < /dev/tty");
            Process process = pb.start();
            process.waitFor();

        } catch (Exception e) {
            System.err.println("Warnung: Normaler Terminal-Modus konnte nicht wiederhergestellt werden: " + e.getMessage());
        }
    }

    /**
     * Retrieves terminal dimensions and cursor position.
     */
    private static TerminalInfo getTerminalInfo() {
        TerminalInfo info = new TerminalInfo();
        try {
            // Get terminal size
            ProcessBuilder pb = new ProcessBuilder("/bin/sh", "-c", "stty size < /dev/tty");
            Process process = pb.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line = reader.readLine();
                if (line != null) {
                    String[] parts = line.trim().split(" ");
                    if (parts.length == 2) {
                        info.lines = Integer.parseInt(parts[0]);
                        info.columns = Integer.parseInt(parts[1]);
                    }
                }
            }
            process.waitFor();

            // Get cursor position (requires mouse reporting enabled)
            System.out.print("\033[6n");
            System.out.flush();
            Thread.sleep(50); // Wait for response
            byte[] buffer = new byte[32];
            if (System.in.available() > 0) {
                int bytesRead = System.in.read(buffer);
                if (bytesRead > 0) {
                    String response = new String(buffer, 0, bytesRead);
                    String[] parts = response.split(";");
                    if (parts.length == 2) {
                        info.cursorLine = Integer.parseInt(parts[0].substring(2));
                        info.cursorColumn = Integer.parseInt(parts[1]);
                    }
                }
            }

        } catch (Exception e) {
            addOutput("Warnung: Terminalinformationen konnten nicht abgerufen werden: " + e.getMessage());
        }
        return info;
    }

    /**
     * Container for terminal information.
     */
    private static class TerminalInfo {
        int lines = 0;
        int columns = 0;
        int cursorLine = 0;
        int cursorColumn = 0;
    }
}
