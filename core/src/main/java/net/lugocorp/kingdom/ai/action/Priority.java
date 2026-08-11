package net.lugocorp.kingdom.ai.action;

/**
 * Models all the levels of priority for computer players' possible actions
 */
public enum Priority {
    FATAL(0, "Fatal"), BAD_IDEA(1, "Bad Idea"), NEUTRAL(2, "Neutral"), GOOD_IDEA(3, "Good Idea"), OPTIMAL(4,
            "Optimal"), NECESSITY(5, "Necessity");

    private final String label;
    public final int value;

    private Priority(int value, String label) {
        this.label = label;
        this.value = value;
    }

    /** {@inheritdoc} */
    @Override
    public String toString() {
        return String.format("%s (%d)", this.label, this.value);
    }
}
