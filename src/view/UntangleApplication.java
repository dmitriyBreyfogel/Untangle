package view;

import model.Game;

import javax.swing.SwingUtilities;

public final class UntangleApplication {
    private UntangleApplication() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GameWindow(new Game()).showWindow());
    }
}
