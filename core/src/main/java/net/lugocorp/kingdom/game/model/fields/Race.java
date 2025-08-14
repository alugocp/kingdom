package net.lugocorp.kingdom.game.model.fields;
import java.util.Optional;

/**
 * Represents the species of a given Unit
 */
public class Race {
    public static final Race UNKNOWN = new Race("???");
    private final Optional<Race> root;
    private final String label;

    public Race(String label, Race root) {
        this.root = Optional.of(root);
        this.label = label;
    }

    public Race(String label) {
        this.root = Optional.empty();
        this.label = label;
    }

    @Override
    public String toString() {
        return this.root.isPresent() ? String.format("%s / %s", this.root.get().toString(), this.label) : this.label;
    }

    /** {@inheritdoc} */
    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof Race) {
            Race r = (Race) o;
            return r.toString().equals(this.toString());
        }
        return false;
    }
}
