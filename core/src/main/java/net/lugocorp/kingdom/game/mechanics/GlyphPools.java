package net.lugocorp.kingdom.game.mechanics;
import net.lugocorp.kingdom.game.Game;
import net.lugocorp.kingdom.game.glyph.Glyph;
import net.lugocorp.kingdom.game.glyph.GlyphCategory;
import net.lugocorp.kingdom.game.layers.DummyUnit;
import net.lugocorp.kingdom.game.model.Unit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class tracks all the Units that can be recruited by their Glyph(s)
 */
public class GlyphPools {
    private final Map<Glyph, List<String>> pools = new HashMap<>();

    /**
     * Initializes this object, ingests all registered Unit names in the Game and
     * their associated Glyphs
     */
    public void init(Game g) {
        for (Glyph glyph : Glyph.values()) {
            this.pools.put(glyph, new ArrayList<String>());
        }
        DummyUnit u = new DummyUnit();
        for (String name : g.events.unit.getStratifiers()) {
            // Set a couple of defaults on the Unit before running the generation event
            u.setNameOverride(name);
            u.glyphs.setDefault();

            // Run the generation event without instantiating a new Unit object,
            // then add it to the GlyphPool
            g.generator.unitOptimal(u);
            if (u.shouldAddToGlyphPool()) {
                this.add(name, u.glyphs.get());
            }
        }
    }

    /**
     * Returns the remaining Unit names in the pool of the given Glyph
     */
    public int remaining(Glyph g) {
        return this.pools.get(g).size();
    }

    /**
     * Calls into the other remaining() method for each Glyph in the given
     * GlyphCategory
     */
    public int remaining(GlyphCategory g) {
        int sum = 0;
        for (int a = 0; a < g.glyphs.length; a++) {
            sum += this.remaining(g.glyphs[a]);
        }
        return sum;
    }

    /**
     * Retrieves a random Unit name by the given Glyph
     */
    public String[] random(Glyph g, int n) {
        final int max = this.pools.get(g).size();
        if (n > max) {
            throw new RuntimeException("Pool size is lower than number of requested entries");
        }

        final int chunkSize = max / n;
        final int lastChunkSize = chunkSize + max - (chunkSize * n);
        String[] names = new String[n];
        for (int a = 0; a < n; a++) {
            int index = (chunkSize * a) + (int) (Math.random() * ((a == n - 1) ? lastChunkSize : chunkSize));
            names[a] = this.pools.get(g).get(index);
        }
        return names;
    }

    /**
     * Adds a name to the pools specified by the given Glyphs
     */
    private void add(String name, Glyph[] glyphs) {
        for (Glyph g : glyphs) {
            final List<String> pool = this.pools.get(g);
            if (!pool.contains(name)) {
                pool.add(name);
            }
        }
    }

    /**
     * Re-adds a Unit to the pools by their Glyph(s)
     */
    public void reincarnate(Unit u) {
        this.add(u.name, u.glyphs.get());
    }

    /**
     * Removes a Unit from the pools by their Glyph(s)
     */
    public void remove(Unit u) {
        Glyph[] glyphs = u.glyphs.get();
        for (int a = 0; a < glyphs.length; a++) {
            this.pools.get(glyphs[a]).remove(u.name);
        }
    }
}
