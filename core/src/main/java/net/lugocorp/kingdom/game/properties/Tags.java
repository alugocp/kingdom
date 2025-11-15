package net.lugocorp.kingdom.game.properties;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Stores a set of tags associated with an item
 */
public class Tags implements Iterable<String> {
    private final Set<String> labels = new HashSet<>();

    /**
     * Adds a new tag to this instance
     */
    public void add(String label) {
        this.labels.add(label);
    }

    /**
     * Returns true if this instance has the given tag
     */
    public boolean has(String s) {
        return this.labels.contains(s);
    }

    /**
     * Returns true if this instance and the given instance have any tags in common
     */
    public boolean intersects(Tags t) {
        for (String s : this.labels) {
            if (t.labels.contains(s)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a nice string representing the tags in this instance (t1, t2, ...,
     * <conjunction> tn <suffix>)
     */
    public final String pretty(String conjunction, String suffix) {
        final StringBuilder sb = new StringBuilder();
        final int n = this.labels.size();
        int a = 0;
        if (n == 0) {
            return String.format("tagless %s", suffix);
        }
        for (String l : this.labels) {
            if (a == 0) {
                sb.append(l);
            } else if (a == n - 1) {
                sb.append(String.format(" %s %s %s", conjunction, l, suffix));
            } else {
                sb.append(String.format(", %s", l));
            }
            a++;
        }
        return sb.toString();
    }

    /**
     * Returns an Iterator for this instance's tags
     */
    @Override
    public Iterator<String> iterator() {
        return this.labels.iterator();
    }
}
