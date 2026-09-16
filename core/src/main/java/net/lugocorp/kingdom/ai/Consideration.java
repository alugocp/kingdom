package net.lugocorp.kingdom.ai;
import java.util.Optional;

/**
 * Tracks multiple options and only keeps the highest Priority option
 */
public class Consideration<T> {
    private Optional<Prioritized<T>> state = Optional.empty();

    /**
     * Returns the value currently associated with this instance
     */
    public Optional<T> getValue() {
        return this.state.map((Prioritized<T> s) -> s.value);
    }

    /**
     * Returns the state currently associated with this instance
     */
    public Optional<Prioritized<T>> getState() {
        return this.state;
    }

    /**
     * Inputs another state value to possibly record
     */
    public void consider(Priority p, T t) {
        if (this.state.map((Prioritized<T> s) -> p.value > s.priority.value
                || (p.value == s.priority.value && Math.random() < 0.3)).orElse(true)) {
            this.state = Optional.of(new Prioritized<T>(p, t));
        }
    }
}
