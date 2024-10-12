package net.lugocorp.kingdom.game.pools;
import net.lugocorp.kingdom.game.Game;

/**
 * Represents a pool of random options that can be retrieved from and replaced
 * back into the Pool
 */
public interface Pool<T> {

    /**
     * Runs whatever logic is needed to initialize the Pool
     */
    public void init(Game g);

    /**
     * Returns how many options remain in the Pool
     */
    public int size();

    /**
     * Retrieves a random element from the Pool
     */
    public T retrieve();

    /**
     * Replaces a given element in the Pool
     */
    public void replace(T e);
}
