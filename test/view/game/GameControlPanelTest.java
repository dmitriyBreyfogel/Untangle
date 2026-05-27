package view.game;

import view.SwingTestSupport;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.swing.JButton;
import java.awt.FlowLayout;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameControlPanelTest {
    @Test
    @DisplayName("Панель управления обновляет доступность кнопок")
    void updatesButtonAvailability() {
        GameControlPanel panel = SwingTestSupport.callOnEdt(GameControlPanel::new);

        JButton startButton = SwingTestSupport.readField(panel, "startGameButton", JButton.class);
        JButton restartButton = SwingTestSupport.readField(panel, "restartLevelButton", JButton.class);
        JButton finishButton = SwingTestSupport.readField(panel, "finishGameButton", JButton.class);

        assertTrue(startButton.isEnabled());
        assertFalse(restartButton.isEnabled());
        assertFalse(finishButton.isEnabled());

        SwingTestSupport.runOnEdt(() -> panel.updateButtonAvailability(true));

        assertFalse(startButton.isEnabled());
        assertTrue(restartButton.isEnabled());
        assertTrue(finishButton.isEnabled());
    }

    @Test
    @DisplayName("Панель управления включает кнопку продолжения при наличии прогресса")
    void enablesContinueButtonWhenProgressExists() {
        GameControlPanel panel = SwingTestSupport.callOnEdt(GameControlPanel::new);
        JButton restartButton = SwingTestSupport.readField(panel, "restartLevelButton", JButton.class);

        SwingTestSupport.runOnEdt(() -> panel.updateButtonAvailability(false, true, 2));

        assertTrue(restartButton.isEnabled());
        assertEquals("Продолжить с уровня 2", restartButton.getText());
    }

    @Test
    @DisplayName("Панель управления выполняет настроенные действия")
    void runsConfiguredActions() {
        GameControlPanel panel = SwingTestSupport.callOnEdt(GameControlPanel::new);
        JButton startButton = SwingTestSupport.readField(panel, "startGameButton", JButton.class);
        JButton restartButton = SwingTestSupport.readField(panel, "restartLevelButton", JButton.class);
        JButton finishButton = SwingTestSupport.readField(panel, "finishGameButton", JButton.class);

        AtomicInteger counter = new AtomicInteger();
        SwingTestSupport.runOnEdt(() -> {
            panel.setStartGameAction(counter::incrementAndGet);
            panel.setRestartLevelAction(counter::incrementAndGet);
            panel.setFinishGameAction(counter::incrementAndGet);
            startButton.doClick();
            panel.updateButtonAvailability(true);
            restartButton.doClick();
            finishButton.doClick();
        });

        assertEquals(3, counter.get());
    }

    @Test
    @DisplayName("Панель управления настраивает layout и кнопки")
    void configuresLayoutAndButtons() {
        GameControlPanel panel = SwingTestSupport.callOnEdt(GameControlPanel::new);
        JButton startButton = SwingTestSupport.readField(panel, "startGameButton", JButton.class);
        JButton restartButton = SwingTestSupport.readField(panel, "restartLevelButton", JButton.class);
        JButton finishButton = SwingTestSupport.readField(panel, "finishGameButton", JButton.class);

        assertTrue(panel.getLayout() instanceof FlowLayout);
        assertEquals(3, panel.getComponentCount());
        assertEquals("Новая игра", startButton.getText());
        assertEquals("Продолжить с уровня 1", restartButton.getText());
        assertEquals("Закончить партию", finishButton.getText());
    }

    @Test
    @DisplayName("Панель управления игнорирует клик без start action")
    void ignoresClickWhenStartActionIsMissing() {
        GameControlPanel panel = SwingTestSupport.callOnEdt(GameControlPanel::new);
        JButton startButton = SwingTestSupport.readField(panel, "startGameButton", JButton.class);

        assertDoesNotThrow(() -> SwingTestSupport.runOnEdt(startButton::doClick));
    }

    @Test
    @DisplayName("Панель управления выполняет только start action для кнопки старта")
    void runsOnlyStartActionForStartButton() {
        GameControlPanel panel = SwingTestSupport.callOnEdt(GameControlPanel::new);
        JButton startButton = SwingTestSupport.readField(panel, "startGameButton", JButton.class);
        AtomicInteger startCounter = new AtomicInteger();
        AtomicInteger restartCounter = new AtomicInteger();
        AtomicInteger finishCounter = new AtomicInteger();

        SwingTestSupport.runOnEdt(() -> {
            panel.setStartGameAction(startCounter::incrementAndGet);
            panel.setRestartLevelAction(restartCounter::incrementAndGet);
            panel.setFinishGameAction(finishCounter::incrementAndGet);
            startButton.doClick();
        });

        assertEquals(1, startCounter.get());
        assertEquals(0, restartCounter.get());
        assertEquals(0, finishCounter.get());
    }

    @Test
    @DisplayName("Панель управления выполняет только restart action для кнопки перезапуска")
    void runsOnlyRestartActionForRestartButton() {
        GameControlPanel panel = SwingTestSupport.callOnEdt(GameControlPanel::new);
        JButton restartButton = SwingTestSupport.readField(panel, "restartLevelButton", JButton.class);
        AtomicInteger startCounter = new AtomicInteger();
        AtomicInteger restartCounter = new AtomicInteger();
        AtomicInteger finishCounter = new AtomicInteger();

        SwingTestSupport.runOnEdt(() -> {
            panel.setStartGameAction(startCounter::incrementAndGet);
            panel.setRestartLevelAction(restartCounter::incrementAndGet);
            panel.setFinishGameAction(finishCounter::incrementAndGet);
            panel.updateButtonAvailability(true);
            restartButton.doClick();
        });

        assertEquals(0, startCounter.get());
        assertEquals(1, restartCounter.get());
        assertEquals(0, finishCounter.get());
    }

    @Test
    @DisplayName("Панель управления выполняет только finish action для кнопки завершения")
    void runsOnlyFinishActionForFinishButton() {
        GameControlPanel panel = SwingTestSupport.callOnEdt(GameControlPanel::new);
        JButton finishButton = SwingTestSupport.readField(panel, "finishGameButton", JButton.class);
        AtomicInteger startCounter = new AtomicInteger();
        AtomicInteger restartCounter = new AtomicInteger();
        AtomicInteger finishCounter = new AtomicInteger();

        SwingTestSupport.runOnEdt(() -> {
            panel.setStartGameAction(startCounter::incrementAndGet);
            panel.setRestartLevelAction(restartCounter::incrementAndGet);
            panel.setFinishGameAction(finishCounter::incrementAndGet);
            panel.updateButtonAvailability(true);
            finishButton.doClick();
        });

        assertEquals(0, startCounter.get());
        assertEquals(0, restartCounter.get());
        assertEquals(1, finishCounter.get());
    }

    @Test
    @DisplayName("Панель управления может переключать доступность кнопок туда и обратно")
    void togglesButtonAvailabilityBackAndForth() {
        GameControlPanel panel = SwingTestSupport.callOnEdt(GameControlPanel::new);
        JButton startButton = SwingTestSupport.readField(panel, "startGameButton", JButton.class);
        JButton restartButton = SwingTestSupport.readField(panel, "restartLevelButton", JButton.class);
        JButton finishButton = SwingTestSupport.readField(panel, "finishGameButton", JButton.class);

        SwingTestSupport.runOnEdt(() -> {
            panel.updateButtonAvailability(true);
            panel.updateButtonAvailability(false);
        });

        assertTrue(startButton.isEnabled());
        assertFalse(restartButton.isEnabled());
        assertFalse(finishButton.isEnabled());
    }
}
