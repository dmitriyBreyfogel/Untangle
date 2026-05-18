import model.Game;
import view.GameWindow;

import javax.swing.SwingUtilities;

public final class UntangleApplication {
    private UntangleApplication() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GameWindow(new Game()).showWindow());
    }
}
