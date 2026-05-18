package view;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FieldParametersTest {
    @Test
    @DisplayName("Параметры поля хранят радиус узла и отступ поля")
    void storesValues() {
        FieldParameters parameters = new FieldParameters(12, 28);

        assertEquals(12, parameters.nodeRadius());
        assertEquals(28, parameters.fieldPadding());
    }

    @Test
    @DisplayName("Параметры поля отклоняют некорректные значения")
    void rejectsInvalidValues() {
        assertThrows(IllegalArgumentException.class, () -> new FieldParameters(0, 10));
        assertThrows(IllegalArgumentException.class, () -> new FieldParameters(-1, 10));
        assertThrows(IllegalArgumentException.class, () -> new FieldParameters(10, -1));
    }
}
