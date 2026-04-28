package view;

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
    @DisplayName("Control panel updates button availability")
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
    @DisplayName("Control panel runs configured actions")
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
    @DisplayName("Control panel configures layout and buttons")
    void configuresLayoutAndButtons() {
        GameControlPanel panel = SwingTestSupport.callOnEdt(GameControlPanel::new);
        JButton startButton = SwingTestSupport.readField(panel, "startGameButton", JButton.class);
        JButton restartButton = SwingTestSupport.readField(panel, "restartLevelButton", JButton.class);
        JButton finishButton = SwingTestSupport.readField(panel, "finishGameButton", JButton.class);

        assertTrue(panel.getLayout() instanceof FlowLayout);
        assertEquals(3, panel.getComponentCount());
        assertEquals("Начать игру", startButton.getText());
        assertEquals("Перезапустить уровень", restartButton.getText());
        assertEquals("Завершить игру", finishButton.getText());
    }

    @Test
    @DisplayName("Control panel ignores click when start action is missing")
    void ignoresClickWhenStartActionIsMissing() {
        GameControlPanel panel = SwingTestSupport.callOnEdt(GameControlPanel::new);
        JButton startButton = SwingTestSupport.readField(panel, "startGameButton", JButton.class);

        assertDoesNotThrow(() -> SwingTestSupport.runOnEdt(startButton::doClick));
    }

    @Test
    @DisplayName("Control panel runs only start action for start button")
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
    @DisplayName("Control panel runs only restart action for restart button")
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
    @DisplayName("Control panel runs only finish action for finish button")
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
    @DisplayName("Control panel can toggle button availability back and forth")
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
