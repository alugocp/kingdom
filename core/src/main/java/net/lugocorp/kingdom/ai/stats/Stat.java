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

    /**
     * Adds a new value to the data
     */
    public void record(int value) {
        this.data.add(value);
        if (this.data.size() > Stat.WINDOW_SIZE) {
            this.data.remove(0);
        }
    }

    /**
     * Returns the average value across the data
     */
    public int getAverage() {
        return Lambda.fold((Integer acc, Integer x) -> acc + x, 0, this.data) / this.data.size();
    }
}
