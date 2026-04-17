package net.lugocorp.kingdom.game.properties;
import java.util.Optional;

/**
 * Represents the species of a given Unit
 */
public class Species {
    public static final Species UNKNOWN = new Species("???", 0x616161);
    private final Optional<Species> root;
    private final String label;
    public final int color;

    public Species(String label, int color, Species root) {
        this.root = Optional.of(root);
        this.label = label;
        this.color = color;
    }

    // This is for Kryo purposes only
    public Species() {
        this.root = Optional.empty();
        this.label = "";
        this.color = 0;
    }

    public Species(String label, int color) {
        this.root = Optional.empty();
        this.label = label;
        this.color = color;
    }

    /**
     * Returns true if this Species counts as the given Species
     */
    public boolean counts(Species s) {
        return this.equals(s) || this.root.map((Species r) -> r.counts(s)).orElse(false);
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
