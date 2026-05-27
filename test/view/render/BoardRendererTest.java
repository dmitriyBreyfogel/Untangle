package view.render;

import view.SwingTestSupport;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class BoardRendererTest {
    @Test
    @DisplayName("Рендерер доски рисует фон и игровую поверхность")
    void drawsBackdropAndBoardSurface() {
        FieldParameters parameters = new FieldParameters(12, 28);
        BoardRenderer renderer = new BoardRenderer();
        BufferedImage image = SwingTestSupport.createCanvas(240, 240);
        Graphics2D graphics = SwingTestSupport.createGraphics(image);
        try {
            renderer.draw(graphics, new Rectangle(0, 0, 240, 240), parameters);
        } finally {
            graphics.dispose();
        }

        int backdropPixel = image.getRGB(5, 5);
        int boardPixel = image.getRGB(60, 60);

        assertNotEquals(0, backdropPixel);
        assertNotEquals(0, boardPixel);
        assertNotEquals(backdropPixel, boardPixel);
    }

    @Test
    @DisplayName("Рендерер доски восстанавливает область отсечения")
    void restoresClipAfterDrawingTexture() {
        FieldParameters parameters = new FieldParameters(12, 28);
        BoardRenderer renderer = new BoardRenderer();
        BufferedImage image = SwingTestSupport.createCanvas(240, 240);
        Graphics2D graphics = SwingTestSupport.createGraphics(image);
        Shape previousClip = graphics.getClip();
        try {
            renderer.draw(graphics, new Rectangle(0, 0, 240, 240), parameters);
            assertEquals(previousClip.getBounds(), graphics.getClip().getBounds());
        } finally {
            graphics.dispose();
        }
    }

    @Test
    @DisplayName("Рендерер доски работает с малой областью")
    void drawsTinyBounds() {
        BoardRenderer renderer = new BoardRenderer();
        BufferedImage image = SwingTestSupport.createCanvas(8, 8);
        Graphics2D graphics = SwingTestSupport.createGraphics(image);
        try {
            assertDoesNotThrow(() -> renderer.draw(graphics, new Rectangle(0, 0, 8, 8), new FieldParameters(12, 28)));
        } finally {
            graphics.dispose();
        }
    }
}
