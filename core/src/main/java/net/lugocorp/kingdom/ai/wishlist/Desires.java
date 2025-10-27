package net.lugocorp.kingdom.ai.wishlist;
import net.lugocorp.kingdom.utils.Tuple;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

/**
 * Models how much the AI Player wants the given value
 */
public class Desires<T> implements Iterable<Tuple<T, Integer>> {
    private final Set<Tuple<T, Integer>> desires = new HashSet<>();

    /**
     * Adds another option to this instance
     */
    public void add(Tuple<T, Integer> tuple) {
        this.desires.add(tuple);
    }

    /**
     * Returns the most wanted option
     */
    public Optional<T> getMostWanted() {
        Optional<Tuple<T, Integer>> most = Optional.empty();
        for (Tuple<T, Integer> t : this.desires) {
            if (t.b > most.map((Tuple<T, Integer> t1) -> t1.b).orElse(-1)) {
                most = Optional.of(t);
            }
        }
        return most.map((Tuple<T, Integer> t) -> t.a);
    }

    /**
     * Returns an Iterator for this instance's options
     */
    @Override
    public Iterator<Tuple<T, Integer>> iterator() {
        return this.desires.iterator();
    }
}
