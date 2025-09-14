package com.consolemaster;

import java.io.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Standalone Console Input Demo that demonstrates raw console input analysis
 * using only Java SDK features. Shows character codes, ASCII values, special keys,
 * and escape sequences. Runs a 10-second timer in the main thread and exits on Ctrl+Q.
 */
public class RawConsoleInputDemo {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static volatile boolean running = true;
    private static final BlockingQueue<String> outputQueue = new LinkedBlockingQueue<>();
    private static int inputCounter = 0;

    public static void main(String[] args) {
        System.out.println("=== Raw Console Input Demo ===");
        System.out.println("Zeigt wie Console Input non-blocking gelesen und analysiert werden kann");
        System.out.println("Drücke beliebige Tasten, verwende Sondertasten oder bewege die Maus");
        System.out.println("Ctrl+Q zum Beenden");
        System.out.println("=".repeat(60));
        System.out.flush();

        // Aktiviere Raw-Modus für vollständige Input-Erfassung
        enableRawMode();

        // Starte Input-Reading Thread
        Thread inputThread = new Thread(RawConsoleInputDemo::readRawInput, "InputReader");
        inputThread.setDaemon(true);
        inputThread.start();

        // Starte Output-Processing Thread
        Thread outputThread = new Thread(RawConsoleInputDemo::processOutput, "OutputProcessor");
        outputThread.setDaemon(true);
        outputThread.start();

        // Haupt-Timer Loop - alle 10 Sekunden eine Meldung
        int secondsCounter = 0;
        while (running) {
            try {
                Thread.sleep(1000);
                secondsCounter++;

                // Alle 10 Sekunden spezielle Meldung ausgeben
                if (secondsCounter % 10 == 0) {
                    addOutput(String.format("🕐 10-SEKUNDEN-MELDUNG: %s - Laufzeit: %d Sekunden - Eingaben: %d",
                            LocalTime.now().format(TIME_FORMAT), secondsCounter, inputCounter));
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        // Terminal-Modus wiederherstellen
        disableRawMode();
        System.out.println("\n" + "=".repeat(60));
        System.out.println("Raw Console Input Demo beendet.");
        System.out.println("Gesamte Eingaben analysiert: " + inputCounter);
    }

    /**
     * Liest rohe Eingaben von System.in in einem separaten Thread.
     */
    private static void readRawInput() {
        try (InputStream in = System.in) {
            byte[] buffer = new byte[256];

            while (running) {
                if (in.available() > 0) {
                    int bytesRead = in.read(buffer);
                    if (bytesRead > 0) {
                        analyzeRawInput(buffer, bytesRead);
                    }
                } else {
                    // Kurze Pause um Busy-Waiting zu vermeiden
                    Thread.sleep(10);
                }
            }
        } catch (IOException | InterruptedException e) {
            if (running) {
                addOutput("❌ Fehler beim Input-Reading: " + e.getMessage());
            }
        }
    }

    /**
     * Analysiert rohe Input-Bytes und erstellt formatierte Ausgabe.
     */
    private static void analyzeRawInput(byte[] buffer, int length) {
        inputCounter++;
        StringBuilder analysis = new StringBuilder();
        analysis.append(String.format("📝 [%04d] ", inputCounter));

        if (length == 1) {
            analyzeSingleByte(analysis, buffer[0]);
        } else {
            analyzeByteSequence(analysis, buffer, length);
        }

        addOutput(analysis.toString());
    }

    /**
     * Analysiert ein einzelnes Byte (normales Zeichen).
     */
    private static void analyzeSingleByte(StringBuilder analysis, byte b) {
        int code = b & 0xFF;
        char ch = (char) code;

        // Prüfe auf Ctrl+Q (ASCII 17) zum Beenden
        if (code == 17) {
            addOutput("🛑 Ctrl+Q erkannt - Beende Anwendung...");
            running = false;
            return;
        }

        analysis.append("Zeichen: ");

        // Zeige druckbares Zeichen oder Beschreibung
        if (isPrintable(ch)) {
            analysis.append("'").append(ch).append("'");
        } else {
            analysis.append("(nicht druckbar)");
        }

        analysis.append(String.format(" | ASCII: %3d | Hex: 0x%02X | Binär: %s",
                code, code, String.format("%8s", Integer.toBinaryString(code)).replace(' ', '0')));

        // Füge Beschreibung für Sonderzeichen hinzu
        String description = getCharacterDescription(code);
        if (description != null) {
            analysis.append(" | ").append(description);
        }
    }

    /**
     * Analysiert eine Byte-Sequenz (Escape-Sequenzen, Sondertasten).
     */
    private static void analyzeByteSequence(StringBuilder analysis, byte[] buffer, int length) {
        analysis.append(String.format("Sequenz (%d Bytes): ", length));

        // Zeige alle Bytes in Hex
        for (int i = 0; i < length; i++) {
            if (i > 0) analysis.append(" ");
            analysis.append(String.format("0x%02X", buffer[i] & 0xFF));
        }

        // Versuche Interpretation als Escape-Sequenz
        String interpretation = interpretSequence(buffer, length);
        if (interpretation != null) {
            analysis.append(" | ").append(interpretation);
        }

        // Zeige auch als String (wenn möglich)
        StringBuilder stringRepr = new StringBuilder();
        for (int i = 0; i < length; i++) {
            char c = (char) (buffer[i] & 0xFF);
            if (isPrintable(c)) {
                stringRepr.append(c);
            } else {
                stringRepr.append("·");
            }
        }
        if (stringRepr.length() > 0) {
            analysis.append(" | String: \"").append(stringRepr).append("\"");
        }
    }

    /**
     * Interpretiert Byte-Sequenzen als bekannte Escape-Sequenzen oder Sondertasten.
     */
    private static String interpretSequence(byte[] buffer, int length) {
        if (length < 1) return null;

        // ESC-Sequenzen beginnen mit 0x1B (27)
        if ((buffer[0] & 0xFF) == 27) {
            if (length == 1) {
                return "🔘 ESC-Taste";
            } else if (length == 2) {
                char second = (char) (buffer[1] & 0xFF);
                return "🔘 Alt+" + second + " (ESC + '" + second + "')";
            } else if (length >= 3 && (buffer[1] & 0xFF) == '[') {
                return interpretAnsiSequence(buffer, length);
            } else if (length >= 3 && (buffer[1] & 0xFF) == 'O') {
                return interpretFunctionKey(buffer, length);
            }
        }

        // Multi-Byte UTF-8 Zeichen
        if ((buffer[0] & 0xFF) >= 0xC0) {
            return "🌐 UTF-8 Zeichen";
        }

        // Unbekannte Sequenz
        return "❓ Unbekannte Sequenz";
    }

    /**
     * Interpretiert ANSI Escape-Sequenzen (ESC[...).
     */
    private static String interpretAnsiSequence(byte[] buffer, int length) {
        if (length < 3) return "🔘 Unvollständige ANSI-Sequenz";

        StringBuilder seq = new StringBuilder();
        for (int i = 2; i < length; i++) {
            seq.append((char) (buffer[i] & 0xFF));
        }

        String sequence = seq.toString();

        return switch (sequence) {
            case "A" -> "⬆️ Pfeil HOCH";
            case "B" -> "⬇️ Pfeil RUNTER";
            case "C" -> "➡️ Pfeil RECHTS";
            case "D" -> "⬅️ Pfeil LINKS";
            case "H" -> "🏠 HOME (Pos1)";
            case "F" -> "🔚 END (Ende)";
            case "1~" -> "🏠 HOME";
            case "2~" -> "📝 INSERT (Einfg)";
            case "3~" -> "🗑️ DELETE (Entf)";
            case "4~" -> "🔚 END";
            case "5~" -> "📄 PAGE UP (Bild↑)";
            case "6~" -> "📄 PAGE DOWN (Bild↓)";
            case "11~", "OP" -> "🔑 F1";
            case "12~", "OQ" -> "🔑 F2";
            case "13~", "OR" -> "🔑 F3";
            case "14~", "OS" -> "🔑 F4";
            case "15~" -> "🔑 F5";
            case "17~" -> "🔑 F6";
            case "18~" -> "🔑 F7";
            case "19~" -> "🔑 F8";
            case "20~" -> "🔑 F9";
            case "21~" -> "🔑 F10";
            case "23~" -> "🔑 F11";
            case "24~" -> "🔑 F12";
            default -> {
                if (sequence.contains("M") || sequence.matches(".*\\d+;\\d+M.*")) {
                    yield "🖱️ MAUS-EVENT: " + sequence;
                } else if (sequence.matches("\\d+;\\d+R")) {
                    yield "📍 CURSOR-POSITION: " + sequence;
                } else {
                    yield "🔘 ANSI ESC[" + sequence;
                }
            }
        };
    }

    /**
     * Interpretiert Funktionstasten-Sequenzen (ESC O...).
     */
    private static String interpretFunctionKey(byte[] buffer, int length) {
        if (length < 3) return "🔘 Unvollständige Funktionstasten-Sequenz";

        char key = (char) (buffer[2] & 0xFF);
        return switch (key) {
            case 'P' -> "🔑 F1 (ESC O P)";
            case 'Q' -> "🔑 F2 (ESC O Q)";
            case 'R' -> "🔑 F3 (ESC O R)";
            case 'S' -> "🔑 F4 (ESC O S)";
            default -> "🔑 Funktionstasten-Sequenz: ESC O " + key;
        };
    }

    /**
     * Prüft ob ein Zeichen druckbar ist.
     */
    private static boolean isPrintable(char ch) {
        return ch >= 32 && ch <= 126;
    }

    /**
     * Gibt eine Beschreibung für Sonderzeichen zurück.
     */
    private static String getCharacterDescription(int code) {
        return switch (code) {
            case 0 -> "NULL";
            case 7 -> "BELL (Klingelzeichen)";
            case 8 -> "BACKSPACE";
            case 9 -> "TAB (Tabulator)";
            case 10 -> "LINE FEED (Zeilenvorschub)";
            case 13 -> "CARRIAGE RETURN (Wagenrücklauf)";
            case 27 -> "ESCAPE";
            case 32 -> "SPACE (Leerzeichen)";
            case 127 -> "DELETE";
            default -> {
                if (code < 32) {
                    yield "CTRL+" + (char) ('A' + code - 1);
                }
                yield null;
            }
        };
    }

    /**
     * Fügt eine Nachricht zur Ausgabe-Queue hinzu.
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
     * Verarbeitet die Ausgabe-Queue und zeigt Nachrichten an.
     */
    private static void processOutput() {
        while (running) {
            try {
                String message = outputQueue.take();
                System.out.println(message);
                System.out.flush();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        // Leere verbleibende Queue beim Beenden
        String message;
        while ((message = outputQueue.poll()) != null) {
            System.out.println(message);
        }
    }

    /**
     * Aktiviert Raw-Modus für Terminal-Input (Unix/Linux/macOS).
     */
    private static void enableRawMode() {
        try {
            // Raw-Modus mit stty aktivieren
            ProcessBuilder pb = new ProcessBuilder("/bin/sh", "-c", "stty raw -echo < /dev/tty");
            Process process = pb.start();
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                // Maus-Reporting aktivieren
                System.out.print("\033[?1000h\033[?1002h\033[?1015h\033[?1006h");
                System.out.flush();
                addOutput("✅ Raw-Modus aktiviert - Vollständige Input-Erfassung verfügbar");
            } else {
                addOutput("⚠️ Raw-Modus konnte nicht aktiviert werden (Exit Code: " + exitCode + ")");
            }

        } catch (Exception e) {
            addOutput("⚠️ Raw-Modus Fehler: " + e.getMessage() + " - Eingeschränkte Input-Erfassung");
        }
    }

    /**
     * Deaktiviert Raw-Modus und stellt normales Terminal-Verhalten wieder her.
     */
    private static void disableRawMode() {
        try {
            // Maus-Reporting deaktivieren
            System.out.print("\033[?1006l\033[?1015l\033[?1002l\033[?1000l");
            System.out.flush();

            // Terminal-Modus wiederherstellen
            ProcessBuilder pb = new ProcessBuilder("/bin/sh", "-c", "stty cooked echo < /dev/tty");
            Process process = pb.start();
            process.waitFor();

        } catch (Exception e) {
            System.err.println("⚠️ Warnung: Terminal-Modus konnte nicht vollständig wiederhergestellt werden: " + e.getMessage());
        }
    }
}
