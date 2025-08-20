package net.lugocorp.kingdom.ai.stats;

/**
 * Records the changes in a single rolling stat used in CompPlayer decision
 * making
 */
public class DiffStat extends Stat {
    private int previous;

    public DiffStat(int initial) {
        this.previous = initial;
    }

    /**
     * Adds a new difference value to the data
     */
    @Override
    public void record(int value) {
        final int diff = value - this.previous;
        this.previous = value;
        super.record(diff);
    }
}
