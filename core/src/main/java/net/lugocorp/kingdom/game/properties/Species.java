package net.lugocorp.kingdom.game.properties;
import java.util.Optional;

/**
 * Represents the species of a given Unit
 */
public class Species {
    public static final Species UNKNOWN = new Species("???");
    private final Optional<Species> root;
    private final String label;

    public Species(String label, Species root) {
        this.root = Optional.of(root);
        this.label = label;
    }

    public Species(String label) {
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
        if (o != null && o instanceof Species) {
            Species s = (Species) o;
            return s.toString().equals(this.toString());
        }
        return false;
    }
}
