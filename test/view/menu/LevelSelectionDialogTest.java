package view.menu;

import view.SwingTestSupport;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.swing.JButton;
import java.awt.Component;
import java.awt.Container;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LevelSelectionDialogTest {
    private LevelSelectionDialog dialog;

    @AfterEach
    void tearDown() {
        if (dialog != null) {
            SwingTestSupport.runOnEdt(dialog::dispose);
        }
    }

    @Test
    @DisplayName("Окно выбора уровня создаёт кнопку для каждого уровня")
    void createsButtonForEveryLevel() {
        requireWindowEnvironment();

        dialog = SwingTestSupport.callOnEdt(() -> new LevelSelectionDialog(null, List.of(1, 2, 3)));

        List<JButton> buttons = buttonsIn(dialog.getContentPane());

        assertEquals(3, buttons.size());
        assertEquals(List.of("1", "2", "3"), buttons.stream().map(JButton::getText).toList());
    }

    @Test
    @DisplayName("Окно выбора уровня сохраняет выбранный уровень")
    void storesSelectedLevel() {
        requireWindowEnvironment();

        dialog = SwingTestSupport.callOnEdt(() -> new LevelSelectionDialog(null, List.of(1, 2, 3)));
        JButton secondLevelButton = buttonsIn(dialog.getContentPane()).get(1);

        SwingTestSupport.runOnEdt(secondLevelButton::doClick);

        assertEquals(2, SwingTestSupport.readField(dialog, "selectedLevelNumber", Integer.class));
        assertFalse(dialog.isDisplayable());
    }

    @Test
    @DisplayName("Окно выбора уровня не принимает пустой список уровней")
    void rejectsNullLevelNumbers() {
        requireWindowEnvironment();

        assertThrows(NullPointerException.class, () -> new LevelSelectionDialog(null, null));
    }

    private static void requireWindowEnvironment() {
        Assumptions.assumeFalse(GraphicsEnvironment.isHeadless());
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
