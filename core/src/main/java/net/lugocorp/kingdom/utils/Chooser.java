package net.lugocorp.kingdom.utils;
import java.util.function.BiFunction;

/**
 * Allows us to pick a value based on some criteria
 */
public class Chooser<T> {
    private final BiFunction<Integer, Integer, Boolean> criteria;
    private T value = null;
    private int score = 0;

    public Chooser(BiFunction<Integer, Integer, Boolean> criteria) {
        this.criteria = criteria;
    }

    /**
     * Returns true if we've accepted an option yet
     */
    public boolean has() {
        return this.value != null;
    }

    /**
     * Returns the chosen value
     */
    public T get() {
        return this.value;
    }

    /**
     * Returns the score associated wth the chosen value
     */
    public int getScore() {
        return this.score;
    }

    /**
     * Makes a new comparison to calculate the best value
     */
    public Chooser<T> choice(T value, int score) {
        if (this.value == null || this.criteria.apply(this.score, score)) {
            this.score = score;
            this.value = value;
        }
        return this;
    }
}
