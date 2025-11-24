package net.lugocorp.kingdom.game.properties;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Stores a set of tags associated with an item
 */
public class Tags implements Iterable<String> {
    private final Set<String> labels = new HashSet<>();
    private boolean all = false;

    /**
     * Adds a new tag to this instance
     */
    public void add(String label) {
        if (!this.all) {
            this.labels.add(label);
        }
    }

    /**
     * Removes the given tag from this instance
     */
    public void remove(String label) {
        if (!this.all) {
            this.labels.remove(label);
        }
    }

    /**
     * Returns true if this instance has the given tag
     */
    public boolean has(String s) {
        return this.all || this.labels.contains(s);
    }

    /**
     * Returns true if this instance and the given instance have any tags in common
     */
    public boolean intersects(Tags t) {
        if (this.all || t.all) {
            return true;
        }
        for (String s : this.labels) {
            if (t.labels.contains(s)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Sets this instance to encompass all tags
     */
    public void acceptAll() {
        this.labels.clear();
        this.all = true;
    }

    /**
     * Returns a nice string representing the tags in this instance (t1, t2, ...,
     * <conjunction> tn <suffix>)
     */
    public final String pretty(String conjunction, String suffix) {
        final StringBuilder sb = new StringBuilder();
        final int n = this.labels.size();
        int a = 0;
        if (this.all) {
            return String.format("any %s", suffix);
        }
        if (n == 0) {
            return String.format("tagless %s", suffix);
        }
        for (String l : this.labels) {
            if (a == 0) {
                sb.append(l);
            } else if (a == n - 1) {
                sb.append(String.format(" %s %s", conjunction, l, suffix));
            } else {
                sb.append(String.format(", %s", l));
            }
            if (a == n - 1) {
                sb.append(String.format(" %s", suffix));
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
