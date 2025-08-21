package net.lugocorp.kingdom.ai.stats;
import net.lugocorp.kingdom.utils.code.Lambda;
import java.util.ArrayList;
import java.util.List;

/**
 * Records a single rolling stat to be used in CompPlayer decision making
 */
public class Stat {
    private static final int WINDOW_SIZE = 50;
    private final List<Integer> data = new ArrayList<>();
    protected int current = 0;

    /**
     * Modifies the current value
     */
    public void add(int value) {
        this.current += value;
    }

    /**
     * Commits the current value to the data
     */
    public void commit() {
        this.data.add(this.current);
        if (this.data.size() > Stat.WINDOW_SIZE) {
            this.data.remove(0);
        }
        this.current = 0;
    }

    /**
     * Returns the average value across the data (as an int)
     */
    public int getAverage() {
        return Lambda.fold((Integer acc, Integer x) -> acc + x, 0, this.data) / this.data.size();
    }

    /**
     * Returns the average value across the data (as a float)
     */
    public float getMean() {
        return Lambda.fold((Float acc, Integer x) -> acc + (float) x, 0f, this.data) / (float) this.data.size();
    }
}
