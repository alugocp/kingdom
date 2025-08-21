package net.lugocorp.kingdom.game.model.fields;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Keeps a collection of mappings between labels and integer values
 */
public class Tags {
    private static final String DELIMITER = ":";
    private static final int DEFAULT = 1;
    private final Map<String, Integer> tags = new HashMap<>();

    /**
     * Returns true if there is a registered tag with the given label
     */
    public boolean has(String label) {
        return this.tags.keySet().contains(label);
    }

    /**
     * Adds the given label as a tag
     */
    public Tags add(String label) {
        if (!this.has(label)) {
            this.set(label, Tags.DEFAULT);
        }
        return this;
    }

    /**
     * Sets the value for the given label
     */
    public void set(String label, int value) {
        this.tags.put(label, value);
    }

    /**
     * Returns the value for the given label, if it is registered
     */
    public Optional<Integer> get(String label) {
        if (this.has(label)) {
            return Optional.of(this.tags.get(label));
        }
        return Optional.empty();
    }

    /**
     * Returns all tags with the given prefix in the label. The returned tags do not
     * contain the prefix.
     */
    public Map<String, Integer> getWithPrefix(String prefix) {
        Map<String, Integer> subset = new HashMap<>();
        for (Map.Entry<String, Integer> e : this.tags.entrySet()) {
            final String qualified = String.format("%s:", prefix);
            if (e.getKey().indexOf(qualified) == 0) {
                subset.put(e.getKey().substring(qualified.length()), e.getValue());
            }
        }
        return subset;
    }
}
