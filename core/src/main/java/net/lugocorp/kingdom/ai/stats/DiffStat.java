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
     * Commits a new difference value to the data
     */
    @Override
    public void commit() {
        final int diff = this.current - this.previous;
        this.previous = this.current;
        this.current = diff;
        super.commit();
    }
}
