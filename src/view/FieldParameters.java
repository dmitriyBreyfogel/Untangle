package view;

public record FieldParameters(int nodeRadius, int fieldPadding) {
    public FieldParameters {
        if (nodeRadius <= 0) {
            throw new IllegalArgumentException("nodeRadius");
        }
        if (fieldPadding < 0) {
            throw new IllegalArgumentException("fieldPadding");
        }
    }
}
