package view;

import model.core.Game;
import view.render.FieldParameters;

import javax.swing.SwingUtilities;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public final class SwingTestSupport {
    private SwingTestSupport() {
    }

    public static void runOnEdt(Runnable action) {
        try {
            if (SwingUtilities.isEventDispatchThread()) {
                action.run();
                return;
            }
            SwingUtilities.invokeAndWait(action);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T callOnEdt(Supplier<T> action) {
        AtomicReference<T> result = new AtomicReference<>();
        runOnEdt(() -> result.set(action.get()));
        return result.get();
    }

    public static <T> T readField(Object target, String fieldName, Class<T> fieldType) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return fieldType.cast(field.get(target));
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    public static BufferedImage createCanvas(int width, int height) {
        return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    }

    public static Graphics2D createGraphics(BufferedImage image) {
        Graphics2D graphics = image.createGraphics();
        graphics.setClip(0, 0, image.getWidth(), image.getHeight());
        return graphics;
    }

    public static Point toScreenPoint(FieldParameters parameters, Game game, double modelX, double modelY, int width, int height) {
        int padding = parameters.fieldPadding();
        int drawableWidth = width - padding * 2;
        int drawableHeight = height - padding * 2;
        double x = padding + modelX * drawableWidth / game.currentLevel().gameField().width();
        double y = padding + modelY * drawableHeight / game.currentLevel().gameField().height();
        return new Point((int) Math.round(x), (int) Math.round(y));
    }
}
