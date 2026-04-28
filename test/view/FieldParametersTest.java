package view;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FieldParametersTest {
    @Test
    @DisplayName("Field parameters store node radius and field padding")
    void storesValues() {
        FieldParameters parameters = new FieldParameters(12, 28);

        assertEquals(12, parameters.nodeRadius());
        assertEquals(28, parameters.fieldPadding());
    }

    @Test
    @DisplayName("Field parameters reject invalid values")
    void rejectsInvalidValues() {
        assertThrows(IllegalArgumentException.class, () -> new FieldParameters(0, 10));
        assertThrows(IllegalArgumentException.class, () -> new FieldParameters(-1, 10));
        assertThrows(IllegalArgumentException.class, () -> new FieldParameters(10, -1));
    }
}
