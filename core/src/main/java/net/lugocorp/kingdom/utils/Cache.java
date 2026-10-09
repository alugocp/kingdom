package net.lugocorp.kingdom.utils;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * This class works like a Map but with automatic fallback for grabbing values
 */
public class Cache<K, V> {
    private final Map<K, V> values = new HashMap<>();

    /**
     * Returns the value cached to the given key, or calculates a new value and
     * updates the cache if the given key is not found
     */
    public V get(K k, Supplier<V> fallback) {
        if (this.values.containsKey(k)) {
            return this.values.get(k);
        }
        final V v = fallback.get();
        this.values.put(k, v);
        return v;
    }
}
