package view.menu;

import view.SwingTestSupport;
import view.game.GameWindow;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.swing.JButton;
import java.awt.Component;
import java.awt.Container;
import java.awt.GraphicsEnvironment;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StartMenuWindowTest {
    private StartMenuWindow window;

    @AfterEach
    void tearDown() {
        SwingTestSupport.runOnEdt(() -> {
            if (window != null) {
                window.dispose();
            }
            for (Window openedWindow : Window.getWindows()) {
                if (openedWindow instanceof GameWindow) {
                    openedWindow.dispose();
                }
            }
        });
    }

    @Test
    @DisplayName("Стартовое окно настраивает основные свойства")
    void configuresWindow() {
        requireWindowEnvironment();

        window = SwingTestSupport.callOnEdt(StartMenuWindow::new);

        assertEquals("Untangle", window.getTitle());
        assertEquals(StartMenuWindow.EXIT_ON_CLOSE, window.getDefaultCloseOperation());
        assertFalse(window.isResizable());
    }

    @Test
    @DisplayName("Стартовое окно создаёт три кнопки меню")
    void createsMenuButtons() {
        requireWindowEnvironment();

        window = SwingTestSupport.callOnEdt(StartMenuWindow::new);

        List<JButton> buttons = buttonsIn(window.getContentPane());

        assertEquals(3, buttons.size());
        assertEquals(List.of("Новая игра", "Выбрать уровень", "Выйти"), buttons.stream().map(JButton::getText).toList());
    }

    @Test
    @DisplayName("Стартовое окно становится видимым")
    void showWindowMakesFrameVisible() {
        requireWindowEnvironment();

        window = SwingTestSupport.callOnEdt(StartMenuWindow::new);

        SwingTestSupport.runOnEdt(window::showWindow);

        assertTrue(window.isVisible());
    }

    @Test
    @DisplayName("Стартовое окно не принимает пустой запускатель игр")
    void rejectsNullGameSessionLauncher() {
        requireWindowEnvironment();

        assertThrows(NullPointerException.class, () -> new StartMenuWindow(null));
    }

    @Test
    @DisplayName("Кнопка новой игры открывает игровое окно")
    void newGameButtonOpensGameWindow() {
        requireWindowEnvironment();

        window = SwingTestSupport.callOnEdt(StartMenuWindow::new);
        JButton newGameButton = buttonsIn(window.getContentPane()).getFirst();

        SwingTestSupport.runOnEdt(newGameButton::doClick);

        assertFalse(window.isDisplayable());
        assertTrue(gameWindowIsOpened());
    }

    private static void requireWindowEnvironment() {
        Assumptions.assumeFalse(GraphicsEnvironment.isHeadless());
    }

    private static boolean gameWindowIsOpened() {
        for (Window openedWindow : Window.getWindows()) {
            if (openedWindow instanceof GameWindow && openedWindow.isDisplayable()) {
                return true;
            }
        }
        return false;
    }

    private static List<JButton> buttonsIn(Container container) {
        List<JButton> buttons = new ArrayList<>();
        collectButtons(container, buttons);
        return buttons;
    }

    private static void collectButtons(Container container, List<JButton> buttons) {
        for (Component component : container.getComponents()) {
            if (component instanceof JButton button) {
                buttons.add(button);
            }
            if (component instanceof Container childContainer) {
                collectButtons(childContainer, buttons);
            }
        }
    }
}
