package com.consolemaster;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Text canvas class.
 */
class TextTest {

    private Text textCanvas;

    @BeforeEach
    void setUp() {
        textCanvas = new Text(10, 20, 30, 5, "Hello World");
    }

    @Test
    void shouldCreateTextCanvasWithCorrectProperties() {
        assertEquals(10, textCanvas.getX());
        assertEquals(20, textCanvas.getY());
        assertEquals(30, textCanvas.getWidth());
        assertEquals(5, textCanvas.getHeight());
        assertEquals("Hello World", textCanvas.getText());
        assertEquals(Text.Alignment.LEFT, textCanvas.getAlignment());
        assertTrue(textCanvas.isWordWrap());
    }

    @Test
    void shouldCreateTextCanvasWithAlignment() {
        Text centeredText = new Text(0, 0, 20, 3, "Centered", Text.Alignment.CENTER);

        assertEquals("Centered", centeredText.getText());
        assertEquals(Text.Alignment.CENTER, centeredText.getAlignment());
    }

    @Test
    void shouldSetAndGetTextContent() {
        textCanvas.setText("New Text Content");
        assertEquals("New Text Content", textCanvas.getText());
    }

    @Test
    void shouldSetAlignment() {
        textCanvas.setAlignment(Text.Alignment.RIGHT);
        assertEquals(Text.Alignment.RIGHT, textCanvas.getAlignment());

        textCanvas.setAlignment(Text.Alignment.CENTER);
        assertEquals(Text.Alignment.CENTER, textCanvas.getAlignment());
    }

    @Test
    void shouldSetWordWrap() {
        textCanvas.setWordWrap(false);
        assertFalse(textCanvas.isWordWrap());

        textCanvas.setWordWrap(true);
        assertTrue(textCanvas.isWordWrap());
    }

    @Test
    void shouldSetColors() {
        textCanvas.setForegroundColor(AnsiColor.RED);
        assertEquals(AnsiColor.RED, textCanvas.getForegroundColor());

        textCanvas.setBackgroundColor(AnsiColor.BLUE);
        assertEquals(AnsiColor.BLUE, textCanvas.getBackgroundColor());
    }

    @Test
    void shouldSetFormats() {
        textCanvas.setFormats(AnsiFormat.BOLD, AnsiFormat.UNDERLINE);
        assertArrayEquals(new AnsiFormat[]{AnsiFormat.BOLD, AnsiFormat.UNDERLINE}, textCanvas.getFormats());
    }

    @Test
    void shouldSetBoldFormatting() {
        textCanvas.setBold(true);
        assertArrayEquals(new AnsiFormat[]{AnsiFormat.BOLD}, textCanvas.getFormats());

        textCanvas.setBold(false);
        assertArrayEquals(new AnsiFormat[0], textCanvas.getFormats());
    }

    @Test
    void shouldSetItalicFormatting() {
        textCanvas.setItalic(true);
        assertArrayEquals(new AnsiFormat[]{AnsiFormat.ITALIC}, textCanvas.getFormats());

        textCanvas.setItalic(false);
        assertArrayEquals(new AnsiFormat[0], textCanvas.getFormats());
    }

    @Test
    void shouldSetUnderlineFormatting() {
        textCanvas.setUnderline(true);
        assertArrayEquals(new AnsiFormat[]{AnsiFormat.UNDERLINE}, textCanvas.getFormats());

        textCanvas.setUnderline(false);
        assertArrayEquals(new AnsiFormat[0], textCanvas.getFormats());
    }

    @Test
    void shouldHandleNullText() {
        Text nullTextCanvas = new Text(0, 0, 10, 5, null);
        assertEquals("", nullTextCanvas.getText());
    }

    @Test
    void shouldSetLineBreak() {
        textCanvas.setLineBreak("\r\n");
        assertEquals("\r\n", textCanvas.getLineBreak());
    }

    @Test
    void shouldHandleEmptyText() {
        textCanvas.setText("");
        assertEquals("", textCanvas.getText());
    }

    @Test
    void shouldMaintainVisibility() {
        assertTrue(textCanvas.isVisible());

        textCanvas.setVisible(false);
        assertFalse(textCanvas.isVisible());
    }
}
