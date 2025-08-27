package net.lugocorp.kingdom.game.glyph;
import java.util.Optional;

/**
 * Handles access to a Unit's Glyphs
 */
public class UnitGlyphs {
    private Optional<Glyph> g2 = Optional.empty();
    private Glyph g1 = Glyph.BATTLE;

    // No one needs to instantiate this class apart from its host Unit
    public UnitGlyphs() {
        this.setDefault();
    }

    /**
     * Sets a single Glyph on this Unit
     */
    public void set(Glyph g) {
        this.g2 = Optional.empty();
        this.g1 = g;
    }

    /**
     * Sets two Glyphs on this Unit
     */
    public void set(Glyph g1, Glyph g2) {
        this.g2 = Optional.of(g2);
        this.g1 = g1;
    }

    /**
     * Sets the default Glyphs for this Unit
     */
    public void setDefault() {
        this.set(Glyph.BATTLE);
    }

    /**
     * Returns true if this Unit has the given Glyph
     */
    public boolean has(Glyph g) {
        return this.g1 == g || this.g2.map((Glyph g2) -> g2 == g).orElse(false);
    }

    /**
     * Retrieves the Glyphs associated with this Unit
     */
    public Glyph[] get() {
        if (this.g2.isPresent()) {
            return new Glyph[]{this.g1, this.g2.get()};
        }
        return new Glyph[]{this.g1};
    }
}
